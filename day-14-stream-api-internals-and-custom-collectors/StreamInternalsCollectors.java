import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * Day 14 - Stream internals (laziness, short-circuiting, barriers), custom Collectors
 * (streaming statistics, bounded top-K, histogram), a sliding-window Spliterator and benchmarks.
 *
 * <p>Run: {@code javac StreamInternalsCollectors.java && java StreamInternalsCollectors}
 */
public class StreamInternalsCollectors {

    // ======================================================================= custom collectors
    static final class Welford {
        long n; double mean, m2;
        void add(double x) { n++; double d = x - mean; mean += d / n; m2 += d * (x - mean); }
        Welford merge(Welford o) {
            if (o.n == 0) return this;
            if (n == 0) { n = o.n; mean = o.mean; m2 = o.m2; return this; }
            double delta = o.mean - mean; long t = n + o.n;
            m2 += o.m2 + delta * delta * ((double) n * o.n / t);
            mean += delta * o.n / t; n = t;
            return this;
        }
    }
    record Summary(long count, double mean, double stddev) {}

    static Collector<Double, Welford, Summary> summary() {
        return Collector.of(Welford::new, Welford::add, Welford::merge,
                w -> new Summary(w.n, w.mean, w.n > 1 ? Math.sqrt(w.m2 / (w.n - 1)) : 0.0));
    }

    /** K largest elements, descending, O(n log k) with bounded memory. */
    static <T extends Comparable<? super T>> Collector<T, PriorityQueue<T>, List<T>> topK(int k) {
        return Collector.of(PriorityQueue::new,
                (pq, x) -> { pq.offer(x); if (pq.size() > k) pq.poll(); },
                (a, b) -> { b.forEach(x -> { a.offer(x); if (a.size() > k) a.poll(); }); return a; },
                pq -> { List<T> out = new ArrayList<>(pq); out.sort(Comparator.reverseOrder()); return out; },
                Collector.Characteristics.UNORDERED);
    }

    /** Fixed-width histogram over [lo, hi). */
    static Collector<Double, int[], int[]> histogram(double lo, double hi, int bins) {
        return Collector.of(() -> new int[bins],
                (h, x) -> { if (x >= lo && x < hi) h[(int) ((x - lo) / (hi - lo) * bins)]++; },
                (a, b) -> { for (int i = 0; i < a.length; i++) a[i] += b[i]; return a; });
    }

