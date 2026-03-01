import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Day 03 - Design patterns for an AI inference backend.
 * Strategy, Decorator, Factory, Builder, Observer and Chain of Responsibility working together.
 *
 * <p>Run: {@code javac DesignPatternsForAiBackends.java && java DesignPatternsForAiBackends}
 */
public class DesignPatternsForAiBackends {

    // ------------------------------------------------------------------ domain
    interface Model {
        String name();
        double[] predict(double[] features);
    }

    record LinearModel(String name, double[] weights, double bias) implements Model {
        public double[] predict(double[] x) {
            if (x.length != weights.length) throw new IllegalArgumentException("expected " + weights.length + " features");
            double s = bias;
            for (int i = 0; i < weights.length; i++) s += weights[i] * x[i];
            return new double[]{1.0 / (1.0 + Math.exp(-s))};
        }
    }

    record ConstantModel(String name, double value) implements Model {
        public double[] predict(double[] x) { return new double[]{value}; }
    }

    /** Test double that counts real invocations. */
    static final class CountingModel implements Model {
        final AtomicInteger calls = new AtomicInteger();
        private final Model inner;
        CountingModel(Model inner) { this.inner = inner; }
        public String name() { return inner.name(); }
        public double[] predict(double[] x) { calls.incrementAndGet(); return inner.predict(x); }
    }

    // ------------------------------------------------------------------ DECORATORS
    static final class CachingModel implements Model {
        private final Model delegate;
        private final Map<List<Double>, double[]> cache = new ConcurrentHashMap<>();
        final LongAdder hits = new LongAdder();
        CachingModel(Model delegate) { this.delegate = delegate; }
        public String name() { return delegate.name() + "+cache"; }
        public double[] predict(double[] x) {
            List<Double> key = Arrays.stream(x).boxed().toList();
            double[] cached = cache.get(key);
            if (cached != null) { hits.increment(); return cached.clone(); }
            double[] out = delegate.predict(x);
            cache.put(key, out.clone());
            return out;
        }
    }

    static final class MeteredModel implements Model {
        private final Model delegate;
        final LongAdder calls = new LongAdder(), nanos = new LongAdder();
        MeteredModel(Model delegate) { this.delegate = delegate; }
        public String name() { return delegate.name(); }
        public double[] predict(double[] x) {
            long t0 = System.nanoTime();
            try { return delegate.predict(x); }
            finally { calls.increment(); nanos.add(System.nanoTime() - t0); }
        }
    }

    // ------------------------------------------------------------------ FACTORY
    static Model create(String type, Map<String, Object> cfg) {
        return switch (type) {
            case "linear" -> new LinearModel((String) cfg.get("name"), (double[]) cfg.get("weights"), (double) cfg.get("bias"));
            case "constant" -> new ConstantModel((String) cfg.get("name"), (double) cfg.get("value"));
            default -> throw new IllegalArgumentException("unknown model type: " + type);
        };
    }

    // ------------------------------------------------------------------ BUILDER
    record InferenceRequest(String tenant, double[] features, long timeoutMillis) {
        static Builder builder() { return new Builder(); }
        static final class Builder {
            private String tenant;
            private double[] features;
            private long timeoutMillis = 1_000;
            Builder tenant(String t) { this.tenant = t; return this; }
            Builder features(double... f) { this.features = f.clone(); return this; }
            Builder timeoutMillis(long ms) { this.timeoutMillis = ms; return this; }
            InferenceRequest build() {
                if (tenant == null || tenant.isBlank()) throw new IllegalStateException("tenant required");
                if (features == null || features.length == 0) throw new IllegalStateException("features required");
                if (timeoutMillis <= 0) throw new IllegalStateException("timeout must be positive");
                return new InferenceRequest(tenant, features, timeoutMillis);
            }
        }
    }

    // ------------------------------------------------------------------ STRATEGY
    interface RoutingStrategy { Model choose(List<Model> candidates, InferenceRequest req); }

    static final class RoundRobin implements RoutingStrategy {
        private final AtomicInteger counter = new AtomicInteger();
        public Model choose(List<Model> c, InferenceRequest r) { return c.get(Math.floorMod(counter.getAndIncrement(), c.size())); }
    }

    static final class TenantAffinity implements RoutingStrategy {
        public Model choose(List<Model> c, InferenceRequest r) { return c.get(Math.floorMod(r.tenant().hashCode(), c.size())); }
    }

    // ------------------------------------------------------------------ OBSERVER
    static final class EventBus<T> {
        private final List<Consumer<T>> listeners = new java.util.concurrent.CopyOnWriteArrayList<>();
        Runnable subscribe(Consumer<T> l) { listeners.add(l); return () -> listeners.remove(l); }
        void publish(T event) { listeners.forEach(l -> l.accept(event)); }
    }
    record PredictionEvent(String tenant, String model, double score) {}

