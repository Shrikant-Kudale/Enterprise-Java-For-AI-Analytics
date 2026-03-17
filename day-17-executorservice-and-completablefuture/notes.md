# Day 17 - ExecutorService and CompletableFuture

> **Phase:** Concurrency | **Date:** 2026-03-17 | **Code:** [`ExecutorsAndFutures.java`](./ExecutorsAndFutures.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **ThreadPoolExecutor** = core size, max size, work queue, keep-alive, `ThreadFactory`, `RejectedExecutionHandler`. Order: fill core threads -> queue -> grow to max -> reject.
* Use **bounded queues** + a rejection policy (`Abort`, `CallerRuns` = back-pressure, `Discard`). Unbounded queues hide overload until OOM.
* `submit` captures exceptions in the `Future`; `execute` hands them to the thread's uncaught handler.
* **CompletableFuture**: `supplyAsync`, `thenApply/thenCompose/thenCombine`, `allOf/anyOf`, `orTimeout/completeOnTimeout`, `exceptionally/handle`. Pass your own executor; the default is the common ForkJoinPool.
* Scatter-gather with per-replica timeout + fallback keeps p99 bounded when one model replica is slow.

## 2. Memory Architecture Breakdown

```
task -> [core threads] -> [BlockingQueue] -> [extra threads up to max] -> RejectedExecutionHandler
CompletableFuture = result/exception field + Completion stack (linked list of dependents, CAS-pushed)
```
Each stage allocates a future + completion node; deep chains on huge fan-outs create garbage - batch the work.

## 3. Interview Edge Cases & Pitfalls

1. Blocking calls on the common pool starve every async stage in the JVM.
2. `CallerRunsPolicy` slows the producer deliberately - that is the back-pressure.
3. `allOf` completes with `null`; join each future to read values.
4. Always `shutdown()` + `awaitTermination`; non-daemon pool threads keep the JVM alive.

## 4. Interview Q&A

**Q1. When does a ThreadPoolExecutor create threads beyond core size?**  
Only when the queue is full; with an unbounded queue max size is never used.

**Q2. How do you add a timeout to a CompletableFuture?**  
`orTimeout` (fail) or `completeOnTimeout` (fallback value), Java 9+.

**Q3. thenApply vs thenCompose?**  
thenApply maps a value; thenCompose flattens a function returning another future (like flatMap).

