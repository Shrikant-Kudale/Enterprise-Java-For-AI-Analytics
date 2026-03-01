import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.*;

/** Day 20 - connection pool, N+1 vs batch fetch, JDBC batching and pool-size experiment. Run: javac PoolAndJpaPatterns.java && java PoolAndJpaPatterns */
public class PoolAndJpaPatterns {
    record User(long id, String name) { }
    record Order(long id, long userId, double amount) { }

    /** In-memory "database": every call is one network round trip with latency; limited server-side concurrency. */
    static final class FakeDatabase {
        final long rtNanos; final AtomicLong roundTrips = new AtomicLong(); final Semaphore serverSlots;
        final List<User> users = new ArrayList<>(); final Map<Long, List<Order>> orders = new HashMap<>(); final List<Order> inserted = new CopyOnWriteArrayList<>();
        FakeDatabase(long rtMicros, int serverConcurrency, int nUsers) {
            rtNanos = rtMicros * 1_000; serverSlots = new Semaphore(serverConcurrency);
            for (long u = 0; u < nUsers; u++) { users.add(new User(u, "u" + u)); orders.put(u, List.of(new Order(u * 10, u, 10), new Order(u * 10 + 1, u, 20))); }
        }
        private void roundTrip() { roundTrips.incrementAndGet(); serverSlots.acquireUninterruptibly(); try { LockSupport.parkNanos(rtNanos); } finally { serverSlots.release(); } }
        List<User> findUsers(int limit) { roundTrip(); return users.subList(0, limit); }
        List<Order> findOrdersByUser(long id) { roundTrip(); return orders.get(id); }
        Map<Long, List<Order>> findOrdersByUsers(Collection<Long> ids) { roundTrip(); return ids.stream().collect(Collectors.toMap(i -> i, orders::get)); }
        void insertOne(Order o) { roundTrip(); inserted.add(o); }
        void insertBatch(List<Order> os) { roundTrip(); inserted.addAll(os); }
        void query() { roundTrip(); }
    }

    static final class Conn implements AutoCloseable {
        final ConnectionPool pool; volatile long borrowedAt; private final AtomicBoolean closed = new AtomicBoolean(true);
        Conn(ConnectionPool p) { pool = p; }
        FakeDatabase db() { return pool.db; }
        @Override public void close() { if (closed.compareAndSet(false, true)) { pool.inUse.decrementAndGet(); pool.idle.offer(this); } }  // idempotent
    }
    static final class ConnectionPool {
        final FakeDatabase db; final BlockingQueue<Conn> idle; final AtomicInteger inUse = new AtomicInteger(), maxInUse = new AtomicInteger(); final List<Conn> all = new ArrayList<>();
        ConnectionPool(FakeDatabase db, int size) { this.db = db; idle = new ArrayBlockingQueue<>(size); for (int i = 0; i < size; i++) { Conn c = new Conn(this); all.add(c); idle.add(c); } }
        Conn borrow(long timeoutMs) throws TimeoutException, InterruptedException {
            Conn c = idle.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (c == null) throw new TimeoutException("connection not available after " + timeoutMs + " ms");
            c.closed.set(false); c.borrowedAt = System.nanoTime();
            maxInUse.accumulateAndGet(inUse.incrementAndGet(), Math::max);
            return c;
        }
        List<Conn> suspectedLeaks(long thresholdMs) { return all.stream().filter(c -> !c.closed.get() && (System.nanoTime() - c.borrowedAt) / 1_000_000 > thresholdMs).toList(); }
    }

    static int requiredConnections(double qps, double avgDbSeconds) { return (int) Math.ceil(qps * avgDbSeconds); }   // Little's law

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        // ---- N+1 vs batch fetch
        FakeDatabase db = new FakeDatabase(100, 8, 100);
        Map<Long, List<Order>> nPlusOne = new HashMap<>();
        for (User u : db.findUsers(100)) nPlusOne.put(u.id(), db.findOrdersByUser(u.id()));
        check(db.roundTrips.get() == 101, "N+1 => 101 round trips, got " + db.roundTrips.get());
        db.roundTrips.set(0);
        Map<Long, List<Order>> batched = db.findOrdersByUsers(db.findUsers(100).stream().map(User::id).toList());
        check(db.roundTrips.get() == 2 && batched.equals(nPlusOne), "join-fetch style => 2 round trips, same data");

        // ---- JDBC batching
        db.roundTrips.set(0);
        List<Order> rows = IntStream.range(0, 1_000).mapToObj(i -> new Order(i, i % 100, i)).toList();
        long t0 = System.nanoTime(); for (Order o : rows) db.insertOne(o); long tOne = System.nanoTime() - t0;
        check(db.roundTrips.get() == 1_000, "1000 single inserts");
        db.roundTrips.set(0);
        t0 = System.nanoTime(); for (int i = 0; i < rows.size(); i += 100) db.insertBatch(rows.subList(i, i + 100)); long tBatch = System.nanoTime() - t0;
        check(db.roundTrips.get() == 10, "batches of 100 => 10 round trips");
        System.out.printf("insert 1000 rows: one-by-one %.0f ms | batch(100) %.1f ms%n", tOne / 1e6, tBatch / 1e6);

        // ---- pool invariants
        FakeDatabase db2 = new FakeDatabase(1_000, 64, 1);
        ConnectionPool pool = new ConnectionPool(db2, 4);
        ExecutorService ex = Executors.newFixedThreadPool(16);
        List<Future<?>> fs = new ArrayList<>();
        for (int t = 0; t < 16; t++) fs.add(ex.submit(() -> { for (int i = 0; i < 20; i++) try (Conn c = pool.borrow(5_000)) { c.db().query(); } return null; }));
        for (Future<?> f : fs) f.get();
        check(pool.maxInUse.get() <= 4 && db2.roundTrips.get() == 320, "never more than 4 in use; max=" + pool.maxInUse.get());
        check(pool.idle.size() == 4, "all connections returned");
        Conn[] held = new Conn[4];
        for (int i = 0; i < 4; i++) held[i] = pool.borrow(100);
        try { pool.borrow(50); check(false, "exhausted pool must time out"); } catch (TimeoutException expected) { }
        Thread.sleep(30);
        check(pool.suspectedLeaks(20).size() == 4, "leak detector flags connections held too long");
        held[0].close(); held[0].close();                                   // double close must not duplicate
        for (int i = 1; i < 4; i++) held[i].close();
        check(pool.idle.size() == 4, "idempotent close");
        check(requiredConnections(2_000, 0.005) == 10, "Little's law: 2000 qps x 5 ms = 10 connections");

        // ---- pool-size experiment: DB saturates at 8 concurrent executions
        System.out.println("pool size -> throughput (400 queries x 2 ms, DB handles 8 concurrently)");
        for (int size : new int[]{1, 2, 4, 8, 16, 32}) {
            FakeDatabase d = new FakeDatabase(2_000, 8, 1);
            ConnectionPool p = new ConnectionPool(d, size);
            long s = System.nanoTime();
            try (ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 400; i++) vt.submit(() -> { try (Conn c = p.borrow(10_000)) { c.db().query(); } return null; });
            }
            double sec = (System.nanoTime() - s) / 1e9;
            System.out.printf("  size %2d : %6.0f queries/s%n", size, 400 / sec);
        }
        ex.shutdown();
        System.out.println("Day 20 OK");
    }
}
