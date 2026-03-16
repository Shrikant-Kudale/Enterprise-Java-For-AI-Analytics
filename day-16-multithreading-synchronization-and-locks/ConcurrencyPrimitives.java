import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;

/** Day 16 - atomicity, lock ordering, visibility and deadlock detection. Run: javac ConcurrencyPrimitives.java && java ConcurrencyPrimitives */
public class ConcurrencyPrimitives {
    static final int T = 4, N = 200_000;
    static int plain, syncd;
    static final Object LOCK = new Object();
    static volatile boolean running = true;

    static long hammer(Runnable inc) throws Exception {
        Thread[] ts = new Thread[T];
        for (int i = 0; i < T; i++) ts[i] = new Thread(() -> { for (int j = 0; j < N; j++) inc.run(); });
        long t0 = System.nanoTime();
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();
        return System.nanoTime() - t0;
    }

    static final class Account {
        final int id; long balance; final ReentrantLock lock = new ReentrantLock();
        Account(int id, long b) { this.id = id; this.balance = b; }
    }
    static void transfer(Account a, Account b, long amt) {
        Account first = a.id < b.id ? a : b, second = first == a ? b : a;   // global lock order
        first.lock.lock();
        try { second.lock.lock(); try { a.balance -= amt; b.balance += amt; } finally { second.lock.unlock(); } }
        finally { first.lock.unlock(); }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long expected = (long) T * N;
        AtomicLong atomic = new AtomicLong(); LongAdder adder = new LongAdder(); ReentrantLock rl = new ReentrantLock(); long[] boxed = {0};
        long tUnsafe = hammer(() -> plain++);
        long tSync = hammer(() -> { synchronized (LOCK) { syncd++; } });
        long tAtomic = hammer(atomic::incrementAndGet);
        long tAdder = hammer(adder::increment);
        long tLock = hammer(() -> { rl.lock(); try { boxed[0]++; } finally { rl.unlock(); } });
        check(syncd == expected && atomic.get() == expected && adder.sum() == expected && boxed[0] == expected, "locked counters must be exact");
        check(plain <= expected, "plain counter can only lose updates");
        System.out.printf("lost updates (plain int++): %d of %d%n", expected - plain, expected);
        System.out.printf("ms: unsafe %.0f | synchronized %.0f | AtomicLong %.0f | LongAdder %.0f | ReentrantLock %.0f%n",
                tUnsafe / 1e6, tSync / 1e6, tAtomic / 1e6, tAdder / 1e6, tLock / 1e6);

        // deadlock-free transfers
        Account[] acc = new Account[5];
        for (int i = 0; i < acc.length; i++) acc[i] = new Account(i, 1_000);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int t = 0; t < 4; t++) pool.submit(() -> {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            for (int i = 0; i < 50_000; i++) { int a = r.nextInt(5), b = r.nextInt(5); if (a != b) transfer(acc[a], acc[b], 1 + r.nextInt(10)); }
        });
        pool.shutdown();
        check(pool.awaitTermination(30, TimeUnit.SECONDS), "transfers must finish (no deadlock)");
        long total = 0; for (Account a : acc) total += a.balance;
        check(total == 5_000, "money conserved: " + total);

        // volatile visibility
        Thread spinner = new Thread(() -> { long c = 0; while (running) c++; });
        spinner.start(); Thread.sleep(50); running = false; spinner.join(2_000);
        check(!spinner.isAlive(), "volatile flag must stop the spinner");

        // deliberate deadlock, detected via ThreadMXBean
        Object l1 = new Object(), l2 = new Object();
        CountDownLatch both = new CountDownLatch(2);
        Thread a = new Thread(() -> { synchronized (l1) { both.countDown(); try { both.await(); } catch (InterruptedException e) { return; } synchronized (l2) { } } });
        Thread b = new Thread(() -> { synchronized (l2) { both.countDown(); try { both.await(); } catch (InterruptedException e) { return; } synchronized (l1) { } } });
        a.setDaemon(true); b.setDaemon(true); a.start(); b.start();
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = null;
        for (int i = 0; i < 60 && ids == null; i++) { Thread.sleep(50); ids = mx.findDeadlockedThreads(); }
        check(ids != null && ids.length == 2, "ThreadMXBean must report the 2-thread deadlock");
        System.out.println("Day 16 OK");
    }
}
