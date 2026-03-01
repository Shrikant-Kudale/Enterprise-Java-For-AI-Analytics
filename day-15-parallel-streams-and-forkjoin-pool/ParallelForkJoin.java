import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

/**
 * Day 15 - Fork/Join: RecursiveTask (parallel reduction), RecursiveAction (parallel merge sort),
 * parallel streams in a dedicated pool, threshold tuning and the classic pitfalls.
 *
 * <p>Run: {@code javac ParallelForkJoin.java && java ParallelForkJoin}
 */
public class ParallelForkJoin {

    /** Compute-bound kernel so that parallelism pays off (memory-bound sums barely scale). */
    static double kernel(double x) { return Math.sin(x) * Math.cos(x * 0.5) + Math.log1p(x); }

    static final class KernelSum extends RecursiveTask<Double> {
        private final double[] a; private final int lo, hi, threshold;
        KernelSum(double[] a, int lo, int hi, int threshold) { this.a = a; this.lo = lo; this.hi = hi; this.threshold = threshold; }
        @Override protected Double compute() {
            if (hi - lo <= threshold) { double s = 0; for (int i = lo; i < hi; i++) s += kernel(a[i]); return s; }
            int mid = (lo + hi) >>> 1;
            KernelSum left = new KernelSum(a, lo, mid, threshold);
            left.fork();                                            // run left asynchronously ...
            double right = new KernelSum(a, mid, hi, threshold).compute(); // ... and right in this thread
            return right + left.join();
        }
    }

