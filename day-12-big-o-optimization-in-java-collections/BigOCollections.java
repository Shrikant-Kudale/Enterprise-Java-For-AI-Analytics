import java.util.*;
import java.util.function.Supplier;

/**
 * Day 12 - Empirical complexity experiments with the JDK collections.
 * Each experiment checks the optimised version returns identical results, then prints measured
 * scaling exponents (log2 of the time ratio when n doubles).
 *
 * <p>Run: {@code javac BigOCollections.java && java BigOCollections}
 */
public class BigOCollections {

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    /** best-of-N wall time in ms (min filters GC/JIT noise). */
    static double ms(Runnable r) {
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) { long t0 = System.nanoTime(); r.run(); best = Math.min(best, (System.nanoTime() - t0) / 1e6); }
        return best;
    }
    static double exponent(double tSmall, double tBig) { return Math.log(Math.max(tBig, 1e-6) / Math.max(tSmall, 1e-6)) / Math.log(2); }

    // ---------------------------------------------------------------- experiment 1: membership
    static long countContainsList(List<Integer> list, int[] queries) { long c = 0; for (int q : queries) if (list.contains(q)) c++; return c; }
    static long countContainsSet(Set<Integer> set, int[] queries) { long c = 0; for (int q : queries) if (set.contains(q)) c++; return c; }
    static long countContainsSorted(int[] sorted, int[] queries) { long c = 0; for (int q : queries) if (Arrays.binarySearch(sorted, q) >= 0) c++; return c; }

    // ---------------------------------------------------------------- experiment 2: two-sum
    static int[] twoSumNaive(int[] a, int target) {
        for (int i = 0; i < a.length; i++) for (int j = i + 1; j < a.length; j++) if (a[i] + a[j] == target) return new int[]{i, j};
        return null;
    }
    static int[] twoSumHash(int[] a, int target) {
        Map<Integer, Integer> seen = new HashMap<>(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            Integer j = seen.get(target - a[i]);
            if (j != null) return new int[]{j, i};
            seen.put(a[i], i);
        }
        return null;
    }

    // ---------------------------------------------------------------- experiment 3: strings
    static int concatSlow(int n) { String s = ""; for (int i = 0; i < n; i++) s += (char) ('a' + i % 26); return s.length(); }
    static int concatFast(int n) { StringBuilder sb = new StringBuilder(n); for (int i = 0; i < n; i++) sb.append((char) ('a' + i % 26)); return sb.length(); }

    // ---------------------------------------------------------------- experiment 4: queue drain
    static long drainArrayList(int n) { List<Integer> l = new ArrayList<>(); for (int i = 0; i < n; i++) l.add(i); long s = 0; while (!l.isEmpty()) s += l.remove(0); return s; }
    static long drainArrayDeque(int n) { Deque<Integer> d = new ArrayDeque<>(); for (int i = 0; i < n; i++) d.add(i); long s = 0; while (!d.isEmpty()) s += d.poll(); return s; }

    // ---------------------------------------------------------------- experiment 5: presizing
    static int fillDefault(int n) { Map<Integer, Integer> m = new HashMap<>(); for (int i = 0; i < n; i++) m.put(i, i); return m.size(); }
    static int fillPresized(int n) { Map<Integer, Integer> m = new HashMap<>((int) (n / 0.75f) + 1); for (int i = 0; i < n; i++) m.put(i, i); return m.size(); }

    public static void main(String[] args) {
        Random rnd = new Random(77);

        // ---- 1. membership: ArrayList.contains vs HashSet vs sorted array + binary search
        System.out.println("1) membership, 2000 queries:       n   list.contains   HashSet   binarySearch   (ms)");
        double prevList = 0;
        for (int n : new int[]{5_000, 10_000, 20_000, 40_000}) {
            int[] data = rnd.ints(n, 0, n * 4).distinct().toArray();
            List<Integer> list = new ArrayList<>(); for (int x : data) list.add(x);
            Set<Integer> set = new HashSet<>(list);
            int[] sorted = data.clone(); Arrays.sort(sorted);
            int[] queries = rnd.ints(2_000, 0, n * 4).toArray();
            long a = countContainsList(list, queries), b = countContainsSet(set, queries), c = countContainsSorted(sorted, queries);
            check(a == b && b == c, "membership answers must agree");
            double tl = ms(() -> countContainsList(list, queries)), ts = ms(() -> countContainsSet(set, queries)), tb = ms(() -> countContainsSorted(sorted, queries));
            System.out.printf("%36d %14.2f %9.3f %14.3f   exponent(list)=%s%n", n, tl, ts, tb, prevList == 0 ? "-" : String.format("%.2f", exponent(prevList, tl)));
            prevList = tl;
        }

        // ---- 2. two-sum
        System.out.println("2) two-sum:                         n   naive O(n^2)   hash O(n)   exponent(naive)");
        double prevNaive = 0;
        for (int n : new int[]{5_000, 10_000, 20_000}) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = 2 * i + 1;      // all odd -> no pair sums to an odd target; forces full scan
            int target = 4 * n + 1;                              // odd target: no sum of two odd numbers can match
            check(twoSumNaive(a, target) == null && twoSumHash(a, target) == null, "no pair expected");
            double tn = ms(() -> twoSumNaive(a, 2 * n + 1)), th = ms(() -> twoSumHash(a, 2 * n + 1));
            System.out.printf("%36d %14.2f %11.3f   %s%n", n, tn, th, prevNaive == 0 ? "-" : String.format("%.2f", exponent(prevNaive, tn)));
            prevNaive = tn;
        }
        int[] sample = rnd.ints(10_000, 0, 1_000_000).toArray();
        int tgt = sample[17] + sample[9_001];
        int[] r1 = twoSumNaive(sample, tgt), r2 = twoSumHash(sample, tgt);
        check(r1 != null && r2 != null && sample[r1[0]] + sample[r1[1]] == tgt && sample[r2[0]] + sample[r2[1]] == tgt, "both find a valid pair");

        // ---- 3. strings
        check(concatSlow(5_000) == concatFast(5_000), "same string length");
        double slow = ms(() -> concatSlow(50_000)), fast = ms(() -> concatFast(50_000));
        System.out.printf("3) string concat 50k chars:  += %.2f ms | StringBuilder %.3f ms (%.0fx)%n", slow, fast, slow / Math.max(fast, 1e-3));
        check(slow > fast, "StringBuilder must win");

        // ---- 4. queue drain
        check(drainArrayList(2_000) == drainArrayDeque(2_000), "same drain sum");
        double tAl = ms(() -> drainArrayList(60_000)), tDq = ms(() -> drainArrayDeque(60_000));
        System.out.printf("4) drain 60k: ArrayList.remove(0) %.1f ms | ArrayDeque.poll %.2f ms (%.0fx)%n", tAl, tDq, tAl / Math.max(tDq, 1e-3));
        check(tAl > tDq, "ArrayDeque must win");

        // ---- 5. presizing
        check(fillDefault(1000) == fillPresized(1000), "same map size");
        double tDef = ms(() -> fillDefault(2_000_000)), tPre = ms(() -> fillPresized(2_000_000));
        System.out.printf("5) HashMap 2M inserts: default %.0f ms | presized %.0f ms%n", tDef, tPre);

        System.out.println("Day 12 OK");
    }
}
