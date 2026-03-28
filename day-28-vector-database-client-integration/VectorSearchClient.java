import java.util.*;
import java.util.function.IntPredicate;

/** Day 28 - flat vs IVF vector search, recall@k, filters, Qdrant-style REST payloads. Run: javac VectorSearchClient.java && java VectorSearchClient */
public class VectorSearchClient {
    static final int DIM = 32;
    static float[][] data; static final Random RND = new Random(5);

    static float[] normalize(float[] v) { double n = 0; for (float x : v) n += x * x; n = Math.sqrt(n); float[] o = new float[v.length]; for (int i = 0; i < v.length; i++) o[i] = (float) (v[i] / n); return o; }
    static float dot(float[] a, float[] b) { float s = 0; for (int i = 0; i < a.length; i++) s += a[i] * b[i]; return s; }

    static int[] topK(float[] q, int k, int[] candidates, IntPredicate filter) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((x, y) -> Float.compare(score(q, x[0]), score(q, y[0])));
        for (int id : candidates) {
            if (!filter.test(id)) continue;
            if (heap.size() < k) heap.add(new int[]{id});
            else if (score(q, id) > score(q, heap.peek()[0])) { heap.poll(); heap.add(new int[]{id}); }
        }
        int[] out = new int[heap.size()];
        for (int i = out.length - 1; i >= 0; i--) out[i] = heap.poll()[0];
        return out;
    }
    static float score(float[] q, int id) { return dot(q, data[id]); }

    static final class Ivf {
        final float[][] centroids; final List<List<Integer>> lists = new ArrayList<>();
        Ivf(int nlist, int iterations) {
            centroids = new float[nlist][];
            for (int c = 0; c < nlist; c++) centroids[c] = data[RND.nextInt(data.length)].clone();
            for (int it = 0; it < iterations; it++) {
                double[][] sum = new double[nlist][DIM]; int[] cnt = new int[nlist];
                for (float[] v : data) { int c = nearest(v); cnt[c]++; for (int d = 0; d < DIM; d++) sum[c][d] += v[d]; }
                for (int c = 0; c < nlist; c++) if (cnt[c] > 0) { float[] m = new float[DIM]; for (int d = 0; d < DIM; d++) m[d] = (float) (sum[c][d] / cnt[c]); centroids[c] = normalize(m); }
            }
            for (int c = 0; c < nlist; c++) lists.add(new ArrayList<>());
            for (int id = 0; id < data.length; id++) lists.get(nearest(data[id])).add(id);
        }
        int nearest(float[] v) { int best = 0; float bs = -2; for (int c = 0; c < centroids.length; c++) { float s = dot(v, centroids[c]); if (s > bs) { bs = s; best = c; } } return best; }
        int[] search(float[] q, int k, int nprobe) {
            Integer[] order = new Integer[centroids.length]; for (int i = 0; i < order.length; i++) order[i] = i;
            Arrays.sort(order, (a, b) -> Float.compare(dot(q, centroids[b]), dot(q, centroids[a])));
            List<Integer> cand = new ArrayList<>();
            for (int i = 0; i < nprobe; i++) cand.addAll(lists.get(order[i]));
            return topK(q, k, cand.stream().mapToInt(Integer::intValue).toArray(), id -> true);
        }
    }

    static String upsertBody(int id, float[] v, String tenant) { return "{\"points\":[{\"id\":" + id + ",\"vector\":" + Arrays.toString(v) + ",\"payload\":{\"tenant\":\"" + tenant + "\"}}]}"; }
    static String searchBody(float[] v, int limit, String tenant) { return "{\"vector\":" + Arrays.toString(v) + ",\"limit\":" + limit + ",\"filter\":{\"must\":[{\"key\":\"tenant\",\"match\":{\"value\":\"" + tenant + "\"}}]}}"; }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        int n = 20_000, clusters = 64;
        float[][] centers = new float[clusters][DIM];
        for (float[] c : centers) for (int d = 0; d < DIM; d++) c[d] = (float) RND.nextGaussian();
        data = new float[n][];
        for (int i = 0; i < n; i++) { float[] v = centers[RND.nextInt(clusters)].clone(); for (int d = 0; d < DIM; d++) v[d] += (float) (RND.nextGaussian() * 0.3); data[i] = normalize(v); }
        int[] all = new int[n]; for (int i = 0; i < n; i++) all[i] = i;
        Ivf ivf = new Ivf(64, 5);

        int queries = 200, k = 10; float[][] qs = new float[queries][];
        for (int i = 0; i < queries; i++) { float[] v = data[RND.nextInt(n)].clone(); for (int d = 0; d < DIM; d++) v[d] += (float) (RND.nextGaussian() * 0.05); qs[i] = normalize(v); }

        long t0 = System.nanoTime(); int[][] exact = new int[queries][];
        for (int i = 0; i < queries; i++) exact[i] = topK(qs[i], k, all, id -> true);
        double flatMs = (System.nanoTime() - t0) / 1e6 / queries;
        for (int nprobe : new int[]{1, 4, 8, 64}) {
            t0 = System.nanoTime(); double recall = 0;
            for (int i = 0; i < queries; i++) { Set<Integer> truth = new HashSet<>(); for (int id : exact[i]) truth.add(id); int hit = 0; for (int id : ivf.search(qs[i], k, nprobe)) if (truth.contains(id)) hit++; recall += hit / (double) k; }
            recall /= queries;
            System.out.printf("nprobe=%-3d recall@10=%.3f  %.3f ms/query (flat %.3f ms)%n", nprobe, recall, (System.nanoTime() - t0) / 1e6 / queries, flatMs);
            if (nprobe == 8) check(recall >= 0.9, "IVF recall with nprobe=8 is " + recall);
            if (nprobe == 64) check(recall > 0.999, "probing every list is exact");
        }
        check(topK(data[123], 1, all, id -> true)[0] == 123, "self-query returns itself");
        int[] filtered = topK(qs[0], 10, all, id -> id % 4 == 1);
        check(filtered.length == 10 && Arrays.stream(filtered).allMatch(id -> id % 4 == 1), "metadata filter respected");
        check(upsertBody(1, new float[]{0.5f}, "a").contains("\"payload\":{\"tenant\":\"a\"}") && searchBody(new float[]{1f}, 5, "a").contains("\"limit\":5"), "REST payloads");
        System.out.println("Day 28 OK");
    }
}
