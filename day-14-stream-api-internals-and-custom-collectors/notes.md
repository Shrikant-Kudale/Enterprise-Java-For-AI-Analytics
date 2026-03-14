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

## 2. Memory Architecture Breakdown

```
list.stream().filter(p).map(f).limit(3).collect(c)

 ReferenceSpliterator(list) -> Head -> StatelessOp(filter) -> StatelessOp(map) -> SliceOp(limit) -> ReduceOp(collect)
 wrapSink() builds:   FilterSink -> MapSink -> LimitSink -> CollectorSink    (one pass, no temp lists)

Allocation per pipeline: ~10-15 small objects (ops, sinks, spliterator). Per *element*: only boxed values / lambda results.
Stream<Integer> boxed sum: 16B Integer per element read + unbox;  IntStream: zero allocation.
```
* `sorted()` buffers the whole stream in an array/ArrayList (O(n) memory); `distinct()` uses a hash set (O(n)); `limit` after `sorted` can't avoid the full sort.
* `Collectors.toList()` uses `ArrayList` with default growth; `Stream.toList()` (16+) builds from `toArray` (exact size, unmodifiable).
* Parallel `groupingBy` merges per-thread maps (copying); `groupingByConcurrent` writes into one `ConcurrentHashMap` (less merging, unordered).

## 3. Interview Edge Cases & Pitfalls

1. `peek` is for debugging only: with `count()` on a SIZED source the JDK 9+ may *skip* the pipeline entirely, so peek never runs.
2. Side effects in `map/filter/forEach` on a parallel stream (adding to a shared ArrayList) are data races - use `collect`.
3. `Collectors.toMap` throws `IllegalStateException` on duplicate keys unless a merge function is given; and NPE on null values.
4. `Stream.iterate(seed, f)` without `limit/takeWhile` never terminates; `Stream.generate` + `limit` on parallel streams wastes work.
5. `flatMap` closes each inner stream, but a lazily-evaluated `flatMap` didn't short-circuit before JDK 10 (fixed).
6. `IntStream.range(0, n).boxed().collect(toList())` boxes n integers - prefer `toArray`.
7. `sorted()` is stable only for ordered streams; `forEach` doesn't preserve order in parallel (`forEachOrdered` does, at a cost).
8. Custom collector without a correct combiner passes sequential tests and breaks in parallel.

