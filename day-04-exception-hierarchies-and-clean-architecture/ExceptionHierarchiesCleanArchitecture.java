import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Day 04 - Sealed exception hierarchy, Result type and a ports-and-adapters scoring use case.
 *
 * <p>Run: {@code javac ExceptionHierarchiesCleanArchitecture.java && java ExceptionHierarchiesCleanArchitecture}
 */
public class ExceptionHierarchiesCleanArchitecture {

    // =========================================================== DOMAIN
    abstract static sealed class DomainException extends RuntimeException
            permits ValidationException, NotFoundException, UpstreamException {
        private final String code;
        protected DomainException(String code, String message, Throwable cause, boolean captureStack) {
            super(message, cause, true, captureStack); // writableStackTrace=false => cheap to create
            this.code = code;
        }
        String code() { return code; }
    }
    static final class ValidationException extends DomainException {
        ValidationException(String msg) { super("VALIDATION", msg, null, false); }
    }
    static final class NotFoundException extends DomainException {
        NotFoundException(String msg) { super("NOT_FOUND", msg, null, false); }
    }
    static final class UpstreamException extends DomainException {
        final boolean retryable;
        UpstreamException(String msg, Throwable cause, boolean retryable) {
            super("UPSTREAM", msg, cause, true);
            this.retryable = retryable;
        }
    }

    sealed interface Result<T> {
        record Ok<T>(T value) implements Result<T> {}
        record Err<T>(DomainException error) implements Result<T> {}

        static <T> Result<T> of(Supplier<T> action) {
            try { return new Ok<>(action.get()); }
            catch (DomainException e) { return new Err<>(e); }
        }
        default <R> Result<R> map(Function<T, R> f) {
            return switch (this) {
                case Ok<T> ok -> new Ok<>(f.apply(ok.value()));
                case Err<T> err -> new Err<>(err.error());
            };
        }
    }

    // ports (owned by the domain/application layer)
    interface FeatureStore { double[] load(String customerId); }
    interface ScoringModel { double score(double[] features); }

    /** Application use case: depends only on ports. */
    static final class ScoreCustomer {
        private final FeatureStore store;
        private final ScoringModel model;
        ScoreCustomer(FeatureStore store, ScoringModel model) { this.store = store; this.model = model; }
        Result<Double> execute(String customerId) {
            return Result.of(() -> {
                if (customerId == null || customerId.isBlank()) throw new ValidationException("customerId required");
                return model.score(store.load(customerId));
            });
        }
    }

    // =========================================================== ADAPTERS
    /** Pretend legacy client that throws infrastructure exceptions. */
    static final class LegacyClient {
        private final Map<String, double[]> data;
        private final boolean down;
        LegacyClient(Map<String, double[]> data, boolean down) { this.data = data; this.down = down; }
        double[] fetch(String id) throws IOException {
            if (down) throw new IOException("connection reset by peer");
            return data.get(id);
        }
    }
    /** Adapter translates IOException/null into domain exceptions. */
    static final class LegacyFeatureStoreAdapter implements FeatureStore {
        private final LegacyClient client;
        LegacyFeatureStoreAdapter(LegacyClient c) { this.client = c; }
        public double[] load(String id) {
            try {
                double[] f = client.fetch(id);
                if (f == null) throw new NotFoundException("no features for " + id);
                return f;
            } catch (IOException e) {
                throw new UpstreamException("feature store unavailable", e, true);
            }
        }
    }

    // =========================================================== OUTERMOST LAYER
    static int httpStatus(DomainException e) {
        return switch (e) { // exhaustive: compiler error if a new subtype is added
            case ValidationException v -> 400;
            case NotFoundException n -> 404;
            case UpstreamException u -> u.retryable ? 503 : 502;
        };
    }

    static String render(Result<Double> r) {
        return switch (r) {
            case Result.Ok<Double> ok -> "200 score=" + ok.value();
            case Result.Err<Double> err -> httpStatus(err.error()) + " " + err.error().code();
        };
    }

    // =========================================================== suppressed-exception demo
    static final class Res implements AutoCloseable {
        public void close() { throw new IllegalStateException("close failed"); }
    }

    static int depthThrow(int depth, boolean stackful) {
        if (depth == 0) {
            if (stackful) throw new UpstreamException("x", null, true);
            throw new ValidationException("x");
        }
        return depthThrow(depth - 1, stackful) + 1;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        FeatureStore healthy = new LegacyFeatureStoreAdapter(new LegacyClient(Map.of("c1", new double[]{1, 2, 3}), false));
        FeatureStore down = new LegacyFeatureStoreAdapter(new LegacyClient(Map.of(), true));
        ScoringModel sum = f -> f[0] + f[1] + f[2];

        check(render(new ScoreCustomer(healthy, sum).execute("c1")).equals("200 score=6.0"), "happy path");
        check(render(new ScoreCustomer(healthy, sum).execute("zzz")).equals("404 NOT_FOUND"), "not found");
        check(render(new ScoreCustomer(healthy, sum).execute(" ")).equals("400 VALIDATION"), "validation");
        check(render(new ScoreCustomer(down, sum).execute("c1")).equals("503 UPSTREAM"), "upstream");

        // cause preserved through translation
        try { down.load("c1"); check(false, "must throw"); }
        catch (UpstreamException e) { check(e.getCause() instanceof IOException, "cause kept"); }

        // Result.map
        var mapped = new ScoreCustomer(healthy, sum).execute("c1").map(v -> v * 2);
        check(mapped instanceof Result.Ok<Double> ok && ok.value() == 12.0, "map on Ok");

        // suppressed exception semantics
        try (Res r = new Res()) { throw new IllegalArgumentException("body failed"); }
        catch (Exception e) {
            check(e instanceof IllegalArgumentException, "primary exception wins");
            check(e.getSuppressed().length == 1 && e.getSuppressed()[0].getMessage().equals("close failed"), "suppressed");
        }

        // stackless exceptions have no trace
        check(new ValidationException("x").getStackTrace().length == 0, "stackless");
        check(new UpstreamException("x", null, false).getStackTrace().length > 0, "stackful");

        // benchmark: creation+throw cost at depth 50
        int n = 200_000;
        long sink = 0;
        for (int w = 0; w < 50_000; w++) { sink += caught(false) + caught(true); }
        long t0 = System.nanoTime(); for (int i = 0; i < n; i++) sink += caught(false);
        long t1 = System.nanoTime(); for (int i = 0; i < n; i++) sink += caught(true);
        long t2 = System.nanoTime();
        System.out.printf("stackless throw: %.0f ns | stack-capturing throw: %.0f ns (sink=%d)%n",
                (t1 - t0) / (double) n, (t2 - t1) / (double) n, sink);
        System.out.println("Day 04 OK");
    }

    static int caught(boolean stackful) {
        try { return depthThrow(50, stackful); } catch (DomainException e) { return 1; }
    }
}
