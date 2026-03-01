import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/** Day 18 - virtual threads vs platform pools, semaphore limits, ThreadLocal, pinning. Run: javac VirtualThreadsLoom.java && java VirtualThreadsLoom */
public class VirtualThreadsLoom {
    static long run(ExecutorService ex, int tasks, int sleepMs, AtomicInteger done) throws Exception {
        List<Future<?>> fs = new ArrayList<>(tasks);
        long t0 = System.nanoTime();
        for (int i = 0; i < tasks; i++) fs.add(ex.submit(() -> { Thread.sleep(sleepMs); done.incrementAndGet(); return null; }));
        for (Future<?> f : fs) f.get();
        return (System.nanoTime() - t0) / 1_000_000;
    }
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        // identity
        boolean[] isVirtual = new boolean[1];
        Thread vt = Thread.ofVirtual().name("vt-", 0).start(() -> isVirtual[0] = Thread.currentThread().isVirtual());
        vt.join();
        check(isVirtual[0] && vt.isDaemon(), "virtual threads are daemon virtual threads");

        // 10k blocking tasks: virtual vs 200 platform threads
        AtomicInteger d1 = new AtomicInteger(), d2 = new AtomicInteger();
        long tVirtual, tFixed;
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) { tVirtual = run(ex, 10_000, 50, d1); }
        try (ExecutorService ex = Executors.newFixedThreadPool(200)) { tFixed = run(ex, 10_000, 50, d2); }
        check(d1.get() == 10_000 && d2.get() == 10_000, "all tasks completed");
        System.out.printf("10k tasks x 50ms sleep: virtual %d ms | fixed(200) %d ms%n", tVirtual, tFixed);
        check(tVirtual < tFixed, "virtual threads must win on blocking work");

        // 100k concurrent virtual threads
        AtomicInteger many = new AtomicInteger();
        long t0 = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100_000; i++) ex.submit(() -> { Thread.sleep(100); many.incrementAndGet(); return null; });
        }
        check(many.get() == 100_000, "100k virtual threads finished");
        System.out.printf("100k concurrent virtual threads: %d ms%n", (System.nanoTime() - t0) / 1_000_000);

        // limit concurrency to a scarce downstream with a Semaphore
        Semaphore permits = new Semaphore(50);
        AtomicInteger inFlight = new AtomicInteger(), maxSeen = new AtomicInteger();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 2_000; i++) ex.submit(() -> {
                permits.acquire();
                try { int n = inFlight.incrementAndGet(); maxSeen.accumulateAndGet(n, Math::max); Thread.sleep(5); inFlight.decrementAndGet(); }
                finally { permits.release(); }
                return null;
            });
        }
        check(maxSeen.get() <= 50 && maxSeen.get() > 1, "semaphore bound respected, max=" + maxSeen.get());

        // ThreadLocal isolation
        ThreadLocal<Integer> tl = new ThreadLocal<>();
        AtomicInteger bad = new AtomicInteger();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) { final int id = i; ex.submit(() -> { tl.set(id); Thread.sleep(2); if (tl.get() != id) bad.incrementAndGet(); return null; }); }
        }
        check(bad.get() == 0, "each virtual thread has its own ThreadLocal");

        // pinning: sleeping inside synchronized vs ReentrantLock
        long pinned, free;
        t0 = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) ex.submit(() -> { synchronized (new Object()) { Thread.sleep(20); } return null; });
        }
        pinned = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) ex.submit(() -> { ReentrantLock l = new ReentrantLock(); l.lock(); try { Thread.sleep(20); } finally { l.unlock(); } return null; });
        }
        free = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("100 x 20ms: sleep in synchronized (pins carrier) %d ms | in ReentrantLock %d ms%n", pinned, free);
        System.out.println("Day 18 OK");
    }
}
