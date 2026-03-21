import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/** Day 21 - reflection-based validation, problem+json errors, cursors and idempotency. Run: javac RestValidation.java && java RestValidation */
public class RestValidation {
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.RECORD_COMPONENT) @interface NotBlank { }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.RECORD_COMPONENT) @interface Size { int min() default 0; int max() default Integer.MAX_VALUE; }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.RECORD_COMPONENT) @interface Range { double min(); double max(); }
    record Violation(String field, String message) { }
    record CreatePrediction(@NotBlank String modelId, @Size(min = 1, max = 4) double[] features, @Range(min = 0, max = 1) double threshold) { }

    static List<Violation> validate(Record r) throws Exception {
        List<Violation> out = new ArrayList<>();
        for (RecordComponent rc : r.getClass().getRecordComponents()) {
            Method acc = rc.getAccessor(); acc.setAccessible(true);
            Object v = acc.invoke(r);
            if (rc.isAnnotationPresent(NotBlank.class) && (v == null || v.toString().isBlank())) out.add(new Violation(rc.getName(), "must not be blank"));
            Size s = rc.getAnnotation(Size.class);
            if (s != null) { int n = v == null ? 0 : Array.getLength(v); if (n < s.min() || n > s.max()) out.add(new Violation(rc.getName(), "size must be between " + s.min() + " and " + s.max())); }
            Range g = rc.getAnnotation(Range.class);
            if (g != null) { double d = ((Number) v).doubleValue(); if (d < g.min() || d > g.max()) out.add(new Violation(rc.getName(), "must be in [" + g.min() + ", " + g.max() + "]")); }
        }
        return out;
    }

    /** RFC 7807 problem details body. */
    static String problem(List<Violation> vs) {
        StringBuilder sb = new StringBuilder("{\"type\":\"about:blank\",\"title\":\"Validation failed\",\"status\":400,\"errors\":[");
        for (int i = 0; i < vs.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"field\":\"").append(vs.get(i).field()).append("\",\"message\":\"").append(vs.get(i).message()).append("\"}");
        }
        return sb.append("]}").toString();
    }

    static String cursor(long lastId) { return Base64.getUrlEncoder().withoutPadding().encodeToString(("id:" + lastId).getBytes()); }
    static long decodeCursor(String c) { return Long.parseLong(new String(Base64.getUrlDecoder().decode(c)).substring(3)); }

    static final ConcurrentHashMap<String, String> IDEMPOTENCY = new ConcurrentHashMap<>();
    static String handle(String key, Supplier<String> action) { return IDEMPOTENCY.computeIfAbsent(key, k -> action.get()); }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        check(validate(new CreatePrediction("m1", new double[]{1, 2}, 0.5)).isEmpty(), "valid request");
        List<Violation> bad = validate(new CreatePrediction(" ", new double[0], 1.5));
        check(bad.size() == 3, "three violations: " + bad);
        check(problem(bad).contains("\"status\":400") && problem(bad).contains("modelId"), "problem+json");
        check(decodeCursor(cursor(12345)) == 12345, "cursor round trip");

        AtomicInteger charges = new AtomicInteger();
        for (int i = 0; i < 3; i++) handle("key-1", () -> { charges.incrementAndGet(); return "receipt-1"; });
        check(charges.get() == 1, "retried POST charged once");

        CreatePrediction ok = new CreatePrediction("m1", new double[]{1, 2}, 0.5);
        long t0 = System.nanoTime();
        for (int i = 0; i < 200_000; i++) validate(ok);
        System.out.printf("validate x200k: %.0f ms (cache RecordComponent metadata in production)%n", (System.nanoTime() - t0) / 1e6);
        System.out.println("Day 21 OK");
    }
}
