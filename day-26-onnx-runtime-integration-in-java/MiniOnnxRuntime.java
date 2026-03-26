import java.nio.*;
import java.util.*;

/** Day 26 - ONNX-style graph executor: initializers, nodes, validation, batching. Run: javac MiniOnnxRuntime.java && java MiniOnnxRuntime */
public class MiniOnnxRuntime {
    record Tensor(int[] shape, float[] data) {
        int rows() { return shape[0]; }
        int cols() { return shape[1]; }
        static Tensor fromBuffer(FloatBuffer fb, int rows, int cols) { float[] d = new float[rows * cols]; fb.duplicate().get(d); return new Tensor(new int[]{rows, cols}, d); }
    }
    record Node(String op, List<String> in, String out) { }

    static Tensor gemm(Tensor a, Tensor b, Tensor c) {      // a[m,k] x b[k,n] + c[n]
        int m = a.rows(), k = a.cols(), n = b.cols();
        float[] o = new float[m * n];
        for (int i = 0; i < m; i++) for (int p = 0; p < k; p++) { float av = a.data()[i * k + p]; for (int j = 0; j < n; j++) o[i * n + j] += av * b.data()[p * n + j]; }
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) o[i * n + j] += c.data()[j];
        return new Tensor(new int[]{m, n}, o);
    }
    static Tensor relu(Tensor t) { float[] o = t.data().clone(); for (int i = 0; i < o.length; i++) o[i] = Math.max(0, o[i]); return new Tensor(t.shape(), o); }
    static Tensor softmax(Tensor t) {
        float[] o = new float[t.data().length]; int r = t.rows(), c = t.cols();
        for (int i = 0; i < r; i++) {
            float max = -Float.MAX_VALUE; for (int j = 0; j < c; j++) max = Math.max(max, t.data()[i * c + j]);
            double sum = 0; for (int j = 0; j < c; j++) { o[i * c + j] = (float) Math.exp(t.data()[i * c + j] - max); sum += o[i * c + j]; }
            for (int j = 0; j < c; j++) o[i * c + j] /= (float) sum;
        }
        return new Tensor(t.shape(), o);
    }

    static final class Session {
        final Map<String, Tensor> initializers; final List<Node> nodes;
        Session(Map<String, Tensor> init, List<Node> nodes) { this.initializers = init; this.nodes = nodes; }
        Tensor run(Map<String, Tensor> feeds, String output) {
            Map<String, Tensor> env = new HashMap<>(initializers); env.putAll(feeds);
            for (Node n : nodes) {
                List<Tensor> ins = new ArrayList<>();
                for (String name : n.in()) { Tensor t = env.get(name); if (t == null) throw new IllegalArgumentException("missing input: " + name); ins.add(t); }
                env.put(n.out(), switch (n.op()) {
                    case "Gemm" -> gemm(ins.get(0), ins.get(1), ins.get(2));
                    case "Relu" -> relu(ins.get(0));
                    case "Softmax" -> softmax(ins.get(0));
                    default -> throw new IllegalStateException("unsupported op " + n.op());
                });
            }
            return env.get(output);
        }
    }

    static Tensor randomTensor(Random r, int rows, int cols) { float[] d = new float[rows * cols]; for (int i = 0; i < d.length; i++) d[i] = (float) (r.nextGaussian() * 0.5); return new Tensor(new int[]{rows, cols}, d); }
    static Session mlp(int in, int hidden, int out, long seed) {
        Random r = new Random(seed);
        Map<String, Tensor> init = Map.of("W1", randomTensor(r, in, hidden), "b1", randomTensor(r, 1, hidden), "W2", randomTensor(r, hidden, out), "b2", randomTensor(r, 1, out));
        return new Session(init, List.of(new Node("Gemm", List.of("x", "W1", "b1"), "h"), new Node("Relu", List.of("h"), "a"),
                new Node("Gemm", List.of("a", "W2", "b2"), "z"), new Node("Softmax", List.of("z"), "probs")));
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        Session s = mlp(4, 8, 3, 42);
        Random r = new Random(7);
        Tensor batch = randomTensor(r, 32, 4);
        Tensor out = s.run(Map.of("x", batch), "probs");
        check(out.rows() == 32 && out.cols() == 3, "output shape [32,3]");
        for (int i = 0; i < 32; i++) check(Math.abs(out.data()[i * 3] + out.data()[i * 3 + 1] + out.data()[i * 3 + 2] - 1) < 1e-5, "softmax row sums to 1");

        for (int i = 0; i < 32; i++) {   // batch result == row-by-row result
            Tensor one = new Tensor(new int[]{1, 4}, Arrays.copyOfRange(batch.data(), i * 4, i * 4 + 4));
            Tensor o1 = s.run(Map.of("x", one), "probs");
            for (int j = 0; j < 3; j++) check(Math.abs(o1.data()[j] - out.data()[i * 3 + j]) < 1e-5, "batch consistency");
        }
        try { s.run(Map.of(), "probs"); check(false, "missing input"); } catch (IllegalArgumentException expected) { }

        FloatBuffer direct = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        direct.put(new float[]{1, 2, 3, 4, 5, 6, 7, 8}).flip();
        Tensor fromDirect = Tensor.fromBuffer(direct, 2, 4);
        check(s.run(Map.of("x", fromDirect), "probs").rows() == 2, "tensor created from a direct buffer");

        Session big = mlp(64, 256, 10, 1);
        System.out.println("batch -> samples/s (64 -> 256 -> 10 MLP)");
        for (int bs : new int[]{1, 8, 64, 256}) {
            Tensor x = randomTensor(r, bs, 64);
            for (int w = 0; w < 50; w++) big.run(Map.of("x", x), "probs");
            int iters = Math.max(20, 4000 / bs); long t0 = System.nanoTime();
            for (int i = 0; i < iters; i++) big.run(Map.of("x", x), "probs");
            System.out.printf("  batch %-4d %,10.0f samples/s%n", bs, iters * (double) bs / ((System.nanoTime() - t0) / 1e9));
        }
        System.out.println("Day 26 OK");
    }
}
