import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/** Day 17 - executors, rejection policies, scatter-gather with timeout/fallback, hedging. Run: javac ExecutorsAndFutures.java && java ExecutorsAndFutures */
public class ExecutorsAndFutures {
    record Replica(String name, long ms, boolean fail) {
        double predict(double x) throws Exception { Thread.sleep(ms); if (fail) throw new IllegalStateException(name + " down"); return x * 2; }
    }
    record Agg(double mean, int ok, int failed) {}

    static ThreadFactory named(String p) { AtomicInteger n = new AtomicInteger(); return r -> new Thread(r, p + "-" + n.incrementAndGet()); }

    static CompletableFuture<Double> call(Replica r, double x, Executor ex) {
        return CompletableFuture.supplyAsync(() -> { try { return r.predict(x); } catch (Exception e) { throw new CompletionException(e); } }, ex);
    }

    static Agg scatterGather(List<Replica> rs, double x, Executor ex, long timeoutMs) {
        List<CompletableFuture<Double>> fs = rs.stream()
                .map(r -> call(r, x, ex).completeOnTimeout(Double.NaN, timeoutMs, TimeUnit.MILLISECONDS).exceptionally(t -> Double.NaN)).toList();
        CompletableFuture.allOf(fs.toArray(new CompletableFuture[0])).join();
        double[] ok = fs.stream().mapToDouble(CompletableFuture::join).filter(v -> !Double.isNaN(v)).toArray();
        return new Agg(ok.length == 0 ? Double.NaN : Arrays.stream(ok).average().getAsDouble(), ok.length, rs.size() - ok.length);
    }

    /** Hedged request: first successful replica wins. */
    static CompletableFuture<Double> firstSuccess(List<Replica> rs, double x, Executor ex) {
        CompletableFuture<Double> result = new CompletableFuture<>();
        AtomicInteger failures = new AtomicInteger();
        for (Replica r : rs) call(r, x, ex).whenComplete((v, t) -> {
            if (t == null) result.complete(v);
            else if (failures.incrementAndGet() == rs.size()) result.completeExceptionally(t);
        });
        return result;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        // rejection: 2 running + 2 queued, 5th is rejected
        CountDownLatch release = new CountDownLatch(1);
        ThreadPoolExecutor abort = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), named("infer"), new ThreadPoolExecutor.AbortPolicy());
        Runnable blocker = () -> { try { release.await(); } catch (InterruptedException ignored) { } };
        for (int i = 0; i < 4; i++) abort.execute(blocker);
        try { abort.execute(() -> { }); check(false, "5th task must be rejected"); } catch (RejectedExecutionException expected) { }
        release.countDown(); abort.shutdown(); check(abort.awaitTermination(5, TimeUnit.SECONDS), "drain");

        // CallerRuns = back-pressure: overflow task runs on the submitting thread
        CountDownLatch rel2 = new CountDownLatch(1);
        ThreadPoolExecutor cr = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), named("cr"), new ThreadPoolExecutor.CallerRunsPolicy());
        Runnable blocker2 = () -> { try { rel2.await(); } catch (InterruptedException ignored) { } };
        cr.execute(blocker2); cr.execute(blocker2);
        AtomicReference<String> ranOn = new AtomicReference<>();
        cr.execute(() -> ranOn.set(Thread.currentThread().getName()));
        check(ranOn.get().equals(Thread.currentThread().getName()), "overflow ran on caller: " + ranOn.get());
        rel2.countDown(); cr.shutdown();

        // named threads + exception capture
        ExecutorService pool = Executors.newFixedThreadPool(2, named("infer"));
        check(pool.submit(() -> Thread.currentThread().getName()).get().startsWith("infer-"), "thread name");
        Future<?> bad = pool.submit(() -> { throw new IllegalStateException("boom"); });
        try { bad.get(); check(false, "must throw"); } catch (ExecutionException e) { check(e.getCause().getMessage().equals("boom"), "cause"); }
        check(pool.invokeAny(List.of(() -> { Thread.sleep(300); return "slow"; }, () -> { Thread.sleep(20); return "fast"; })).equals("fast"), "invokeAny");
        pool.shutdown();

        // scatter-gather: 2 healthy, 1 failing, 1 too slow
        ExecutorService ex = Executors.newFixedThreadPool(8, named("sg"));
        List<Replica> rs = List.of(new Replica("A", 20, false), new Replica("B", 30, false), new Replica("C", 10, true), new Replica("D", 800, false));
        long t0 = System.nanoTime();
        Agg agg = scatterGather(rs, 3, ex, 150);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check(agg.ok() == 2 && agg.failed() == 2 && agg.mean() == 6.0, agg.toString());
        check(ms < 600, "timeout must bound latency, took " + ms);

        // hedging
        double v = firstSuccess(List.of(new Replica("slow", 300, false), new Replica("dead", 5, true), new Replica("mid", 40, false)), 5, ex).get(2, TimeUnit.SECONDS);
        check(v == 10.0, "hedged result");
        try { firstSuccess(List.of(new Replica("x", 5, true), new Replica("y", 5, true)), 1, ex).get(2, TimeUnit.SECONDS); check(false, "all fail"); } catch (ExecutionException ok) { }
        check(CompletableFuture.supplyAsync(() -> 2).thenCompose(a -> CompletableFuture.supplyAsync(() -> a * 10)).thenCombine(CompletableFuture.supplyAsync(() -> 5), Integer::sum).get() == 25, "compose/combine");

        // benchmark: sequential vs scatter-gather over 8 replicas x 50 ms
        List<Replica> eight = new ArrayList<>();
        for (int i = 0; i < 8; i++) eight.add(new Replica("r" + i, 50, false));
        long s0 = System.nanoTime(); for (Replica r : eight) r.predict(1); long seq = (System.nanoTime() - s0) / 1_000_000;
        long p0 = System.nanoTime(); scatterGather(eight, 1, ex, 1_000); long par = (System.nanoTime() - p0) / 1_000_000;
        System.out.printf("8 x 50ms replicas: sequential %d ms | scatter-gather %d ms%n", seq, par);
        check(par < seq / 2, "parallel must be much faster");
        ex.shutdown();
        System.out.println("Day 17 OK");
    }
}
