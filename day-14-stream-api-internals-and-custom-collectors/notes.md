# Day 14 - Stream API Internals and Custom Collectors

> **Phase:** Functional Java & Streams | **Date:** 2026-03-14 | **Code:** [`StreamInternalsCollectors.java`](./StreamInternalsCollectors.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Pipeline anatomy
`source -> intermediate ops (lazy) -> terminal op (triggers execution)`. Nothing runs until the terminal op. Internally each op is a **`Sink`** in a chain (`Sink.begin / accept / end / cancellationRequested`); the pipeline is evaluated by *pushing each element through the whole chain* (loop fusion) - no intermediate collections.

* **Stateless** ops (`map`, `filter`, `flatMap`, `peek`) vs **stateful** (`sorted`, `distinct`, `limit`, `skip`) - stateful ops are barriers that may buffer everything (`sorted`).
* **Short-circuiting**: `findFirst`, `anyMatch`, `limit`, `takeWhile` stop the upstream early - which lets infinite streams (`Stream.iterate`) work.
* **Characteristics** (`ORDERED, SIZED, SORTED, DISTINCT, NONNULL, IMMUTABLE, CONCURRENT`) let the framework skip work (e.g. `sorted()` on an already `SORTED` stream is a no-op, `SIZED` allows exact array pre-allocation in `toArray`).
* A stream can be consumed **once** (`IllegalStateException: stream has already been operated upon`).

### Spliterator
`tryAdvance`, `forEachRemaining`, `trySplit` (for parallelism), `estimateSize`, `characteristics`. ArrayList/array splits perfectly in half (great for parallel), `LinkedList`/`Stream.iterate` split poorly.

### Collectors
`Collector<T, A, R>` = `supplier` (A), `accumulator` (A,T), `combiner` (A,A -> A, for parallel), `finisher` (A -> R), `characteristics` (`CONCURRENT`, `UNORDERED`, `IDENTITY_FINISH`). Rules: the combiner must be **associative**; accumulation into the container is single-threaded per container.
Built-ins worth knowing: `groupingBy(classifier, mapFactory, downstream)`, `partitioningBy`, `toMap(k,v,merge)`, `teeing(c1,c2,merger)` (Java 12), `flatMapping`, `filtering`, `summarizingDouble`, `collectingAndThen`.

### Primitive streams
`IntStream/LongStream/DoubleStream` avoid boxing; `mapToObj`, `boxed`, `summaryStatistics`, `range`. `mapMulti` (16) is an allocation-light alternative to `flatMap` for small fan-outs.

### When *not* to use streams
Hot inner loops over primitives (loop is simpler and ~as fast or faster), code with checked exceptions/mutation, debugging-heavy logic. Streams shine for declarative data transformation and when parallelism may help (day 15).

### AI/analytics relevance
Batch feature aggregation, group-by metrics per model/tenant, rolling-window features (sliding windows), top-K collectors, histograms for drift detection - all expressible as collectors that also work in parallel.

