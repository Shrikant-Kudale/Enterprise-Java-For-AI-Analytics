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

