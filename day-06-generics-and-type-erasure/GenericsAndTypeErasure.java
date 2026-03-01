import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * Day 06 - Generics: bounded types, PECS, type erasure, bridge methods, super type tokens,
 * typesafe heterogeneous containers, heap pollution and the boxing tax.
 *
 * <p>Run: {@code javac GenericsAndTypeErasure.java && java GenericsAndTypeErasure}
 */
public class GenericsAndTypeErasure {

    // ---------------------------------------------------------------- bounded generic method
    static <T extends Comparable<? super T>> T max(Collection<? extends T> items) {
        Iterator<? extends T> it = items.iterator();
        T best = it.next();
        while (it.hasNext()) { T c = it.next(); if (c.compareTo(best) > 0) best = c; }
        return best;
    }

    // ---------------------------------------------------------------- PECS
    static double sum(Collection<? extends Number> producer) {
        double s = 0;
        for (Number n : producer) s += n.doubleValue();
        return s;
    }
    static <T> void copy(List<? super T> consumer, List<? extends T> producer) { consumer.addAll(producer); }

    // ---------------------------------------------------------------- Comparable record => bridge method
    record Version(int major, int minor) implements Comparable<Version> {
        public int compareTo(Version o) {
            int c = Integer.compare(major, o.major);
            return c != 0 ? c : Integer.compare(minor, o.minor);
        }
    }

    // ---------------------------------------------------------------- super type token
    abstract static class TypeRef<T> {
        final Type type;
        protected TypeRef() {
            Type s = getClass().getGenericSuperclass();
            if (!(s instanceof ParameterizedType p)) throw new IllegalStateException("must be subclassed with a type argument");
            this.type = p.getActualTypeArguments()[0];
        }
    }

    // ---------------------------------------------------------------- typesafe heterogeneous container
    static final class TypeSafeRegistry {
        private final Map<Class<?>, Object> map = new HashMap<>();
        <T> void put(Class<T> key, T value) { map.put(Objects.requireNonNull(key), key.cast(value)); }
        <T> T get(Class<T> key) { return key.cast(map.get(key)); }
    }

    @SuppressWarnings("unchecked")
    static <T> T[] newArray(Class<T> type, int n) { return (T[]) Array.newInstance(type, n); }

    // ---------------------------------------------------------------- typed pipeline stage
    interface Stage<I, O> {
        O apply(I in);
        default <R> Stage<I, R> then(Stage<? super O, ? extends R> next) { return in -> next.apply(apply(in)); }
        static <A, B> Stage<A, B> of(Function<A, B> f) { return f::apply; }
    }

    // ---------------------------------------------------------------- heap pollution
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void pollute(List<String> strings) {
        List raw = strings;
        raw.add(42); // compiles with a warning; poisons the list
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        // bounded generics
        check(max(List.of(3, 9, 4)) == 9, "max Integer");
        check(max(Set.of("a", "zz", "m")).equals("zz"), "max String");
        check(max(List.of(new Version(1, 2), new Version(1, 10), new Version(0, 99))).equals(new Version(1, 10)), "max Version");

        // erasure of the method signature
        Method m = GenericsAndTypeErasure.class.getDeclaredMethod("max", Collection.class);
        check(m.getReturnType() == Comparable.class, "T extends Comparable erases to Comparable");
        check(m.getGenericReturnType() instanceof TypeVariable<?>, "generic signature is retained as metadata");
        check(new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass(), "same runtime class");

        // bridge method generated for Comparable<Version>
        boolean bridge = Arrays.stream(Version.class.getDeclaredMethods()).anyMatch(Method::isBridge);
        check(bridge, "compareTo(Object) bridge must exist");

        // PECS
        List<Number> numbers = new ArrayList<>();
        copy(numbers, List.of(1, 2, 3));
        copy(numbers, List.of(0.5, 0.25));
        check(sum(numbers) == 6.75, "PECS copy + sum");

        // invariance vs array covariance
        Object[] arr = new String[1];
        try { arr[0] = 1; check(false, "ArrayStoreException expected"); } catch (ArrayStoreException expected) { }

        // super type token
        Type t = new TypeRef<Map<String, List<Integer>>>() { }.type;
        check(t.getTypeName().equals("java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>"), t.getTypeName());
        try { new TypeRef<String>() { }; new TypeRef<>() { }; } catch (RuntimeException ignored) { }

        // heterogeneous container + reified array
        TypeSafeRegistry reg = new TypeSafeRegistry();
        reg.put(String.class, "model-v1");
        reg.put(Integer.class, 7);
        check(reg.get(String.class).equals("model-v1") && reg.get(Integer.class) == 7, "registry");
        String[] sa = newArray(String.class, 3);
        check(sa.getClass() == String[].class, "Array.newInstance keeps the runtime component type");

        // typed pipeline
        Stage<String, Integer> len = String::length;
        Stage<String, String> pipeline = len.then(n -> n * 2).then(n -> "len2=" + n);
        check(pipeline.apply("abcd").equals("len2=8"), "stage composition");

        // heap pollution surfaces far from the cause
        List<String> strings = new ArrayList<>();
        pollute(strings);
        check(strings.size() == 1, "polluted list accepted an Integer");
        try { String s = strings.get(0); check(false, "ClassCastException expected " + s); }
        catch (ClassCastException expected) { }

        // Integer cache pitfall
        Integer a = 127, b = 127, c = 1000, d = 1000;
        check(a == b, "cached");
        check(c != d && c.equals(d), "== on boxed values >127 compares references");

        // benchmark: boxing tax
        int n = 5_000_000;
        List<Integer> boxed = new ArrayList<>(n);
        int[] prim = new int[n];
        for (int i = 0; i < n; i++) { boxed.add(i); prim[i] = i; }
        long s1 = 0, s2 = 0;
        for (int w = 0; w < 3; w++) { s1 += sumBoxed(boxed); s2 += sumPrim(prim); }
        long t0 = System.nanoTime(); s1 += sumBoxed(boxed);
        long t1 = System.nanoTime(); s2 += sumPrim(prim);
        long t2 = System.nanoTime();
        check(s1 / 4 == s2 / 4, "same sums");
        System.out.printf("List<Integer>: %.1f ms | int[]: %.1f ms  (boxing tax %.1fx)%n",
                (t1 - t0) / 1e6, (t2 - t1) / 1e6, (double) (t1 - t0) / Math.max(1, t2 - t1));
        System.out.println("Day 06 OK");
    }

    static long sumBoxed(List<Integer> l) { long s = 0; for (int x : l) s += x; return s; }
    static long sumPrim(int[] a) { long s = 0; for (int x : a) s += x; return s; }
}