    // ======================================================================= sliding-window spliterator
    static <T> Stream<List<T>> windows(List<T> src, int w) {
        Spliterator<List<T>> sp = new Spliterators.AbstractSpliterator<>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
            int i = 0;
            @Override public boolean tryAdvance(Consumer<? super List<T>> action) {
                if (i + w > src.size()) return false;
                action.accept(src.subList(i++, i - 1 + w));
                return true;
            }
        };
        return StreamSupport.stream(sp, false);
    }

    record Event(String model, String tenant, double latencyMs, boolean error) {}

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // ---- laziness & short-circuit: only 7 elements flow through
        AtomicInteger visited = new AtomicInteger();
        int firstMultipleOf7 = Stream.iterate(1, x -> x + 1).peek(x -> visited.incrementAndGet()).filter(x -> x % 7 == 0).findFirst().orElseThrow();
        check(firstMultipleOf7 == 7 && visited.get() == 7, "short-circuit stops upstream, visited=" + visited.get());

        // no terminal op => nothing executes
        AtomicInteger sideEffects = new AtomicInteger();
        Stream<Integer> lazy = Stream.of(1, 2, 3).peek(x -> sideEffects.incrementAndGet()).map(x -> x * 2);
        check(sideEffects.get() == 0, "intermediate ops are lazy");
        lazy.toList();
        check(sideEffects.get() == 3, "terminal op triggers execution");

        // sorted() is a barrier: everything is consumed even for findFirst
        AtomicInteger seen = new AtomicInteger();
        Stream.of(5, 3, 9, 1).peek(x -> seen.incrementAndGet()).sorted().findFirst();
        check(seen.get() == 4, "sorted buffers all elements");

        // stream reuse
        Stream<String> once = Stream.of("a");
        once.count();
        try { once.count(); check(false, "must throw"); } catch (IllegalStateException expected) { }

        // ---- custom collectors, sequential == parallel
        double[] data = new Random(8).doubles(200_000, 0, 100).toArray();
        Summary seq = Arrays.stream(data).boxed().collect(summary());
        Summary par = Arrays.stream(data).boxed().parallel().collect(summary());
        DoubleSummaryStatistics ref = Arrays.stream(data).summaryStatistics();
        check(seq.count() == 200_000 && Math.abs(seq.mean() - ref.getAverage()) < 1e-9, "mean");
        check(Math.abs(seq.mean() - par.mean()) < 1e-9 && Math.abs(seq.stddev() - par.stddev()) < 1e-9, "parallel combiner correct");

        List<Integer> nums = new Random(9).ints(100_000, 0, 1_000_000).boxed().toList();
        List<Integer> top5 = nums.stream().collect(topK(5));
        List<Integer> expected = nums.stream().sorted(Comparator.reverseOrder()).limit(5).toList();
        check(top5.equals(expected), "topK " + top5);
        check(nums.parallelStream().collect(topK(5)).equals(expected), "parallel topK");

        int[] h = Arrays.stream(data).boxed().collect(histogram(0, 100, 10));
        check(Arrays.stream(h).sum() == 200_000, "histogram counts all");
        check(Arrays.stream(h).allMatch(c -> c > 18_000 && c < 22_000), "uniform data -> flat histogram " + Arrays.toString(h));

        // ---- sliding windows via custom Spliterator
        List<Double> series = DoubleStream.iterate(1, x -> x + 1).limit(10).boxed().toList();
        List<Double> movingAvg = windows(series, 3).map(w -> w.stream().mapToDouble(Double::doubleValue).average().orElse(0)).toList();
        check(movingAvg.equals(List.of(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)), "moving average " + movingAvg);

        // ---- groupingBy / teeing / partitioningBy / toMap merge
        Random r = new Random(10);
        List<Event> events = IntStream.range(0, 50_000).mapToObj(i ->
                new Event("m" + r.nextInt(3), "t" + r.nextInt(5), 5 + r.nextDouble() * 95, r.nextInt(50) == 0)).toList();
        Map<String, Double> avgByModel = events.stream().collect(Collectors.groupingBy(Event::model, TreeMap::new, Collectors.averagingDouble(Event::latencyMs)));
        check(avgByModel.keySet().equals(Set.of("m0", "m1", "m2")) && avgByModel.values().stream().allMatch(v -> v > 40 && v < 60), "groupingBy avg");
        var minMax = events.stream().collect(Collectors.teeing(
                Collectors.minBy(Comparator.comparingDouble(Event::latencyMs)),
                Collectors.maxBy(Comparator.comparingDouble(Event::latencyMs)),
                (mn, mx) -> new double[]{mn.get().latencyMs(), mx.get().latencyMs()}));
        check(minMax[0] < minMax[1], "teeing");
        Map<Boolean, Long> errs = events.stream().collect(Collectors.partitioningBy(Event::error, Collectors.counting()));
        check(errs.get(true) + errs.get(false) == 50_000 && errs.get(true) > 0, "partitioningBy");
        try { Stream.of("a", "a").collect(Collectors.toMap(s -> s, s -> 1)); check(false, "duplicate key"); } catch (IllegalStateException ok) { }
        check(Stream.of("a", "a").collect(Collectors.toMap(s -> s, s -> 1, Integer::sum)).get("a") == 2, "merge function");
        List<Integer> multi = Stream.of(1, 2, 3).<Integer>mapMulti((x, sink) -> { if (x != 2) { sink.accept(x); sink.accept(x * 10); } }).toList();
        check(multi.equals(List.of(1, 10, 3, 30)), "mapMulti");

        // ---- benchmarks: stream vs loop
        int n = 10_000_000;
        int[] arr = new Random(1).ints(n, 0, 1000).toArray();
        List<Integer> boxed = Arrays.stream(arr).boxed().toList();
        long sink = 0;
        for (int w = 0; w < 5; w++) { sink += loop(arr); sink += Arrays.stream(arr).filter(x -> (x & 1) == 0).mapToLong(x -> x).sum(); sink += boxed.stream().filter(x -> (x & 1) == 0).mapToLong(x -> x).sum(); }
        long t0 = System.nanoTime(); sink += loop(arr);
        long t1 = System.nanoTime(); sink += Arrays.stream(arr).filter(x -> (x & 1) == 0).mapToLong(x -> x).sum();
        long t2 = System.nanoTime(); sink += boxed.stream().filter(x -> (x & 1) == 0).mapToLong(x -> x).sum();
        long t3 = System.nanoTime(); sink += Arrays.stream(arr).parallel().filter(x -> (x & 1) == 0).mapToLong(x -> x).sum();
        long t4 = System.nanoTime();
        check(loop(arr) == Arrays.stream(arr).filter(x -> (x & 1) == 0).mapToLong(x -> x).sum(), "same result");
        System.out.printf("filter+sum over 10M: loop %.1f ms | IntStream %.1f ms | Stream<Integer> %.1f ms | IntStream.parallel %.1f ms (sink=%d)%n",
                (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6, (t4 - t3) / 1e6, sink);
        System.out.println("Day 14 OK");
    }

    static long loop(int[] a) { long s = 0; for (int x : a) if ((x & 1) == 0) s += x; return s; }
}
