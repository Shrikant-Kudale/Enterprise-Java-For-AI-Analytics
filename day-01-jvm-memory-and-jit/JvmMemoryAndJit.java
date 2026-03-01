import java.lang.management.*;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Day 01 - JVM memory areas, escape analysis and JIT warm-up.
 *
 * <p>Run: {@code javac JvmMemoryAndJit.java && java JvmMemoryAndJit}
 * <p>Optional flags to explore: {@code -Xlog:gc -XX:+PrintCompilation -XX:-DoEscapeAnalysis}
 */
public class JvmMemoryAndJit {

    record Point(double x, double y) {}

    /** Allocation never leaves the loop -> C2 scalar-replaces it (zero heap allocation). */
    static double nonEscaping(int n) {
        double acc = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i, i + 1);
            acc += Math.sqrt(p.x() * p.x() + p.y() * p.y());
        }
        return acc;
    }

    static final Point[] SINK = new Point[1024];

    /** Reference is published to a static array -> object escapes -> real allocation. */
    static double escaping(int n) {
        double acc = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i, i + 1);
            SINK[i & 1023] = p;
            acc += Math.sqrt(p.x() * p.x() + p.y() * p.y());
        }
        return acc;
    }

    /** Shallow size estimate of an object (HotSpot 64-bit layout). */
    static long shallowSize(int refs, int longsOrDoubles, int ints, int bytes, boolean compressedOops) {
        int header = compressedOops ? 12 : 16;
        long raw = header + (long) refs * (compressedOops ? 4 : 8) + longsOrDoubles * 8L + ints * 4L + bytes;
        return (raw + 7) / 8 * 8;
    }

    static int depth;
    static void recurse() { depth++; recurse(); }

    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // ---- 1. Inspect memory areas via JMX ----------------------------------------------
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = mem.getHeapMemoryUsage();
        MemoryUsage nonHeap = mem.getNonHeapMemoryUsage();
        System.out.printf("Heap     used=%,d KB committed=%,d KB max=%,d KB%n",
                heap.getUsed() >> 10, heap.getCommitted() >> 10, heap.getMax() >> 10);
        System.out.printf("NonHeap  used=%,d KB (metaspace + code cache)%n", nonHeap.getUsed() >> 10);
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.printf("  pool %-30s type=%-8s used=%,d KB%n",
                    pool.getName(), pool.getType(), pool.getUsage().getUsed() >> 10);
        }
        check(heap.getCommitted() > 0 && heap.getMax() != 0 && nonHeap.getUsed() > 0, "memory beans must report usage");

        // ---- 2. Object layout arithmetic ---------------------------------------------------
        check(shallowSize(0, 2, 0, 0, true) == 32, "record Point(double,double) = 32 bytes");
        check(shallowSize(0, 0, 1, 0, true) == 16, "Integer = 16 bytes");
        check(shallowSize(1, 0, 0, 0, true) == 16, "object with one ref = 16 bytes");
        System.out.println("Layout: Point=32B, Integer=16B, int[] element=4B -> boxing costs 4x memory");

        // ---- 3. Stack depth ----------------------------------------------------------------
        try {
            recurse();
            check(false, "expected StackOverflowError");
        } catch (StackOverflowError expected) {
            System.out.println("StackOverflowError at depth ~" + depth);
            check(depth > 1_000, "stack should hold at least 1000 frames");
        }

        // ---- 4. Off-heap (direct) buffers --------------------------------------------------
        ByteBuffer direct = ByteBuffer.allocateDirect(16 << 20);
        check(direct.isDirect(), "buffer must be direct");
        long directUsed = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class).stream()
                .filter(b -> b.getName().equals("direct")).mapToLong(BufferPoolMXBean::getMemoryUsed).sum();
        check(directUsed >= (16 << 20), "direct pool must account for 16 MB");
        System.out.printf("Direct buffer pool used = %,d KB (outside the heap)%n", directUsed >> 10);

        // ---- 5. GC activity under allocation churn ----------------------------------------
        List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
        long before = gcs.stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();
        byte[] sinkArr = null;
        for (int i = 0; i < 3_000_000; i++) sinkArr = new byte[1024];
        long after = gcs.stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();
        System.out.printf("GC collectors=%s, collections during churn=%d (sink=%d)%n",
                gcs.stream().map(GarbageCollectorMXBean::getName).toList(), after - before, sinkArr.length);
        check(after >= before, "GC count cannot go backwards");

        // ---- 6. JIT warm-up + escape analysis benchmark ----------------------------------
        CompilationMXBean jit = ManagementFactory.getCompilationMXBean();
        long jitBefore = jit.getTotalCompilationTime();
        final int n = 2_000_000, rounds = 12;
        double reference = nonEscaping(n);
        System.out.println("Round |  non-escaping ms | escaping ms   (watch times drop as C2 kicks in)");
        long lastFast = 0, lastSlow = 0;
        for (int r = 1; r <= rounds; r++) {
            long t0 = System.nanoTime();
            double a = nonEscaping(n);
            long t1 = System.nanoTime();
            double b = escaping(n);
            long t2 = System.nanoTime();
            check(a == reference && b == reference, "results must be identical across rounds");
            lastFast = t1 - t0;
            lastSlow = t2 - t1;
            System.out.printf("%5d | %16.2f | %12.2f%n", r, lastFast / 1e6, lastSlow / 1e6);
        }
        System.out.printf("JIT time spent compiling during run: %d ms (compiler: %s)%n",
                jit.getTotalCompilationTime() - jitBefore, jit.getName());
        System.out.printf("Steady-state speedup from scalar replacement: %.2fx%n", (double) lastSlow / lastFast);

        System.out.println("Day 01 OK");
    }
}