    static final class ParallelMergeSort extends RecursiveAction {
        private static final int SEQ = 8_192;
        private final int[] a, tmp; private final int lo, hi;
        ParallelMergeSort(int[] a, int[] tmp, int lo, int hi) { this.a = a; this.tmp = tmp; this.lo = lo; this.hi = hi; }
        @Override protected void compute() {
            if (hi - lo <= SEQ) { Arrays.sort(a, lo, hi); return; }
            int mid = (lo + hi) >>> 1;
            invokeAll(new ParallelMergeSort(a, tmp, lo, mid), new ParallelMergeSort(a, tmp, mid, hi));
            int i = lo, j = mid, k = lo;
            while (i < mid && j < hi) tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
            while (i < mid) tmp[k++] = a[i++];
            while (j < hi) tmp[k++] = a[j++];
            System.arraycopy(tmp, lo, a, lo, hi - lo);
        }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }
    static boolean close(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.abs(a)); }

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("cores=" + cores + " commonPool parallelism=" + ForkJoinPool.commonPool().getParallelism());

        double[] data = new Random(3).doubles(6_000_000, 0, 1000).toArray();
        double sequential = 0;
        for (double x : data) sequential += kernel(x);

        // ---- RecursiveTask correctness
        ForkJoinPool pool = new ForkJoinPool(Math.max(2, cores));
        double viaTask = pool.invoke(new KernelSum(data, 0, data.length, 50_000));
        check(close(sequential, viaTask), "fork/join sum " + viaTask + " vs " + sequential);

        // ---- parallel merge sort
        int[] ints = new Random(4).ints(3_000_000).toArray();
        int[] expected = ints.clone(); Arrays.sort(expected);
        int[] work = ints.clone();
        pool.invoke(new ParallelMergeSort(work, new int[work.length], 0, work.length));
        check(Arrays.equals(work, expected), "parallel merge sort");

        // ---- parallel stream in a dedicated pool uses that pool's threads, not the common pool
        Set<String> threadNames = ConcurrentHashMap.newKeySet();
        ForkJoinPool custom = new ForkJoinPool(4);
        long count = custom.submit(() -> IntStream.range(0, 100_000).parallel().peek(i -> threadNames.add(Thread.currentThread().getName())).filter(i -> i % 3 == 0).count()).get();
        check(count == 33_334, "count " + count);
        check(threadNames.stream().noneMatch(n -> n.contains("commonPool")), "must not use common pool: " + threadNames);
        System.out.println("custom pool threads used: " + threadNames.stream().sorted().toList());
        custom.shutdown();

        // ---- pitfall 1: shared mutable state
        List<Integer> unsafe = new ArrayList<>();
        String outcome;
        try { IntStream.range(0, 200_000).parallel().forEach(unsafe::add); outcome = "size=" + unsafe.size() + " (expected 200000)"; }
        catch (RuntimeException e) { outcome = "threw " + e.getClass().getSimpleName(); }
        System.out.println("pitfall forEach(list::add): " + outcome);
        List<Integer> safe = IntStream.range(0, 200_000).parallel().boxed().collect(Collectors.toList());
        check(safe.size() == 200_000 && safe.get(199_999) == 199_999, "collect is safe and ordered");

        // ---- pitfall 2: non-associative reduce / bad identity
        int seqSub = IntStream.rangeClosed(1, 1_000).boxed().reduce(0, (a, b) -> a - b);
        int parSub = IntStream.rangeClosed(1, 1_000).boxed().parallel().reduce(0, (a, b) -> a - b);
        int badIdentity = IntStream.rangeClosed(1, 1_000).parallel().reduce(10, Integer::sum);
        System.out.printf("pitfall reduce: seq(a-b)=%d par(a-b)=%d | sum with identity 10 -> %d (true sum 500500)%n", seqSub, parSub, badIdentity);
        check(seqSub == -500_500, "sequential subtraction reduce");
        check(IntStream.rangeClosed(1, 1_000).parallel().sum() == 500_500, "associative sum is deterministic");
        check(badIdentity >= 500_500 + 10, "identity is added once per partition (>= 1 extra)");

        // ---- pitfall 3: ordering
        AtomicInteger order = new AtomicInteger();
        List<Integer> ordered = Collections.synchronizedList(new ArrayList<>());
        IntStream.range(0, 1_000).parallel().forEachOrdered(ordered::add);
        for (int i = 0; i < 1_000; i++) check(ordered.get(i) == i, "forEachOrdered keeps encounter order");

        // ---- benchmark: threshold tuning (the 'sweet spot')
        System.out.println("Threshold sweep for compute-bound sum of 6M elements (best of 3):");
        for (int w = 0; w < 2; w++) pool.invoke(new KernelSum(data, 0, data.length, 50_000));
        double tSeq = best(() -> { double s = 0; for (double x : data) s += kernel(x); return s; });
        System.out.printf("  sequential loop            %8.1f ms%n", tSeq);
        for (int threshold : new int[]{10, 1_000, 10_000, 100_000, 1_000_000, 6_000_000}) {
            double t = best(() -> pool.invoke(new KernelSum(data, 0, data.length, threshold)));
            System.out.printf("  threshold %-10d       %8.1f ms  speed-up %.2fx%n", threshold, t, tSeq / t);
        }
        double tStream = best(() -> Arrays.stream(data).parallel().map(ParallelForkJoin::kernel).sum());
        double tStreamSeq = best(() -> Arrays.stream(data).map(ParallelForkJoin::kernel).sum());
        System.out.printf("  DoubleStream sequential    %8.1f ms | parallel %.1f ms%n", tStreamSeq, tStream);
        double tSort = best(() -> { int[] c = ints.clone(); Arrays.sort(c); return c[0]; });
        double tPSort = best(() -> { int[] c = ints.clone(); Arrays.parallelSort(c); return c[0]; });
        double tMySort = best(() -> { int[] c = ints.clone(); pool.invoke(new ParallelMergeSort(c, new int[c.length], 0, c.length)); return c[0]; });
        System.out.printf("  sort 3M ints: Arrays.sort %.1f ms | parallelSort %.1f ms | our ParallelMergeSort %.1f ms%n", tSort, tPSort, tMySort);
        pool.shutdown();
        System.out.println("Day 15 OK");
    }

    static volatile double SINK;
    static double best(Callable<Object> task) throws Exception {
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            long t0 = System.nanoTime();
            Object r = task.call();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
            SINK = r instanceof Number n ? n.doubleValue() : 0;
        }
        return best;
    }
}
