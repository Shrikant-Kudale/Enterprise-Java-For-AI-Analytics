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

