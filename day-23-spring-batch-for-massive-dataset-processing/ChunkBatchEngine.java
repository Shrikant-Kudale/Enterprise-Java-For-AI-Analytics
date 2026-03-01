import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.LongFunction;

/** Day 23 - chunk-oriented batch engine: skip, restart from checkpoint, partitions, chunk-size benchmark. Run: javac ChunkBatchEngine.java && java ChunkBatchEngine */
public class ChunkBatchEngine {
    interface Reader<I> { I read() throws Exception; }
    interface Processor<I, O> { O process(I in) throws Exception; }
    interface Writer<O> { void write(List<O> chunk) throws Exception; }
    static final class JobRepository { volatile long committed; }
    static final class State { long read, written, skipped, commits; int inChunk; }
    record Result(long read, long written, long skipped, long commits) { }

    static <I, O> Result run(LongFunction<Reader<I>> readerAt, Processor<I, O> p, Writer<O> w, int chunk, int skipLimit, JobRepository repo, long commitCostNanos) throws Exception {
        Reader<I> rd = readerAt.apply(repo.committed);          // restart: reopen at last committed offset
        List<O> buf = new ArrayList<>(chunk);
        State s = new State();
        I item;
        while ((item = rd.read()) != null) {
            s.read++; s.inChunk++;
            try { O o = p.process(item); if (o != null) buf.add(o); }
            catch (Exception e) { if (++s.skipped > skipLimit) throw e; }
            if (s.inChunk == chunk) flush(s, buf, w, repo, commitCostNanos);
        }
        if (s.inChunk > 0) flush(s, buf, w, repo, commitCostNanos);
        return new Result(s.read, s.written, s.skipped, s.commits);
    }
    static <O> void flush(State s, List<O> buf, Writer<O> w, JobRepository repo, long cost) throws Exception {
        w.write(new ArrayList<>(buf));                           // "transaction": write then checkpoint
        repo.committed += s.inChunk; s.written += buf.size(); s.commits++;
        buf.clear(); s.inChunk = 0;
        long end = System.nanoTime() + cost; while (System.nanoTime() < end) { }  // simulated commit cost
    }
    static LongFunction<Reader<Long>> range(long from, long to) {
        return off -> { AtomicLong n = new AtomicLong(from + off); return () -> n.get() < to ? n.getAndIncrement() : null; };
    }
    static Long transform(Long id) { if (id % 1000 == 998) throw new IllegalArgumentException("bad record " + id); return id % 2 == 1 ? null : id * 2; }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        final long TOTAL = 50_000;
        long expected = 0; for (long i = 0; i < TOTAL; i++) if (i % 2 == 0 && i % 1000 != 998) expected++;

        // crash on the 5th chunk, then restart: no loss, no duplicates
        Set<Long> sink = ConcurrentHashMap.newKeySet(); AtomicInteger dups = new AtomicInteger(), calls = new AtomicInteger(); AtomicBoolean crashed = new AtomicBoolean();
        Writer<Long> writer = chunk -> {
            if (calls.incrementAndGet() == 5 && crashed.compareAndSet(false, true)) throw new IllegalStateException("simulated crash");
            for (Long v : chunk) if (!sink.add(v)) dups.incrementAndGet();
        };
        JobRepository repo = new JobRepository();
        try { run(range(0, TOTAL), ChunkBatchEngine::transform, writer, 1_000, 100, repo, 0); check(false, "first run must crash"); }
        catch (IllegalStateException expectedCrash) { }
        check(repo.committed == 4_000, "checkpoint after 4 chunks: " + repo.committed);
        run(range(0, TOTAL), ChunkBatchEngine::transform, writer, 1_000, 100, repo, 0);
        check(sink.size() == expected && dups.get() == 0 && repo.committed == TOTAL, "restart is exactly-once, written=" + sink.size());

        // skip limit
        try { run(range(0, TOTAL), ChunkBatchEngine::transform, c -> { }, 500, 10, new JobRepository(), 0); check(false, "skip limit"); }
        catch (IllegalArgumentException expectedSkipOverflow) { }

        // partitioned parallel run (4 workers)
        AtomicLong total = new AtomicLong();
        ExecutorService ex = Executors.newFixedThreadPool(4);
        List<Future<Result>> fs = new ArrayList<>();
        for (int p = 0; p < 4; p++) { long from = p * 12_500L; fs.add(ex.submit(() -> run(range(from, from + 12_500), ChunkBatchEngine::transform, c -> total.addAndGet(c.size()), 500, 100, new JobRepository(), 0))); }
        for (Future<Result> f : fs) f.get();
        ex.shutdown();
        check(total.get() == expected, "partitions cover the range exactly");

        // chunk-size benchmark with a 10 us simulated commit cost
        System.out.println("chunk size -> time for 200k items (commit cost 10 us)");
        for (int chunk : new int[]{1, 10, 100, 1_000, 10_000}) {
            long t0 = System.nanoTime();
            Result r = run(range(0, 200_000), id -> id, c -> { }, chunk, 0, new JobRepository(), 10_000);
            System.out.printf("  chunk %-6d commits=%-7d %7.0f ms%n", chunk, r.commits(), (System.nanoTime() - t0) / 1e6);
        }
        System.out.println("Day 23 OK");
    }
}
