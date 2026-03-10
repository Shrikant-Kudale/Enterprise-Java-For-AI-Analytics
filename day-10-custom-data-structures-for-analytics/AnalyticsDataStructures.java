import java.util.*;

/**
 * Day 10 - Streaming/probabilistic data structures for analytics:
 * Bloom filter, Count-Min sketch, HyperLogLog, Welford running statistics (mergeable) and reservoir sampling.
 *
 * <p>Run: {@code javac AnalyticsDataStructures.java && java AnalyticsDataStructures}
 */
public class AnalyticsDataStructures {

    /** SplitMix64 finaliser: excellent avalanche for 64-bit ids. */
    static long mix(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    // ================================================================= Bloom filter
    static final class BloomFilter {
        private final long[] bits;
        private final int m, k;
        BloomFilter(long expected, double fpp) {
            this.m = (int) Math.ceil(-expected * Math.log(fpp) / (Math.log(2) * Math.log(2)));
            this.k = Math.max(1, (int) Math.round((double) m / expected * Math.log(2)));
            this.bits = new long[(m + 63) >>> 6];
        }
        void add(long x) {
            long h1 = mix(x), h2 = mix(x ^ 0x9E3779B97F4A7C15L) | 1L;
            for (int i = 0; i < k; i++) {
                int idx = (int) Long.remainderUnsigned(h1 + i * h2, m);
                bits[idx >>> 6] |= 1L << idx;
            }
        }
        boolean mightContain(long x) {
            long h1 = mix(x), h2 = mix(x ^ 0x9E3779B97F4A7C15L) | 1L;
            for (int i = 0; i < k; i++) {
                int idx = (int) Long.remainderUnsigned(h1 + i * h2, m);
                if ((bits[idx >>> 6] & (1L << idx)) == 0) return false;
            }
            return true;
        }
        void merge(BloomFilter o) { for (int i = 0; i < bits.length; i++) bits[i] |= o.bits[i]; }
        int bitsCount() { return m; }
        int hashes() { return k; }
    }

    // ================================================================= Count-Min sketch
    static final class CountMinSketch {
        private final long[][] table;
        private final int depth, width;
        CountMinSketch(int depth, int width) { this.depth = depth; this.width = width; this.table = new long[depth][width]; }
        private int col(long x, int row) { return (int) Long.remainderUnsigned(mix(x + 0x632BE59BD9B4E019L * (row + 1)), width); }
        void add(long x, long count) { for (int r = 0; r < depth; r++) table[r][col(x, r)] += count; }
        long estimate(long x) {
            long min = Long.MAX_VALUE;
            for (int r = 0; r < depth; r++) min = Math.min(min, table[r][col(x, r)]);
            return min;
        }
        double epsilon() { return Math.E / width; }
    }

    // ================================================================= HyperLogLog
    static final class HyperLogLog {
        private final int p, m;
        private final byte[] reg;
        HyperLogLog(int p) { this.p = p; this.m = 1 << p; this.reg = new byte[m]; }
        void add(long x) {
            long h = mix(x);
            int idx = (int) (h >>> (64 - p));
            long w = (h << p) | (1L << (p - 1)); // sentinel bit bounds the rank
            byte rank = (byte) (Long.numberOfLeadingZeros(w) + 1);
            if (rank > reg[idx]) reg[idx] = rank;
        }
        double estimate() {
            double sum = 0; int zeros = 0;
            for (byte r : reg) { sum += Math.pow(2, -r); if (r == 0) zeros++; }
            double alpha = 0.7213 / (1 + 1.079 / m);
            double e = alpha * m * m / sum;
            if (e <= 2.5 * m && zeros > 0) e = m * Math.log((double) m / zeros); // small-range correction
            return e;
        }
        void merge(HyperLogLog o) { for (int i = 0; i < m; i++) reg[i] = (byte) Math.max(reg[i], o.reg[i]); }
        double stdError() { return 1.04 / Math.sqrt(m); }
        int bytes() { return m; }
    }

    // ================================================================= Welford running stats (mergeable)
    static final class RunningStats {
        long n; double mean, m2;
        void add(double x) { n++; double d = x - mean; mean += d / n; m2 += d * (x - mean); }
        double variance() { return n > 1 ? m2 / (n - 1) : 0; }
        double stddev() { return Math.sqrt(variance()); }
        void merge(RunningStats o) {   // Chan et al. parallel algorithm
            if (o.n == 0) return;
            if (n == 0) { n = o.n; mean = o.mean; m2 = o.m2; return; }
            double delta = o.mean - mean;
            long total = n + o.n;
            m2 += o.m2 + delta * delta * ((double) n * o.n / total);
            mean += delta * o.n / total;
            n = total;
        }
    }

    // ================================================================= Reservoir sampling (Algorithm R)
    static int[] reservoir(int[] stream, int k, Random rnd) {
        int[] res = Arrays.copyOf(stream, k);
        for (int i = k; i < stream.length; i++) {
            int j = rnd.nextInt(i + 1);
            if (j < k) res[j] = stream[i];
        }
        return res;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // ---- Bloom filter: no false negatives, FP rate near design
        final int N = 200_000;
        BloomFilter bloom = new BloomFilter(N, 0.01);
        for (long i = 0; i < N; i++) bloom.add(i);
        for (long i = 0; i < N; i++) check(bloom.mightContain(i), "false negative at " + i);
        int fp = 0, probes = 200_000;
        for (long i = N; i < N + probes; i++) if (bloom.mightContain(i)) fp++;
        double fpRate = fp / (double) probes;
        System.out.printf("Bloom: m=%,d bits (%.0f KB) k=%d  FP rate=%.3f%% (design 1%%)%n",
                bloom.bitsCount(), bloom.bitsCount() / 8192.0, bloom.hashes(), fpRate * 100);
        check(fpRate < 0.02, "FP rate within 2x of design");
        BloomFilter other = new BloomFilter(N, 0.01);
        other.add(-12345L); bloom.merge(other);
        check(bloom.mightContain(-12345L), "merged filter sees other's keys");

        // ---- Count-Min: never under-estimates, bounded over-estimate
        CountMinSketch cms = new CountMinSketch(5, 2048);
        long[] truth = new long[1_000];
        Random rnd = new Random(5);
        long total = 0;
        for (int i = 0; i < 1_000_000; i++) {
            int item = (int) Math.min(999, (long) (-Math.log(1 - rnd.nextDouble()) * 120)); // skewed (exponential)
            truth[item]++; cms.add(item, 1); total++;
        }
        int within = 0;
        for (int i = 0; i < truth.length; i++) {
            long est = cms.estimate(i);
            check(est >= truth[i], "CMS under-estimated item " + i);
            if (est - truth[i] <= cms.epsilon() * total) within++;
        }
        System.out.printf("Count-Min: eps*N=%.0f, %.1f%% of items within bound; est(item0)=%d truth=%d%n",
                cms.epsilon() * total, within / 10.0, cms.estimate(0), truth[0]);
        check(within >= 970, "at least 97% of items within eps*N");

        // ---- HyperLogLog: accuracy + merge
        HyperLogLog hll = new HyperLogLog(12);
        for (long i = 0; i < 100_000; i++) { hll.add(i); hll.add(i); } // duplicates ignored
        double est = hll.estimate(), err = Math.abs(est - 100_000) / 100_000;
        System.out.printf("HLL: estimate=%.0f (true 100000) error=%.2f%% (theory %.2f%%), memory=%d B vs HashSet ~%d KB%n",
                est, err * 100, hll.stdError() * 100, hll.bytes(), 100_000 * 48 / 1024);
        check(err < 0.05, "HLL error below 5%");
        HyperLogLog a = new HyperLogLog(12), b = new HyperLogLog(12);
        for (long i = 0; i < 60_000; i++) a.add(i);
        for (long i = 40_000; i < 100_000; i++) b.add(i);
        a.merge(b);
        check(Math.abs(a.estimate() - 100_000) / 100_000 < 0.05, "merged HLL counts the union");
        HyperLogLog small = new HyperLogLog(12);
        for (long i = 0; i < 50; i++) small.add(i);
        check(Math.abs(small.estimate() - 50) <= 3, "linear counting for small cardinalities: " + small.estimate());

        // ---- Welford vs two-pass; merge
        double[] xs = new double[500_000];
        for (int i = 0; i < xs.length; i++) xs[i] = 1e9 + rnd.nextGaussian() * 3.0; // large mean, small variance
        RunningStats whole = new RunningStats(), left = new RunningStats(), right = new RunningStats();
        double sum = 0;
        for (int i = 0; i < xs.length; i++) { whole.add(xs[i]); (i < 123_457 ? left : right).add(xs[i]); sum += xs[i]; }
        double mean = sum / xs.length, ss = 0;
        for (double x : xs) ss += (x - mean) * (x - mean);
        double twoPass = ss / (xs.length - 1);
        left.merge(right);
        check(Math.abs(whole.variance() - twoPass) < 1e-6, "Welford matches two-pass variance");
        check(Math.abs(left.variance() - twoPass) < 1e-6 && left.n == xs.length, "Chan merge matches");
        double naive = 0, naiveSq = 0;
        for (double x : xs) { naive += x; naiveSq += x * x; }
        double naiveVar = (naiveSq - naive * naive / xs.length) / (xs.length - 1);
        System.out.printf("variance: two-pass=%.4f welford=%.4f naive(E[x^2]-E[x]^2)=%.4f  <- catastrophic cancellation%n",
                twoPass, whole.variance(), naiveVar);

        // ---- Reservoir sampling uniformity: n=10,k=3 -> each element chosen with p=0.3
        int[] stream = java.util.stream.IntStream.range(0, 10).toArray();
        int[] hits = new int[10];
        Random rr = new Random(99);
        int trials = 60_000;
        for (int t = 0; t < trials; t++) for (int v : reservoir(stream, 3, rr)) hits[v]++;
        for (int h : hits) check(Math.abs(h / (double) trials - 0.3) < 0.01, "reservoir uniformity " + h / (double) trials);

        // ---- benchmark
        int ops = 3_000_000;
        BloomFilter bf = new BloomFilter(ops, 0.01);
        CountMinSketch cm = new CountMinSketch(5, 2048);
        HyperLogLog hl = new HyperLogLog(12);
        HashSet<Long> exact = new HashSet<>();
        long t0 = System.nanoTime(); for (long i = 0; i < ops; i++) bf.add(i);
        long t1 = System.nanoTime(); for (long i = 0; i < ops; i++) cm.add(i & 0xFFFF, 1);
        long t2 = System.nanoTime(); for (long i = 0; i < ops; i++) hl.add(i);
        long t3 = System.nanoTime(); for (long i = 0; i < ops; i++) exact.add(i);
        long t4 = System.nanoTime();
        System.out.printf("add x3M -> bloom %.0f ms | count-min %.0f ms | hll %.0f ms | exact HashSet %.0f ms%n",
                (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6, (t4 - t3) / 1e6);
        System.out.println("Day 10 OK");
    }
}
