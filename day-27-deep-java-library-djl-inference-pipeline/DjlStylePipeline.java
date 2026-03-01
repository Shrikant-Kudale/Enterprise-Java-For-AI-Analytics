import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/** Day 27 - DJL-style Translator/Predictor/Criteria + micro-batching. Run: javac DjlStylePipeline.java && java DjlStylePipeline */
public class DjlStylePipeline {
    interface Translator<I, O> { float[] processInput(I in); O processOutput(float[] out); }
    record Classification(String label, double prob) { }

    /** Model with a fixed per-call cost (serialised like a single GPU stream). */
    static final class Model {
        static final int DIM = 256;
        final float[] w = new float[DIM]; final float bias = 0f;
        final AtomicInteger forwardCalls = new AtomicInteger(); private final ReentrantLock gpu = new ReentrantLock();
        Model() {
            for (String s : List.of("good", "great", "excellent", "love")) w[SentimentTranslator.idx(s)] = 2.5f;
            for (String s : List.of("bad", "awful", "terrible", "hate")) w[SentimentTranslator.idx(s)] = -2.5f;
        }
        float[] forward(float[][] batch) {
            gpu.lock();
            try {
                forwardCalls.incrementAndGet();
                LockSupport.parkNanos(200_000L + 3_000L * batch.length);       // fixed + per-item cost
                float[] out = new float[batch.length];
                for (int i = 0; i < batch.length; i++) { double s = bias; for (int j = 0; j < DIM; j++) s += w[j] * batch[i][j]; out[i] = (float) (1 / (1 + Math.exp(-s))); }
                return out;
            } finally { gpu.unlock(); }
        }
    }
    static final class SentimentTranslator implements Translator<String, Classification> {
        static int idx(String token) { return Math.floorMod(token.hashCode(), Model.DIM); }
        public float[] processInput(String text) {
            float[] v = new float[Model.DIM];
            for (String t : text.toLowerCase().split("[^a-z]+")) if (!t.isEmpty()) v[idx(t)]++;
            return v;
        }
        public Classification processOutput(float[] out) { return out[0] >= 0.5 ? new Classification("positive", out[0]) : new Classification("negative", 1 - out[0]); }
    }

    static final class Predictor<I, O> {
        private final Model model; private final Translator<I, O> tr;
        Predictor(Model m, Translator<I, O> t) { model = m; tr = t; }
        O predict(I in) { return batchPredict(List.of(in)).get(0); }
        List<O> batchPredict(List<I> items) {
            float[][] x = new float[items.size()][];
            for (int i = 0; i < x.length; i++) x[i] = tr.processInput(items.get(i));
            float[] y = model.forward(x);
            List<O> out = new ArrayList<>();
            for (float v : y) out.add(tr.processOutput(new float[]{v}));
            return out;
        }
    }
    /** Mirrors DJL's Criteria.builder()...build().loadPredictor(). */
    static final class Criteria<I, O> {
        private Translator<I, O> translator; private Model model;
        static <I, O> Criteria<I, O> builder() { return new Criteria<>(); }
        Criteria<I, O> optTranslator(Translator<I, O> t) { translator = t; return this; }
        Criteria<I, O> optModel(Model m) { model = m; return this; }
        Predictor<I, O> loadPredictor() { return new Predictor<>(model, translator); }
    }

    static final class MicroBatcher<I, O> {
        private record Pending<I, O>(I in, CompletableFuture<O> future) { }
        private final BlockingQueue<Pending<I, O>> queue = new LinkedBlockingQueue<>();
        MicroBatcher(Predictor<I, O> predictor, int maxBatch, long maxWaitMs) {
            Thread.ofPlatform().daemon().start(() -> {
                try {
                    while (true) {
                        List<Pending<I, O>> batch = new ArrayList<>();
                        batch.add(queue.take());
                        long deadline = System.nanoTime() + maxWaitMs * 1_000_000;
                        while (batch.size() < maxBatch) {
                            long left = deadline - System.nanoTime();
                            if (left <= 0) break;
                            Pending<I, O> next = queue.poll(left, TimeUnit.NANOSECONDS);
                            if (next == null) break;
                            batch.add(next);
                        }
                        try {
                            List<O> out = predictor.batchPredict(batch.stream().map(p -> p.in()).toList());
                            for (int i = 0; i < batch.size(); i++) batch.get(i).future().complete(out.get(i));
                        } catch (Exception e) { batch.forEach(p -> p.future().completeExceptionally(e)); }
                    }
                } catch (InterruptedException ignored) { }
            });
        }
        CompletableFuture<O> submit(I in) { CompletableFuture<O> f = new CompletableFuture<>(); queue.add(new Pending<>(in, f)); return f; }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        Model model = new Model();
        Predictor<String, Classification> predictor = Criteria.<String, Classification>builder().optTranslator(new SentimentTranslator()).optModel(model).loadPredictor();
        check(predictor.predict("Great product, I love it").label().equals("positive"), "positive");
        check(predictor.predict("awful and terrible").label().equals("negative"), "negative");

        List<String> texts = new ArrayList<>();
        for (int i = 0; i < 64; i++) texts.add(i % 2 == 0 ? "great value number " + i : "bad quality number " + i);
        List<Classification> single = texts.stream().map(predictor::predict).toList();
        model.forwardCalls.set(0);
        MicroBatcher<String, Classification> batcher = new MicroBatcher<>(predictor, 32, 5);
        List<CompletableFuture<Classification>> fs = texts.stream().map(batcher::submit).toList();
        for (int i = 0; i < 64; i++) check(Math.abs(fs.get(i).get(5, TimeUnit.SECONDS).prob() - single.get(i).prob()) < 1e-6, "batched == single");
        check(model.forwardCalls.get() < 16, "64 requests used " + model.forwardCalls.get() + " forward calls");

        int n = 2_000;
        long t0 = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) { for (int i = 0; i < n; i++) ex.submit(() -> predictor.predict("great")); }
        double single_s = (System.nanoTime() - t0) / 1e9;
        t0 = System.nanoTime();
        List<CompletableFuture<Classification>> all = new ArrayList<>();
        for (int i = 0; i < n; i++) all.add(batcher.submit("great"));
        for (CompletableFuture<Classification> f : all) f.get();
        double batched_s = (System.nanoTime() - t0) / 1e9;
        System.out.printf("%d requests: one-by-one %.0f req/s | micro-batched %.0f req/s%n", n, n / single_s, n / batched_s);
        System.out.println("Day 27 OK");
    }
}
