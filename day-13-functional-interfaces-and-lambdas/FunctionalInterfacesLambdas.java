import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Day 13 - Functional interfaces and lambdas for an ML feature pipeline:
 * custom interfaces, composition, currying, memoisation, lazy values, checked-exception adapters,
 * lambda-vs-anonymous semantics and call-site (mono vs megamorphic) benchmarks.
 *
 * <p>Run: {@code javac FunctionalInterfacesLambdas.java && java FunctionalInterfacesLambdas}
 */
public class FunctionalInterfacesLambdas {

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
        default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) { return (a, b, c) -> after.apply(apply(a, b, c)); }
    }

    @FunctionalInterface
    interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
        static <T, R, E extends Exception> Function<T, R> unchecked(ThrowingFunction<T, R, E> f) {
            return t -> { try { return f.apply(t); } catch (Exception e) { throw new RuntimeException(e); } };
        }
    }

    static <A, B, C> Function<A, Function<B, C>> curry(BiFunction<A, B, C> f) { return a -> b -> f.apply(a, b); }

    static <T, R> Function<T, R> memoize(Function<T, R> f) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return t -> cache.computeIfAbsent(t, f);
    }

    /** Thread-safe lazy value (double-checked locking with volatile). */
    static final class Lazy<T> implements Supplier<T> {
        private final Supplier<T> init;
        private volatile T value;
        Lazy(Supplier<T> init) { this.init = init; }
        public T get() {
            T v = value;
            if (v == null) synchronized (this) { v = value; if (v == null) value = v = init.get(); }
            return v;
        }
    }

    // ---------------------------------------------------------------- feature pipeline stages
    static UnaryOperator<double[]> standardize(double mean, double std) { return x -> { double[] o = new double[x.length]; for (int i = 0; i < o.length; i++) o[i] = (x[i] - mean) / std; return o; }; }
    static UnaryOperator<double[]> clip(double lo, double hi) { return x -> { double[] o = x.clone(); for (int i = 0; i < o.length; i++) o[i] = Math.max(lo, Math.min(hi, o[i])); return o; }; }
    static UnaryOperator<double[]> log1p() { return x -> { double[] o = x.clone(); for (int i = 0; i < o.length; i++) o[i] = Math.log1p(Math.abs(o[i])) * Math.signum(o[i]); return o; }; }

    @SafeVarargs
    static Function<double[], double[]> pipeline(UnaryOperator<double[]>... stages) {
        Function<double[], double[]> p = Function.identity();
        for (UnaryOperator<double[]> s : stages) p = p.andThen(s);
        return p;
    }

    // ---------------------------------------------------------------- this-semantics demo
    static final class Owner {
        final String name = "owner";
        Supplier<String> lambdaThis() { return () -> this.toString(); }
        Supplier<String> anonymousThis() { return new Supplier<>() { public String get() { return this.toString(); } @Override public String toString() { return "anonymous"; } }; }
        @Override public String toString() { return name; }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // ---- custom functional interfaces
        TriFunction<Integer, Integer, Integer, Integer> volume = (a, b, c) -> a * b * c;
        check(volume.andThen(v -> "v=" + v).apply(2, 3, 4).equals("v=24"), "TriFunction.andThen");
        Function<String, Integer> parse = ThrowingFunction.unchecked(s -> { if (s.isEmpty()) throw new java.io.IOException("empty"); return Integer.parseInt(s); });
        check(parse.apply("42") == 42, "unchecked wrapper happy path");
        try { parse.apply(""); check(false, "must throw"); } catch (RuntimeException e) { check(e.getCause() instanceof java.io.IOException, "cause preserved"); }

        // ---- currying and composition
        Function<Integer, Function<Integer, Integer>> add = curry(Integer::sum);
        Function<Integer, Integer> add5 = add.apply(5);
        check(add5.apply(10) == 15, "partial application");
        Function<Integer, Integer> doubleThenAdd = add5.compose((Integer x) -> x * 2);   // add5(x*2)
        check(doubleThenAdd.apply(10) == 25 && add5.andThen(x -> x * 2).apply(10) == 30, "compose vs andThen");
        Predicate<String> longish = s -> s.length() > 3, hasA = s -> s.contains("a");
        check(longish.and(hasA).test("data") && !longish.and(hasA).test("ab") && longish.or(hasA).test("ab") && Predicate.not(longish).test("ab"), "predicate algebra");

        // ---- four kinds of method references
        Function<String, Integer> staticRef = Integer::parseInt;
        String prefix = "ml-";
        Function<String, String> boundRef = prefix::concat;
        ToIntFunction<String> unboundRef = String::length;
        Supplier<List<String>> ctorRef = ArrayList::new;
        check(staticRef.apply("7") == 7 && boundRef.apply("x").equals("ml-x") && unboundRef.applyAsInt("four") == 4 && ctorRef.get().isEmpty(), "method refs");

        // ---- memoisation: expensive function evaluated once per key
        AtomicInteger evaluations = new AtomicInteger();
        Function<Integer, Long> slowSquare = memoize(n -> { evaluations.incrementAndGet(); return (long) n * n; });
        for (int i = 0; i < 100; i++) slowSquare.apply(i % 10);
        check(evaluations.get() == 10, "memoize evaluates each key once");

        // ---- lazy value
        AtomicInteger inits = new AtomicInteger();
        Lazy<String> lazy = new Lazy<>(() -> { inits.incrementAndGet(); return "model-loaded"; });
        check(inits.get() == 0, "not initialised yet");
        Thread[] ts = new Thread[8];
        for (int i = 0; i < ts.length; i++) { ts[i] = new Thread(lazy::get); ts[i].start(); }
        for (Thread t : ts) { try { t.join(); } catch (InterruptedException e) { throw new RuntimeException(e); } }
        check(inits.get() == 1 && lazy.get().equals("model-loaded"), "initialised exactly once under contention");

        // ---- feature pipeline built by composition
        Function<double[], double[]> p = pipeline(standardize(10, 2), clip(-3, 3), log1p());
        double[] out = p.apply(new double[]{10, 12, 100, -50});
        check(Math.abs(out[0]) < 1e-12 && Math.abs(out[1] - Math.log1p(1)) < 1e-12, "standardize + log1p");
        check(Math.abs(out[2] - Math.log1p(3)) < 1e-12 && Math.abs(out[3] + Math.log1p(3)) < 1e-12, "clip then log");

        // ---- capturing a mutable holder + effectively-final rule
        AtomicInteger counter = new AtomicInteger();
        Stream.of(1, 2, 3).forEach(x -> counter.addAndGet(x));
        check(counter.get() == 6, "mutation through holder");

        // ---- 'this' semantics
        Owner o = new Owner();
        check(o.lambdaThis().get().equals("owner") && o.anonymousThis().get().equals("anonymous"), "lambda this = enclosing, anonymous this = itself");

        // ---- laziness of Optional.orElseGet
        AtomicInteger fallbackCalls = new AtomicInteger();
        Optional.of("x").orElseGet(() -> { fallbackCalls.incrementAndGet(); return "y"; });
        Optional.of("x").orElse(computeFallback(fallbackCalls));
        check(fallbackCalls.get() == 1, "orElse evaluates eagerly, orElseGet lazily");

        // ---- benchmark: monomorphic vs megamorphic functional call sites, lambda vs anonymous vs direct
        IntUnaryOperator lam = x -> x * 31 + 7;
        IntUnaryOperator anon = new IntUnaryOperator() { public int applyAsInt(int x) { return x * 31 + 7; } };
        IntUnaryOperator[] mega = {x -> x + 1, x -> x * 3, x -> x ^ 0x55, x -> x - 9};
        int n = 200_000_000;
        long sink = 0;
        for (int w = 0; w < 3; w++) { sink += run(lam, n / 10); sink += run(anon, n / 10); sink += runMega(mega, n / 10); sink += direct(n / 10); }
        long t0 = System.nanoTime(); sink += direct(n);
        long t1 = System.nanoTime(); sink += run(lam, n);
        long t2 = System.nanoTime(); sink += run(anon, n);
        long t3 = System.nanoTime(); sink += runMega(mega, n);
        long t4 = System.nanoTime();
        System.out.printf("per op (ns): direct %.2f | lambda %.2f | anonymous %.2f | 4 different lambdas at one call site (megamorphic) %.2f  (sink=%d)%n",
                (t1 - t0) / (double) n, (t2 - t1) / (double) n, (t3 - t2) / (double) n, (t4 - t3) / (double) n, sink);
        System.out.println("Day 13 OK");
    }

    static String computeFallback(AtomicInteger c) { c.incrementAndGet(); return "eager"; }
    static int direct(int n) { int acc = 1; for (int i = 0; i < n; i++) acc = acc * 31 + 7; return acc; }
    static int run(IntUnaryOperator f, int n) { int acc = 1; for (int i = 0; i < n; i++) acc = f.applyAsInt(acc); return acc; }
    static int runMega(IntUnaryOperator[] fs, int n) { int acc = 1; for (int i = 0; i < n; i++) acc = fs[i & 3].applyAsInt(acc); return acc; }
}
