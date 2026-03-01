import java.util.*;
import java.util.stream.IntStream;

/** Day 25 - flat matrices, matmul variants, stable softmax, cosine similarity, top-k. Run: javac MatrixOps.java && java MatrixOps */
public class MatrixOps {
    static double[] naive(double[] a, double[] b, int n) {          // i-j-k : strided access on b
        double[] c = new double[n * n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) { double s = 0; for (int k = 0; k < n; k++) s += a[i * n + k] * b[k * n + j]; c[i * n + j] = s; }
        return c;
    }
    static double[] ikj(double[] a, double[] b, int n) {            // i-k-j : sequential access
        double[] c = new double[n * n];
        for (int i = 0; i < n; i++) for (int k = 0; k < n; k++) { double aik = a[i * n + k]; for (int j = 0; j < n; j++) c[i * n + j] += aik * b[k * n + j]; }
        return c;
    }
    static double[] blocked(double[] a, double[] b, int n, int bs) {
        double[] c = new double[n * n];
        for (int ii = 0; ii < n; ii += bs) for (int kk = 0; kk < n; kk += bs) for (int jj = 0; jj < n; jj += bs)
            for (int i = ii; i < Math.min(ii + bs, n); i++) for (int k = kk; k < Math.min(kk + bs, n); k++) {
                double aik = a[i * n + k];
                for (int j = jj; j < Math.min(jj + bs, n); j++) c[i * n + j] += aik * b[k * n + j];
            }
        return c;
    }
    static double[] parallel(double[] a, double[] b, int n) {
        double[] c = new double[n * n];
        IntStream.range(0, n).parallel().forEach(i -> { for (int k = 0; k < n; k++) { double aik = a[i * n + k]; for (int j = 0; j < n; j++) c[i * n + j] += aik * b[k * n + j]; } });
        return c;
    }
    static double dot(double[] a, double[] b) {
        double s0 = 0, s1 = 0, s2 = 0, s3 = 0; int i = 0;
        for (; i + 3 < a.length; i += 4) { s0 += a[i] * b[i]; s1 += a[i + 1] * b[i + 1]; s2 += a[i + 2] * b[i + 2]; s3 += a[i + 3] * b[i + 3]; }
        for (; i < a.length; i++) s0 += a[i] * b[i];
        return s0 + s1 + s2 + s3;
    }
    static double cosine(double[] a, double[] b) { return dot(a, b) / Math.sqrt(dot(a, a) * dot(b, b)); }
    static double[] softmax(double[] x) {
        double max = Arrays.stream(x).max().orElse(0), sum = 0; double[] e = new double[x.length];
        for (int i = 0; i < x.length; i++) { e[i] = Math.exp(x[i] - max); sum += e[i]; }
        for (int i = 0; i < e.length; i++) e[i] /= sum;
        return e;
    }
    static int[] topK(double[] v, int k) {
        return IntStream.range(0, v.length).boxed().sorted((p, q) -> Double.compare(v[q], v[p])).limit(k).mapToInt(Integer::intValue).toArray();
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }
    static boolean close(double[] a, double[] b) { for (int i = 0; i < a.length; i++) if (Math.abs(a[i] - b[i]) > 1e-9 * Math.max(1, Math.abs(a[i]))) return false; return true; }
    static double best(Runnable r) { double b = 1e18; for (int i = 0; i < 3; i++) { long t = System.nanoTime(); r.run(); b = Math.min(b, (System.nanoTime() - t) / 1e6); } return b; }

    public static void main(String[] args) {
        int n = 320; Random rnd = new Random(1);
        double[] a = rnd.doubles((long) n * n).toArray(), b = rnd.doubles((long) n * n).toArray();
        double[] ref = naive(a, b, n);
        check(close(ref, ikj(a, b, n)) && close(ref, blocked(a, b, n, 64)) && close(ref, parallel(a, b, n)), "all matmul variants agree");
        double flops = 2.0 * n * n * n;
        double t1 = best(() -> naive(a, b, n)), t2 = best(() -> ikj(a, b, n)), t3 = best(() -> blocked(a, b, n, 64)), t4 = best(() -> parallel(a, b, n));
        System.out.printf("matmul %dx%d  ijk %.0f ms (%.2f GFLOPS) | ikj %.0f ms (%.2f) | blocked %.0f ms (%.2f) | parallel %.0f ms (%.2f)%n",
                n, n, t1, flops / t1 / 1e6, t2, flops / t2 / 1e6, t3, flops / t3 / 1e6, t4, flops / t4 / 1e6);

        double[] v = {1, 2, 3, 4, 5};
        check(dot(v, v) == 55 && Math.abs(cosine(v, v) - 1) < 1e-12 && Math.abs(cosine(new double[]{1, 0}, new double[]{0, 1})) < 1e-12, "dot/cosine");
        double[] sm = softmax(new double[]{1000, 1001, 1002});
        check(Math.abs(Arrays.stream(sm).sum() - 1) < 1e-12 && !Double.isNaN(sm[0]), "stable softmax");
        check(Double.isNaN(Math.exp(1000) / Math.exp(1000)), "naive softmax overflows to NaN");
        check(Arrays.equals(topK(new double[]{0.1, 0.9, 0.5, 0.7}, 2), new int[]{1, 3}), "top-k indices");
        System.out.println("Day 25 OK");
    }
}
