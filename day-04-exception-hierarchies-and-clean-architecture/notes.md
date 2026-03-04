# Day 04 - Exception Hierarchies and Clean Architecture

> **Phase:** Java Foundations | **Date:** 2026-03-04 | **Code:** [`ExceptionHierarchiesCleanArchitecture.java`](./ExceptionHierarchiesCleanArchitecture.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Throwable taxonomy
`Throwable` -> `Error` (JVM failures; don't catch: OOM, StackOverflow, LinkageError) and `Exception` -> *checked* (compiler-enforced) / `RuntimeException` (unchecked). Modern practice: **checked for recoverable boundary failures that callers must consider** (I/O at an adapter edge), **unchecked for programming errors and domain failures** propagated to a central handler.

### Clean / Hexagonal architecture
```
 [ Controller / gRPC ]  -->  [ Use case (application) ]  -->  [ Domain ]
        adapter                  depends on ports only          pure rules
                                       |
                              ports: FeatureStore, ScoringModel
                                       ^
                       adapters: JDBC, Redis, ONNX, HTTP (translate exceptions)
```
*Dependencies point inwards.* Adapters **translate** infrastructure exceptions (`SQLException`, `IOException`, `SocketTimeoutException`) into domain exceptions so the core never imports a framework type. The outermost layer maps domain exceptions to protocol codes (HTTP 400/404/502/503, gRPC `INVALID_ARGUMENT`, ...).

### Design rules
1. Sealed domain hierarchy -> exhaustive mapping to status codes.
2. Carry a stable **machine-readable code** (`VALIDATION`, `UPSTREAM`) and a human message.
3. Preserve the **cause**; never swallow; never `catch (Exception e) {}`.
4. Don't use exceptions for ordinary control flow in hot paths - or disable stack-trace capture for expected failures.
5. `Result<T>` (Ok | Err) for expected business failures inside a pipeline; exceptions for the exceptional.
6. try-with-resources: the *primary* exception wins, close() failures are attached via `addSuppressed`.

### Retry semantics
Mark exceptions `retryable` (timeouts, 503) vs not (validation). Retry only idempotent operations, with exponential backoff + jitter and an overall deadline.

