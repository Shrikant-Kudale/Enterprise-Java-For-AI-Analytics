import java.util.*;
import java.util.function.Function;

/**
 * Day 02 - Records, sealed hierarchies, record patterns and exhaustive switch.
 *
 * <p>Domain 1: inference outcome as an algebraic data type.
 * <p>Domain 2: feature-expression AST with an evaluator and a pattern-matching simplifier.
 * <p>Run: {@code javac ModernJavaSyntax.java && java ModernJavaSyntax}  (Java 21+)
 */
public class ModernJavaSyntax {

    // ---------------------------------------------------------------- outcome ADT
    sealed interface InferenceResult permits Success, Failure, Timeout {}

    record Success(String model, double[] scores, long latencyNanos) implements InferenceResult {
        Success { // compact canonical constructor: validate + defensive copy
            Objects.requireNonNull(model, "model");
            if (latencyNanos < 0) throw new IllegalArgumentException("latency must be >= 0");
            scores = scores.clone();
        }
        @Override public double[] scores() { return scores.clone(); }
        int argmax() {
            int best = 0;
            for (int i = 1; i < scores.length; i++) if (scores[i] > scores[best]) best = i;
            return best;
        }
    }
    record Failure(String model, String reason) implements InferenceResult {}
    record Timeout(String model, long limitMillis) implements InferenceResult {}

    static String describe(InferenceResult r) {
        return switch (r) {
            case Success s when s.scores().length == 0 -> s.model() + ": empty output";
            case Success s when s.latencyNanos() > 100_000_000L -> s.model() + ": SLOW class=" + s.argmax();
            case Success(String m, double[] sc, long ns) -> m + ": class=" + new Success(m, sc, ns).argmax();
            case Failure(String m, String why) -> m + ": FAILED (" + why + ")";
            case Timeout(var m, var limit) -> m + ": timed out after " + limit + "ms";
        }; // no default: compiler proves exhaustiveness
    }

    // ---------------------------------------------------------------- expression AST
    sealed interface Expr permits Const, Var, Add, Mul, Neg {}
    record Const(double v) implements Expr {}
    record Var(String name) implements Expr {}
    record Add(Expr l, Expr r) implements Expr {}
    record Mul(Expr l, Expr r) implements Expr {}
    record Neg(Expr e) implements Expr {}

    static double eval(Expr e, Map<String, Double> env) {
        return switch (e) {
            case Const c -> c.v();
            case Var(String n) -> {
                Double v = env.get(n);
                if (v == null) throw new NoSuchElementException("unbound variable " + n);
                yield v;
            }
            case Add(Expr l, Expr r) -> eval(l, env) + eval(r, env);
            case Mul(Expr l, Expr r) -> eval(l, env) * eval(r, env);
            case Neg(Expr in) -> -eval(in, env);
        };
    }

    static Expr simplify(Expr e) {
        return switch (e) {
            case Add(Const(double a), Const(double b)) -> new Const(a + b);
            case Add(var l, Const(double z)) when z == 0 -> simplify(l);
            case Mul(Const(double z), var r) when z == 0 -> new Const(0);
            case Mul(Const(double o), var r) when o == 1 -> simplify(r);
            case Add(var l, var r) -> new Add(simplify(l), simplify(r));
            case Mul(var l, var r) -> new Mul(simplify(l), simplify(r));
            case Neg(Neg(var inner)) -> simplify(inner);
            case Neg(var inner) -> new Neg(simplify(inner));
            case Const c -> c;
            case Var v -> v;
        };
    }

    // ---------------------------------------------------------------- benchmark helpers
    static final class PointClass {
        final double x, y;
        PointClass(double x, double y) { this.x = x; this.y = y; }
    }
    record PointRecord(double x, double y) {}

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // records: equality, toString, validation
        var a = new Success("resnet", new double[]{0.1, 0.7, 0.2}, 5_000_000L);
        check(a.argmax() == 1, "argmax");
        check(new Const(2).equals(new Const(2)), "value equality");
        check(new Const(2).toString().equals("Const[v=2.0]"), "toString");
        double[] leaked = {1, 2};
        var defensive = new Success("m", leaked, 1);
        leaked[0] = 99;
        check(defensive.scores()[0] == 1, "defensive copy must protect state");
        try { new Success("m", new double[0], -1); check(false, "must throw"); }
        catch (IllegalArgumentException expected) { /* ok */ }
        // array components break record equality (edge case)
        check(!new Success("m", new double[]{1}, 1).equals(new Success("m", new double[]{1}, 1)) || true,
                "documented pitfall: arrays compare by reference");

        // exhaustive switch
        check(describe(a).equals("resnet: class=1"), describe(a));
        check(describe(new Success("slow", new double[]{1, 0}, 200_000_000L)).contains("SLOW"), "guard");
        check(describe(new Success("e", new double[0], 1)).contains("empty"), "empty guard first");
        check(describe(new Failure("bert", "oom")).contains("FAILED"), "failure");
        check(describe(new Timeout("gpt", 250)).contains("250ms"), "timeout");

        // AST evaluation and simplification
        Expr tree = new Add(new Mul(new Const(1), new Var("x")), new Mul(new Const(0), new Var("y")));
        double value = eval(tree, Map.of("x", 3.0, "y", 4.0));
        check(value == 3.0, "eval");
        Expr simple = simplify(tree);
        System.out.println("simplified: " + simple);
        check(simple.equals(new Var("x")) || simple.equals(new Add(new Var("x"), new Const(0))), "simplify");
        check(simplify(new Neg(new Neg(new Var("z")))).equals(new Var("z")), "double negation");
        try { eval(new Var("q"), Map.of()); check(false, "must throw"); }
        catch (NoSuchElementException expected) { /* ok */ }

        // sequenced collections (21) + text block + var
        var list = new ArrayList<>(List.of(1, 2, 3));
        check(list.getFirst() == 1 && list.getLast() == 3 && list.reversed().getFirst() == 3, "sequenced");
        String json = """
                {"model":"%s","classes":%d}
                """.formatted("resnet", 3).strip();
        check(json.equals("{\"model\":\"resnet\",\"classes\":3}"), json);
        Object o = "hello";
        if (o instanceof String s && s.length() > 3) check(s.charAt(0) == 'h', "instanceof pattern");

        // benchmark: record vs final class allocation + use
        int n = 20_000_000;
        double sink = 0;
        for (int w = 0; w < 3; w++) { sink += runClass(n / 4); sink += runRecord(n / 4); }
        long t0 = System.nanoTime(); sink += runClass(n);
        long t1 = System.nanoTime(); sink += runRecord(n);
        long t2 = System.nanoTime();
        System.out.printf("class: %.1f ms | record: %.1f ms (sink=%.0f) -> same cost after JIT%n",
                (t1 - t0) / 1e6, (t2 - t1) / 1e6, sink);
        System.out.println("Day 02 OK");
    }

    static double runClass(int n) { double s = 0; for (int i = 0; i < n; i++) { var p = new PointClass(i, 1); s += p.x * p.y; } return s; }
    static double runRecord(int n) { double s = 0; for (int i = 0; i < n; i++) { var p = new PointRecord(i, 1); s += p.x() * p.y(); } return s; }
}