    // ------------------------------------------------------------------ CHAIN OF RESPONSIBILITY
    static Optional<String> validate(InferenceRequest r, List<Function<InferenceRequest, Optional<String>>> chain) {
        for (var handler : chain) {
            Optional<String> failure = handler.apply(r);
            if (failure.isPresent()) return failure; // short-circuit
        }
        return Optional.empty();
    }

    // ------------------------------------------------------------------ ROUTER (ties everything together)
    static final class ModelRouter {
        private final List<Model> models;
        private final RoutingStrategy strategy;
        private final EventBus<PredictionEvent> bus;
        ModelRouter(List<Model> models, RoutingStrategy strategy, EventBus<PredictionEvent> bus) {
            this.models = List.copyOf(models); this.strategy = strategy; this.bus = bus;
        }
        double[] predict(InferenceRequest req) {
            Model m = strategy.choose(models, req);
            double[] out = m.predict(req.features());
            bus.publish(new PredictionEvent(req.tenant(), m.name(), out[0]));
            return out;
        }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // factory
        Model lin = create("linear", Map.of("name", "lin", "weights", new double[]{1, -1}, "bias", 0.0));
        check(Math.abs(lin.predict(new double[]{0, 0})[0] - 0.5) < 1e-12, "sigmoid(0)=0.5");
        try { create("nope", Map.of()); check(false, "must throw"); } catch (IllegalArgumentException ok) { }

        // decorator stack: Metered(Caching(Counting(base)))
        CountingModel base = new CountingModel(lin);
        CachingModel caching = new CachingModel(base);
        MeteredModel stack = new MeteredModel(caching);
        for (int i = 0; i < 5; i++) stack.predict(new double[]{1, 2});
        check(base.calls.get() == 1, "cache must absorb repeats");
        check(caching.hits.sum() == 4 && stack.calls.sum() == 5, "metrics");

        // builder + chain
        try { InferenceRequest.builder().tenant("t").build(); check(false, "must throw"); } catch (IllegalStateException ok) { }
        InferenceRequest req = InferenceRequest.builder().tenant("acme").features(1, 2).timeoutMillis(50).build();
        List<Function<InferenceRequest, Optional<String>>> chain = List.of(
                r -> r.features().length > 8 ? Optional.of("too many features") : Optional.empty(),
                r -> r.timeoutMillis() > 10_000 ? Optional.of("timeout too large") : Optional.empty());
        check(validate(req, chain).isEmpty(), "valid request");
        check(validate(InferenceRequest.builder().tenant("a").features(new double[9]).build(), chain)
                .orElse("").equals("too many features"), "chain short-circuits");

        // strategy + observer
        List<Model> replicas = List.of(new ConstantModel("A", 0.1), new ConstantModel("B", 0.2), new ConstantModel("C", 0.3));
        EventBus<PredictionEvent> bus = new EventBus<>();
        Map<String, Integer> perModel = new TreeMap<>();
        Runnable unsubscribe = bus.subscribe(e -> perModel.merge(e.model(), 1, Integer::sum));
        ModelRouter rr = new ModelRouter(replicas, new RoundRobin(), bus);
        for (int i = 0; i < 300; i++) rr.predict(req);
        check(perModel.equals(Map.of("A", 100, "B", 100, "C", 100)), "round robin balance " + perModel);
        unsubscribe.run();
        rr.predict(req);
        check(perModel.get("A") == 100, "unsubscribed listener must not fire");

        ModelRouter affinity = new ModelRouter(replicas, new TenantAffinity(), bus);
        String first = affinity.predict(req)[0] + "";
        for (int i = 0; i < 20; i++) check((affinity.predict(req)[0] + "").equals(first), "sticky routing");

        // benchmark: decorator overhead
        Model direct = lin;
        Model decorated = new MeteredModel(new MeteredModel(new MeteredModel(lin)));
        double[] x = {0.3, 0.4};
        double sink = 0;
        for (int w = 0; w < 3_000_000; w++) { sink += direct.predict(x)[0] + decorated.predict(x)[0]; }
        int n = 5_000_000;
        long t0 = System.nanoTime(); for (int i = 0; i < n; i++) sink += direct.predict(x)[0];
        long t1 = System.nanoTime(); for (int i = 0; i < n; i++) sink += decorated.predict(x)[0];
        long t2 = System.nanoTime();
        System.out.printf("direct %.1f ns/call | 3 decorators %.1f ns/call (sink=%.1f)%n",
                (t1 - t0) / (double) n, (t2 - t1) / (double) n, sink);
        System.out.println("Day 03 OK");
    }
}
