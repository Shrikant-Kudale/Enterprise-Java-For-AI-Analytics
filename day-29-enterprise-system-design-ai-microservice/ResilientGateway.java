import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/** Day 29 - resilient inference gateway built from small, deterministic components. Run: javac ResilientGateway.java && java ResilientGateway */
public class ResilientGateway {
    static final class TokenBucket {
        private double tokens; private final double capacity, perSecond; private long last; private final LongSupplier clock;
        TokenBucket(double capacity, double perSecond, LongSupplier clock) { this.capacity = capacity; this.perSecond = perSecond; this.clock = clock; tokens = capacity; last = clock.getAsLong(); }
        synchronized boolean tryAcquire() { long now = clock.getAsLong(); tokens = Math.min(capacity, tokens + (now - last) * perSecond / 1000.0); last = now; if (tokens >= 1) { tokens--; return true; } return false; }
    }
    enum State { CLOSED, OPEN, HALF_OPEN }
    static final class CircuitOpenException extends RuntimeException { }
    static final class CircuitBreaker {
        State state = State.CLOSED; int failures; long openedAt; final int threshold; final long openMs; final LongSupplier clock;
        CircuitBreaker(int threshold, long openMs, LongSupplier clock) { this.threshold = threshold; this.openMs = openMs; this.clock = clock; }
        synchronized <T> T call(Supplier<T> action) {
            if (state == State.OPEN) { if (clock.getAsLong() - openedAt >= openMs) state = State.HALF_OPEN; else throw new CircuitOpenException(); }
            try { T r = action.get(); failures = 0; state = State.CLOSED; return r; }
            catch (RuntimeException e) { if (state == State.HALF_OPEN || ++failures >= threshold) { state = State.OPEN; openedAt = clock.getAsLong(); } throw e; }
        }
    }
    static <T> T retry(Supplier<T> action, int attempts, long baseMs, LongConsumer sleeper) {
        RuntimeException last = null;
        for (int i = 0; i < attempts; i++) {
            try { return action.get(); } catch (CircuitOpenException e) { throw e; } catch (RuntimeException e) { last = e; if (i < attempts - 1) sleeper.accept(baseMs << i); }
        }
        throw last;
    }
    record Response(int status, double value, String source) { }

    static final class Downstream {
        final AtomicBoolean down = new AtomicBoolean(); final AtomicInteger calls = new AtomicInteger();
        double predict(double x) { calls.incrementAndGet(); if (down.get()) throw new IllegalStateException("model down"); return x * 2; }
    }
    static final class Gateway {
        final TokenBucket bucket; final Semaphore bulkhead; final CircuitBreaker breaker; final Downstream ds; final Map<Double, Double> cache = new ConcurrentHashMap<>();
        Gateway(TokenBucket b, Semaphore s, CircuitBreaker cb, Downstream d) { bucket = b; bulkhead = s; breaker = cb; ds = d; }
        Response predict(double x) {
            if (!bucket.tryAcquire()) return new Response(429, Double.NaN, "rate-limit");
            Double hit = cache.get(x);
            if (hit != null) return new Response(200, hit, "cache");
            if (!bulkhead.tryAcquire()) return new Response(503, Double.NaN, "bulkhead");
            try {
                double v = retry(() -> breaker.call(() -> ds.predict(x)), 3, 10, ms -> { });
                cache.put(x, v);
                return new Response(200, v, "model");
            } catch (RuntimeException e) { return new Response(200, 0.0, "fallback"); }
            finally { bulkhead.release(); }
        }
    }

    static int replicas(double qps, double p99Seconds, double perReplica, double utilisation) { return (int) Math.ceil(qps * p99Seconds / perReplica / utilisation); }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        AtomicLong now = new AtomicLong(0);
        TokenBucket tb = new TokenBucket(5, 0, now::get);
        int ok = 0; for (int i = 0; i < 6; i++) if (tb.tryAcquire()) ok++;
        check(ok == 5, "bucket of 5 rejects the 6th");

        Downstream ds = new Downstream();
        Gateway gw = new Gateway(new TokenBucket(1_000, 0, now::get), new Semaphore(2), new CircuitBreaker(3, 1_000, now::get), ds);
        check(gw.predict(1).source().equals("model") && gw.predict(1).source().equals("cache") && ds.calls.get() == 1, "cache absorbs repeats");

        ds.down.set(true);
        for (int i = 10; i < 14; i++) check(gw.predict(i).source().equals("fallback"), "fallback while down");
        int callsWhenOpen = ds.calls.get();
        check(gw.breaker.state == State.OPEN, "breaker OPEN after repeated failures");
        gw.predict(99);
        check(ds.calls.get() == callsWhenOpen, "OPEN breaker fails fast without touching the downstream");

        ds.down.set(false); now.set(2_000);
        check(gw.predict(100).source().equals("model") && gw.breaker.state == State.CLOSED, "HALF_OPEN trial succeeds -> CLOSED");

        Gateway tight = new Gateway(new TokenBucket(100, 0, now::get), new Semaphore(2), new CircuitBreaker(3, 1_000, now::get), new Downstream());
        tight.bulkhead.tryAcquire(); tight.bulkhead.tryAcquire();
        check(tight.predict(5).status() == 503, "bulkhead full -> 503");

        List<Long> delays = new ArrayList<>(); int[] attempts = {0};
        double v = retry(() -> { if (++attempts[0] < 3) throw new IllegalStateException("flaky"); return 7.0; }, 5, 100, delays::add);
        check(v == 7.0 && delays.equals(List.of(100L, 200L)), "exponential backoff " + delays);

        check(replicas(5_000, 0.04, 8, 0.6) == 42, "capacity estimate = 42 replicas");
        System.out.println("capacity: 5000 QPS x 40 ms p99, 8 concurrent/replica, 60% util => " + replicas(5_000, 0.04, 8, 0.6) + " replicas");
        System.out.println("Day 29 OK");
    }
}
