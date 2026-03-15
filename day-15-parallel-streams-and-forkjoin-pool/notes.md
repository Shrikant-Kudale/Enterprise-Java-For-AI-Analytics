# Day 15 - Parallel Streams and the Fork/Join Pool

> **Phase:** Functional Java & Streams | **Date:** 2026-03-15 | **Code:** [`ParallelForkJoin.java`](./ParallelForkJoin.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Fork/Join framework
* `ForkJoinPool` = work-stealing scheduler: each worker has a **deque**; it pushes/pops its own tasks LIFO (cache-warm) while idle workers **steal FIFO from the opposite end** (older, bigger tasks).
* `RecursiveTask<V>` (returns a value) / `RecursiveAction`. Pattern: *if small -> compute directly; else split, `fork()` one half, `compute()` the other in the current thread, then `join()`* (fork one, compute one avoids creating needless tasks).
* `invokeAll(a, b)` forks both; `join()` of a not-yet-started task runs it inline (help-first) - "managed blocking" via `ForkJoinPool.managedBlock` compensates threads for blocking I/O.
* **Common pool**: `ForkJoinPool.commonPool()`, parallelism = cores - 1 (+ the calling thread participates). Used by parallel streams, `CompletableFuture.supplyAsync` (without executor) and `Arrays.parallelSort`.

### Parallel streams
`list.parallelStream()` / `.parallel()`: the spliterator recursively `trySplit()`s into sub-ranges (~4 x parallelism leaves), each leaf runs the fused sink chain, partial results are combined with the collector's combiner / reduction op.

**Rules for correct parallel code**
1. Operations must be **stateless, non-interfering, associative** (`reduce` identity must be a true identity).
2. No shared mutable state (`forEach(list::add)` is a race). Use `collect`, `reduce`, `groupingByConcurrent`.
3. Don't block (I/O, locks) in the common pool - it starves every other parallel stream and async stage in the JVM. Use a dedicated pool or virtual threads.
4. Source matters: arrays/`ArrayList`/`IntStream.range` split perfectly; `LinkedList`, `Stream.iterate`, `BufferedReader.lines` split poorly.
5. Cost model (Doug Lea): worth it when `N * Q` (elements x per-element cost) > ~10,000 and the work is CPU-bound; boxing/memory-bound ops see little speed-up.

### Running in a custom pool
`pool.submit(() -> stream.parallel()...).get()` - the parallel stream uses the pool *of the thread that invokes the terminal op* (an implementation detail but widely relied on). Isolates workloads (e.g. a "batch scoring" pool of 8 threads vs the request-serving common pool).

### Amdahl & overhead
Speed-up <= `1 / (s + (1-s)/p)`; task creation, splitting, memory bandwidth and combine steps are the serial/overhead parts. `Arrays.parallelSort` only parallelises above 8192 elements for that reason.

### AI backend relevance
Batch scoring of features, parallel evaluation of candidate models/rules, shard merges, distance computations over large embedding arrays (compute-bound kernels). For I/O-bound fan-out prefer `CompletableFuture` + executor or virtual threads (day 17-18).

## 2. Memory Architecture Breakdown

```
Worker 0 deque: [T5 T4 T3]  <-- owner push/pop (LIFO, hot in L1/L2)        ... thieves take from the head: T3? no - FIFO end
Worker 1 deque: []        --- steals oldest/largest task from Worker 0's opposite end ---
Common pool threads: daemon, named ForkJoinPool.commonPool-worker-N; custom: ForkJoinPool-N-worker-M

Task object ~ 32-48 B (fields lo, hi, array ref, result, status). A threshold of 1 creates millions of tasks -> allocation + queue contention dominate.
```
* Fork/join tasks are heap objects: recursion depth is logarithmic (the pool, not the Java stack, holds the pending work) so deep splits don't overflow the stack.
* Memory-bound loops (sum of a big array) saturate bandwidth with 2-4 cores; extra cores add nothing. Compute-bound kernels (sin/log/model scoring) scale ~linearly.
* False sharing: per-thread accumulators packed in one array (`long[] partial = new long[p]`) ping-pong cache lines; pad or use local variables + final combine (or `LongAdder`).

## 3. Interview Edge Cases & Pitfalls

1. Blocking inside a parallel stream on the common pool (HTTP calls, `sleep`, DB) can stall the whole JVM's async work; classic production outage.
2. `reduce(0, (a,b) -> a - b)` isn't associative: sequential and parallel results differ. Same for `reduce` with a non-identity seed (`reduce(10, Integer::sum)` adds 10 per split!).
3. `parallelStream().forEach(list::add)` -> lost elements / `ArrayIndexOutOfBoundsException`; `Collections.synchronizedList` fixes safety but kills speed-up.
4. `findFirst` on a parallel ordered stream preserves encounter order (and costs); `findAny` is cheaper.
5. `limit(n)` on an ordered parallel stream can be slower than sequential - drop ordering with `unordered()`.
6. Nested parallel streams share the same pool: no extra parallelism, risk of starvation.
7. Thread-locals and `SecurityContext`/MDC are not propagated into pool threads.
8. Floating-point sums differ between sequential and parallel runs (non-associative rounding) - compare with tolerance.

## 4. Interview Q&A

**Q1. How does work stealing work and why is it efficient?**  
Each worker owns a deque, pushing/popping at one end; idle workers steal from the other end, taking the oldest (largest) tasks, minimising contention and giving good load balance for irregular recursive workloads.

**Q2. When should you not use parallel streams?**  
Small data, I/O or lock-bound work, ordered/limited pipelines, poorly splittable sources, code with side effects, or when the common pool is shared with latency-critical code.

**Q3. How do you run a parallel stream on a dedicated pool?**  
Submit the terminal operation as a task into your own `ForkJoinPool`: `pool.submit(() -> stream.parallel().sum()).get()`; size the pool deliberately and shut it down.

**Q4. Why must a reduce identity be a real identity?**  
Each parallel partition starts from the identity; a non-identity (e.g. 10 for sum) is added once per partition, producing a result that depends on the split count.

**Q5. fork() both halves and join both, or fork one and compute the other?**  
Fork one, compute the other directly: it avoids queueing a task you'd immediately wait for and keeps the current thread busy.

**Q6. How do you choose the sequential threshold?**  
Profile: pick a leaf size where per-leaf work is ~10-100 us (thousands of simple ops) so scheduling overhead is < 5%; typical heuristic is `n / (8 * parallelism)`.

## 5. Run the Lab

```bash
cd day-15-parallel-streams-and-forkjoin-pool
javac ParallelForkJoin.java
java ParallelForkJoin
```
The program prints benchmark tables and finishes with `Day 15 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
