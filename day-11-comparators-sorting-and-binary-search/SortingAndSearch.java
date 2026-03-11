import java.util.*;
import java.util.stream.IntStream;

/**
 * Day 11 - Comparator chains, stability, hand-written merge sort / 3-way quicksort,
 * binary-search variants (lower/upper bound, search on the answer) and sort benchmarks.
 *
 * <p>Run: {@code javac SortingAndSearch.java && java SortingAndSearch}
 */
public class SortingAndSearch {

    record Prediction(String id, String label, double score, long ts) {}

    // ======================================================================= stable merge sort
    static <T> void mergeSort(T[] a, Comparator<? super T> c) { msort(a, a.clone(), 0, a.length, c); }

    private static <T> void msort(T[] a, T[] tmp, int lo, int hi, Comparator<? super T> c) {
        if (hi - lo < 2) return;
        int mid = (lo + hi) >>> 1;
        msort(a, tmp, lo, mid, c);
        msort(a, tmp, mid, hi, c);
        if (c.compare(a[mid - 1], a[mid]) <= 0) return;      // already ordered: O(n) on sorted input (TimSort idea)
        System.arraycopy(a, lo, tmp, lo, hi - lo);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) a[k++] = c.compare(tmp[j], tmp[i]) < 0 ? tmp[j++] : tmp[i++]; // ties take LEFT => stable
        while (i < mid) a[k++] = tmp[i++];
        while (j < hi) a[k++] = tmp[j++];
    }

    // ======================================================================= 3-way randomised quicksort
    static void quickSort(int[] a) { quick(a, 0, a.length - 1, new Random(12345)); }

    private static void quick(int[] a, int lo, int hi, Random rnd) {
        while (lo < hi) {
            if (hi - lo < 16) { insertion(a, lo, hi); return; }
            int pivot = a[lo + rnd.nextInt(hi - lo + 1)];
            int lt = lo, gt = hi, i = lo;
            while (i <= gt) {                                    // Dutch national flag partition
                if (a[i] < pivot) swap(a, lt++, i++);
                else if (a[i] > pivot) swap(a, i, gt--);
                else i++;
            }
            if (lt - lo < hi - gt) { quick(a, lo, lt - 1, rnd); lo = gt + 1; }   // recurse smaller side => O(log n) stack
            else { quick(a, gt + 1, hi, rnd); hi = lt - 1; }
        }
    }
    private static void insertion(int[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) { int x = a[i], j = i - 1; while (j >= lo && a[j] > x) { a[j + 1] = a[j]; j--; } a[j + 1] = x; }
    }
    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // ======================================================================= binary search family
    /** first index i with a[i] >= x (== a.length if none). */
    static int lowerBound(double[] a, double x) {
        int lo = 0, hi = a.length;
        while (lo < hi) { int mid = (lo + hi) >>> 1; if (a[mid] < x) lo = mid + 1; else hi = mid; }
        return lo;
    }
    /** first index i with a[i] > x. */
    static int upperBound(double[] a, double x) {
        int lo = 0, hi = a.length;
        while (lo < hi) { int mid = (lo + hi) >>> 1; if (a[mid] <= x) lo = mid + 1; else hi = mid; }
        return lo;
    }
    /** Binary search ON THE ANSWER: smallest batch size whose throughput meets the target. */
    static int smallestBatchForThroughput(double targetPerSec) {
        int lo = 1, hi = 1 << 16;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            double latency = 0.002 + 0.00001 * mid;          // 2 ms fixed overhead + 10 us per item
            if (mid / latency >= targetPerSec) hi = mid; else lo = mid + 1;
        }
        return lo;
    }
    static double sqrtByBisection(double x) {
        double lo = 0, hi = Math.max(1, x);
        for (int i = 0; i < 100; i++) { double mid = (lo + hi) / 2; if (mid * mid < x) lo = mid; else hi = mid; }
        return lo;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        Random rnd = new Random(2024);

        // ---- comparator chain
        Comparator<Prediction> ranking = Comparator.comparingDouble(Prediction::score).reversed()
                .thenComparingLong(Prediction::ts)
                .thenComparing(Prediction::id);
        List<Prediction> ps = new ArrayList<>(List.of(
                new Prediction("b", "cat", 0.9, 5), new Prediction("a", "dog", 0.9, 5),
                new Prediction("c", "cat", 0.95, 9), new Prediction("d", "dog", 0.2, 1), new Prediction("e", "cat", 0.9, 2)));
        ps.sort(ranking);
        check(ps.stream().map(Prediction::id).toList().equals(List.of("c", "e", "a", "b", "d")), ps.toString());

        // double ordering corner cases
        check(Double.compare(Double.NaN, 1e300) > 0 && Double.compare(-0.0, 0.0) < 0, "Double.compare total order");
        check(!(Double.NaN < 1) && !(Double.NaN > 1), "operators are NOT a total order for NaN");
        int overflowBug = Integer.MAX_VALUE - (-1);
        check(overflowBug < 0 && Integer.compare(Integer.MAX_VALUE, -1) > 0, "a - b overflows; Integer.compare doesn't");

        // ---- stability of List.sort and our merge sort
        List<Prediction> ties = new ArrayList<>();
        for (int i = 0; i < 20_000; i++) ties.add(new Prediction(String.format("%06d", i), "x", rnd.nextInt(10), i));
        List<Prediction> jdkSorted = new ArrayList<>(ties);
        jdkSorted.sort(Comparator.comparingDouble(Prediction::score));
        Prediction[] mine = ties.toArray(new Prediction[0]);
        mergeSort(mine, Comparator.comparingDouble(Prediction::score));
        check(Arrays.asList(mine).equals(jdkSorted), "merge sort must equal TimSort output (both stable)");
        for (int i = 1; i < mine.length; i++)
            if (mine[i].score() == mine[i - 1].score()) check(mine[i].ts() > mine[i - 1].ts(), "stability broken");

        // ---- quicksort vs Arrays.sort (random, sorted, all-equal, few-distinct)
        int[][] inputs = {
                rnd.ints(100_000, -1_000_000, 1_000_000).toArray(),
                IntStream.range(0, 100_000).toArray(),
                IntStream.range(0, 100_000).map(i -> 100_000 - i).toArray(),
                new int[100_000],
                rnd.ints(100_000, 0, 5).toArray()};
        for (int[] in : inputs) { int[] a = in.clone(), b = in.clone(); quickSort(a); Arrays.sort(b); check(Arrays.equals(a, b), "quicksort mismatch"); }

        // ---- binary search family
        double[] scores = rnd.doubles(100_000).map(x -> Math.round(x * 100) / 100.0).sorted().toArray();
        for (double probe : new double[]{-1, 0.0, 0.5, 0.505, 0.99, 1.0, 2.0}) {
            int lb = lowerBound(scores, probe), ub = upperBound(scores, probe);
            long expectedLb = Arrays.stream(scores).filter(s -> s < probe).count();
            long expectedCount = Arrays.stream(scores).filter(s -> s == probe).count();
            check(lb == expectedLb && ub - lb == expectedCount, "bounds for " + probe);
        }
        int idx = Arrays.binarySearch(new double[]{1, 3, 5}, 4);
        check(idx == -3, "missing key returns -(insertionPoint)-1 = " + idx);
        check(smallestBatchForThroughput(40_000) == 134, "binary search on the answer: " + smallestBatchForThroughput(40_000));
        check(Math.abs(sqrtByBisection(2) - Math.sqrt(2)) < 1e-12, "bisection sqrt");
        int hugeLo = Integer.MAX_VALUE - 1, hugeHi = Integer.MAX_VALUE;
        check((hugeLo + hugeHi) / 2 < 0 && ((hugeLo + hugeHi) >>> 1) > 0, "midpoint overflow bug vs unsigned shift");

        // ---- benchmarks
        int n = 1_000_000;
        int[] base = rnd.ints(n, 0, Integer.MAX_VALUE).toArray();
        Integer[] boxed = Arrays.stream(base).boxed().toArray(Integer[]::new);
        Prediction[] recs = new Prediction[500_000];
        for (int i = 0; i < recs.length; i++) recs[i] = new Prediction("id" + i, "l", rnd.nextDouble(), i);

        for (int warm = 0; warm < 2; warm++) { int[] w = base.clone(); Arrays.sort(w); quickSort(base.clone()); }
        System.out.println("Sorting benchmarks (ms):");
        System.out.printf("  int[] 1M  Arrays.sort (dual-pivot)   %8.1f%n", time(() -> Arrays.sort(base.clone())));
        System.out.printf("  int[] 1M  our 3-way quicksort        %8.1f%n", time(() -> quickSort(base.clone())));
        System.out.printf("  int[] 1M  parallelSort               %8.1f%n", time(() -> Arrays.parallelSort(base.clone())));
        System.out.printf("  Integer[] 1M natural (TimSort)       %8.1f%n", time(() -> Arrays.sort(boxed.clone())));
        System.out.printf("  Integer[] 1M our merge sort          %8.1f%n", time(() -> mergeSort(boxed.clone(), Comparator.naturalOrder())));
        System.out.printf("  Prediction[] 500k by score (TimSort) %8.1f%n", time(() -> Arrays.sort(recs.clone(), Comparator.comparingDouble(Prediction::score))));
        System.out.println("Day 11 OK");
    }

    static double time(Runnable r) {
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) { long t0 = System.nanoTime(); r.run(); best = Math.min(best, (System.nanoTime() - t0) / 1e6); }
        return best;
    }
}
