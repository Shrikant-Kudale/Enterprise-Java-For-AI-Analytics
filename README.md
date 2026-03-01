# Enterprise Java for AI & Analytics

> A 92-day (March - May 2026), interview-driven Java curriculum for **AI & Analytics** students targeting FAANG / Tier-1 backend and AI-platform roles.
> Each day pairs a deep-dive `notes.md` (concepts, memory architecture, edge cases, Q&A) with a **runnable, self-asserting, benchmarked Java program**.

![Java](https://img.shields.io/badge/Java-21%2B-orange) ![Build](https://img.shields.io/badge/build-javac-blue) ![Days](https://img.shields.io/badge/days-92-green) ![License](https://img.shields.io/badge/license-MIT-lightgrey)

**Timeline:** 2026-03-01 -> 2026-05-31 (92 consecutive days: March, April, May) | **Level:** intermediate -> advanced

---

## Why this repository

Top-tier interviews probe *how the platform works* (JVM, collections, concurrency), *how you build services* (Spring, caching, RPC, batch) and *how you ship AI* (tensors, ONNX, DJL, vector search, system design). This repo walks that path in order, and every lab verifies itself:

* Programs end with `Day NN OK` only if all assertions pass.
* Every lab includes a micro-benchmark so you build **numerical intuition**, not just vocabulary.
* Notes follow one template: **Concepts -> Memory architecture -> Edge cases -> Q&A**.

## Tech stack

| Layer | Technology |
|-------|-----------|
| Language / runtime | Java 21 LTS (records, sealed types, pattern matching, virtual threads, sequenced collections) |
| Build | plain `javac` / `java` (zero dependencies for every lab) |
| Platform APIs | `java.nio`, `java.util.concurrent`, `java.net.http`, `com.sun.net.httpserver`, `java.lang.management` |
| Concepts mapped to production libs | Spring Boot 3, Spring Data JPA, HikariCP, Redis (Lettuce), Spring Batch, gRPC / Protobuf, ONNX Runtime, Deep Java Library (DJL), Qdrant / vector databases |
| Notes | GitHub-flavoured Markdown |

> **Design note - dependency-free labs.** Days 19-24 and 26-28 cover frameworks (Spring, Redis, gRPC, ONNX Runtime, DJL, vector DBs).
> To keep every lab runnable with *only a JDK*, each one implements the **core mechanism** of the framework from first principles
> (DI container + HTTP routing, connection pool + N+1 analysis, TTL/LRU cache + RESP wire protocol, chunk-oriented batch engine,
> Protobuf-style binary RPC, ONNX-style graph executor, DJL-style Translator/Predictor + micro-batching, ANN vector index + REST client).
> The matching **production code with Maven coordinates** is in section 5 of each `notes.md`.

## Prerequisites

* JDK **21** or newer (`java -version`)
* Any shell (bash/zsh/PowerShell); no Maven/Gradle needed
* Recommended: a machine with >= 4 cores and 4 GB free RAM for the benchmark labs

## Build & run commands

```bash
# run one day
cd day-01-jvm-memory-and-jit
javac JvmMemoryAndJit.java && java JvmMemoryAndJit

# compile + run every day (stops on the first failing assertion)
for d in day-*/; do
  ( cd "$d" && src=$(ls *.java) && javac "$src" && java "${src%.java}" ) || { echo "FAILED: $d"; break; }
done

# compile only (fast syntax check of the whole repo)
mkdir -p out && javac -d out day-*/*.java

# inspect GC / JIT behaviour of any lab
java -Xlog:gc -XX:+PrintCompilation -Xmx512m JvmMemoryAndJit
```

Windows PowerShell:

```powershell
Get-ChildItem -Directory day-* | ForEach-Object { Push-Location $_; $s = (Get-ChildItem *.java).BaseName; javac "$s.java"; java $s; Pop-Location }
```

## 92-day index

| Day | Date | Phase | Notes | Code |
|----:|------|-------|-------|------|
| 01 | 2026-03-01 | Java Foundations | [JVM Memory Model and JIT Compilation](./day-01-jvm-memory-and-jit/notes.md) | [`JvmMemoryAndJit.java`](./day-01-jvm-memory-and-jit/JvmMemoryAndJit.java) |
| 02 | 2026-03-02 | Java Foundations | [Modern Java Syntax: Records, Sealed Classes, Pattern Matching](./day-02-modern-java-syntax-records-sealed-classes/notes.md) | [`ModernJavaSyntax.java`](./day-02-modern-java-syntax-records-sealed-classes/ModernJavaSyntax.java) |
| 03 | 2026-03-03 | Java Foundations | [OOP and Design Patterns for AI Backends](./day-03-oop-and-design-patterns-for-ai-backends/notes.md) | [`DesignPatternsForAiBackends.java`](./day-03-oop-and-design-patterns-for-ai-backends/DesignPatternsForAiBackends.java) |
| 04 | 2026-03-04 | Java Foundations | [Exception Hierarchies and Clean Architecture](./day-04-exception-hierarchies-and-clean-architecture/notes.md) | [`ExceptionHierarchiesCleanArchitecture.java`](./day-04-exception-hierarchies-and-clean-architecture/ExceptionHierarchiesCleanArchitecture.java) |
| 05 | 2026-03-05 | Java Foundations | [I/O, NIO and Fast Data Serialization](./day-05-io-nio-and-fast-data-serialization/notes.md) | [`FastDataSerialization.java`](./day-05-io-nio-and-fast-data-serialization/FastDataSerialization.java) |
| 06 | 2026-03-06 | Collections & Data Structures | [Generics and Type Erasure](./day-06-generics-and-type-erasure/notes.md) | [`GenericsAndTypeErasure.java`](./day-06-generics-and-type-erasure/GenericsAndTypeErasure.java) |
| 07 | 2026-03-07 | Collections & Data Structures | [Collections: ArrayList and LinkedList Internals](./day-07-collections-arraylist-and-linkedlist-internals/notes.md) | [`ListInternals.java`](./day-07-collections-arraylist-and-linkedlist-internals/ListInternals.java) |
| 08 | 2026-03-08 | Collections & Data Structures | [HashMap, Treeification and ConcurrentHashMap](./day-08-hashmap-treeify-and-concurrenthashmap/notes.md) | [`HashMapInternals.java`](./day-08-hashmap-treeify-and-concurrenthashmap/HashMapInternals.java) |
| 09 | 2026-03-09 | Collections & Data Structures | [Queues, Deques and Priority Heaps](./day-09-queues-deques-and-priority-heaps/notes.md) | [`QueuesDequesHeaps.java`](./day-09-queues-deques-and-priority-heaps/QueuesDequesHeaps.java) |
| 10 | 2026-03-10 | Collections & Data Structures | [Custom Data Structures for Analytics](./day-10-custom-data-structures-for-analytics/notes.md) | [`AnalyticsDataStructures.java`](./day-10-custom-data-structures-for-analytics/AnalyticsDataStructures.java) |
| 11 | 2026-03-11 | Collections & Data Structures | [Comparators, Sorting and Binary Search](./day-11-comparators-sorting-and-binary-search/notes.md) | [`SortingAndSearch.java`](./day-11-comparators-sorting-and-binary-search/SortingAndSearch.java) |
| 12 | 2026-03-12 | Collections & Data Structures | [Big-O Optimization in Java Collections](./day-12-big-o-optimization-in-java-collections/notes.md) | [`BigOCollections.java`](./day-12-big-o-optimization-in-java-collections/BigOCollections.java) |
| 13 | 2026-03-13 | Functional Java & Streams | [Functional Interfaces and Lambdas](./day-13-functional-interfaces-and-lambdas/notes.md) | [`FunctionalInterfacesLambdas.java`](./day-13-functional-interfaces-and-lambdas/FunctionalInterfacesLambdas.java) |
| 14 | 2026-03-14 | Functional Java & Streams | [Stream API Internals and Custom Collectors](./day-14-stream-api-internals-and-custom-collectors/notes.md) | [`StreamInternalsCollectors.java`](./day-14-stream-api-internals-and-custom-collectors/StreamInternalsCollectors.java) |
| 15 | 2026-03-15 | Functional Java & Streams | [Parallel Streams and the Fork/Join Pool](./day-15-parallel-streams-and-forkjoin-pool/notes.md) | [`ParallelForkJoin.java`](./day-15-parallel-streams-and-forkjoin-pool/ParallelForkJoin.java) |
| 16 | 2026-03-16 | Concurrency | [Multithreading, Synchronization and Locks](./day-16-multithreading-synchronization-and-locks/notes.md) | [`ConcurrencyPrimitives.java`](./day-16-multithreading-synchronization-and-locks/ConcurrencyPrimitives.java) |
| 17 | 2026-03-17 | Concurrency | [ExecutorService and CompletableFuture](./day-17-executorservice-and-completablefuture/notes.md) | [`ExecutorsAndFutures.java`](./day-17-executorservice-and-completablefuture/ExecutorsAndFutures.java) |
| 18 | 2026-03-18 | Concurrency | [Virtual Threads and Project Loom](./day-18-virtual-threads-and-project-loom/notes.md) | [`VirtualThreadsLoom.java`](./day-18-virtual-threads-and-project-loom/VirtualThreadsLoom.java) |
| 19 | 2026-03-19 | Enterprise Backend Services | [Spring Boot 3 Microservices Architecture](./day-19-spring-boot-3-microservices-architecture/notes.md) | [`MiniBootMicroservice.java`](./day-19-spring-boot-3-microservices-architecture/MiniBootMicroservice.java) |
| 20 | 2026-03-20 | Enterprise Backend Services | [Spring Data JPA and HikariCP Optimization](./day-20-spring-data-jpa-and-hikaricp-optimization/notes.md) | [`PoolAndJpaPatterns.java`](./day-20-spring-data-jpa-and-hikaricp-optimization/PoolAndJpaPatterns.java) |
| 21 | 2026-03-21 | Enterprise Backend Services | [REST API Design and Validation](./day-21-rest-api-design-and-validation/notes.md) | [`RestValidation.java`](./day-21-rest-api-design-and-validation/RestValidation.java) |
| 22 | 2026-03-22 | Enterprise Backend Services | [Redis Caching for High Throughput](./day-22-redis-caching-for-high-throughput/notes.md) | [`CacheAndResp.java`](./day-22-redis-caching-for-high-throughput/CacheAndResp.java) |
| 23 | 2026-03-23 | Enterprise Backend Services | [Spring Batch for Massive Dataset Processing](./day-23-spring-batch-for-massive-dataset-processing/notes.md) | [`ChunkBatchEngine.java`](./day-23-spring-batch-for-massive-dataset-processing/ChunkBatchEngine.java) |
| 24 | 2026-03-24 | Enterprise Backend Services | [gRPC for Low-Latency Microservice RPC](./day-24-grpc-for-low-latency-microservice-rpc/notes.md) | [`BinaryRpc.java`](./day-24-grpc-for-low-latency-microservice-rpc/BinaryRpc.java) |
| 25 | 2026-03-25 | AI / ML Integration | [Vector and Matrix Manipulation in Java](./day-25-vector-and-matrix-manipulation-in-java/notes.md) | [`MatrixOps.java`](./day-25-vector-and-matrix-manipulation-in-java/MatrixOps.java) |
| 26 | 2026-03-26 | AI / ML Integration | [ONNX Runtime Integration in Java](./day-26-onnx-runtime-integration-in-java/notes.md) | [`MiniOnnxRuntime.java`](./day-26-onnx-runtime-integration-in-java/MiniOnnxRuntime.java) |
| 27 | 2026-03-27 | AI / ML Integration | [Deep Java Library (DJL) Inference Pipeline](./day-27-deep-java-library-djl-inference-pipeline/notes.md) | [`DjlStylePipeline.java`](./day-27-deep-java-library-djl-inference-pipeline/DjlStylePipeline.java) |
| 28 | 2026-03-28 | AI / ML Integration | [Vector Database Client Integration](./day-28-vector-database-client-integration/notes.md) | [`VectorSearchClient.java`](./day-28-vector-database-client-integration/VectorSearchClient.java) |
| 29 | 2026-03-29 | System Design & Interview Capstone | [Enterprise System Design: AI Microservice](./day-29-enterprise-system-design-ai-microservice/notes.md) | [`ResilientGateway.java`](./day-29-enterprise-system-design-ai-microservice/ResilientGateway.java) |
| 30 | 2026-03-30 | System Design & Interview Capstone | [FAANG Interview Deep Dive and Mock Round](./day-30-faang-interview-deep-dive-and-mock-round/notes.md) | [`MockInterviewRound.java`](./day-30-faang-interview-deep-dive-and-mock-round/MockInterviewRound.java) |
| 31 | 2026-03-31 | Java Core Extras & JVM Tooling | [String Internals and Interning](./day-31-string-internals-and-interning/notes.md) | [`StringInternals.java`](./day-31-string-internals-and-interning/StringInternals.java) |
| 32 | 2026-04-01 | Java Core Extras & JVM Tooling | [Optional and Null Safety](./day-32-optional-and-null-safety/notes.md) | [`OptionalNullSafety.java`](./day-32-optional-and-null-safety/OptionalNullSafety.java) |
| 33 | 2026-04-02 | Java Core Extras & JVM Tooling | [Immutability and Defensive Copying](./day-33-immutability-and-defensive-copying/notes.md) | [`ImmutabilityCopies.java`](./day-33-immutability-and-defensive-copying/ImmutabilityCopies.java) |
| 34 | 2026-04-03 | Java Core Extras & JVM Tooling | [equals, hashCode and Object Contracts](./day-34-equals-hashcode-and-object-contracts/notes.md) | [`EqualsHashCodeContract.java`](./day-34-equals-hashcode-and-object-contracts/EqualsHashCodeContract.java) |
| 35 | 2026-04-04 | Java Core Extras & JVM Tooling | [Enums and State Machines](./day-35-enums-and-state-machines/notes.md) | [`EnumStateMachine.java`](./day-35-enums-and-state-machines/EnumStateMachine.java) |
| 36 | 2026-04-05 | Java Core Extras & JVM Tooling | [Interfaces, Default Methods and Mixins](./day-36-interfaces-default-methods-and-mixins/notes.md) | [`DefaultMethodsMixins.java`](./day-36-interfaces-default-methods-and-mixins/DefaultMethodsMixins.java) |
| 37 | 2026-04-06 | Java Core Extras & JVM Tooling | [Nested Classes and Variable Capture](./day-37-nested-classes-and-capture/notes.md) | [`NestedClassCapture.java`](./day-37-nested-classes-and-capture/NestedClassCapture.java) |
| 38 | 2026-04-07 | Java Core Extras & JVM Tooling | [Reflection and Annotations](./day-38-reflection-and-annotations/notes.md) | [`ReflectionAnnotations.java`](./day-38-reflection-and-annotations/ReflectionAnnotations.java) |
| 39 | 2026-04-08 | Java Core Extras & JVM Tooling | [java.time API and Time Series](./day-39-java-time-api-and-time-series/notes.md) | [`JavaTimeSeries.java`](./day-39-java-time-api-and-time-series/JavaTimeSeries.java) |
| 40 | 2026-04-09 | Java Core Extras & JVM Tooling | [BigDecimal and Numeric Precision](./day-40-bigdecimal-and-numeric-precision/notes.md) | [`BigDecimalPrecision.java`](./day-40-bigdecimal-and-numeric-precision/BigDecimalPrecision.java) |
| 41 | 2026-04-10 | Java Core Extras & JVM Tooling | [Bit Manipulation and BitSets](./day-41-bit-manipulation-and-bitsets/notes.md) | [`BitManipulation.java`](./day-41-bit-manipulation-and-bitsets/BitManipulation.java) |
| 42 | 2026-04-11 | Java Core Extras & JVM Tooling | [Regex and Tokenization](./day-42-text-processing-regex-and-tokenization/notes.md) | [`RegexTokenization.java`](./day-42-text-processing-regex-and-tokenization/RegexTokenization.java) |
| 43 | 2026-04-12 | Java Core Extras & JVM Tooling | [JSON Parsing from Scratch](./day-43-json-parsing-from-scratch/notes.md) | [`JsonParserLite.java`](./day-43-json-parsing-from-scratch/JsonParserLite.java) |
| 44 | 2026-04-13 | Java Core Extras & JVM Tooling | [CSV Streaming and Large Files](./day-44-csv-streaming-and-large-files/notes.md) | [`CsvStreaming.java`](./day-44-csv-streaming-and-large-files/CsvStreaming.java) |
| 45 | 2026-04-14 | Java Core Extras & JVM Tooling | [Logging and Structured Observability](./day-45-logging-and-structured-observability/notes.md) | [`StructuredLogging.java`](./day-45-logging-and-structured-observability/StructuredLogging.java) |
| 46 | 2026-04-15 | Java Core Extras & JVM Tooling | [Unit Testing and Property Tests](./day-46-unit-testing-assertions-and-property-tests/notes.md) | [`MiniPropertyTests.java`](./day-46-unit-testing-assertions-and-property-tests/MiniPropertyTests.java) |
| 47 | 2026-04-16 | Java Core Extras & JVM Tooling | [JMH-style Benchmarking Pitfalls](./day-47-jmh-style-benchmarking-pitfalls/notes.md) | [`BenchmarkPitfalls.java`](./day-47-jmh-style-benchmarking-pitfalls/BenchmarkPitfalls.java) |
| 48 | 2026-04-17 | Java Core Extras & JVM Tooling | [Garbage Collection: Mark-Sweep Simulation](./day-48-garbage-collection-algorithms-simulation/notes.md) | [`MarkSweepSimulation.java`](./day-48-garbage-collection-algorithms-simulation/MarkSweepSimulation.java) |
| 49 | 2026-04-18 | Java Core Extras & JVM Tooling | [Class Loading Basics](./day-49-classloading-and-service-loader/notes.md) | [`ClassLoadingBasics.java`](./day-49-classloading-and-service-loader/ClassLoadingBasics.java) |
| 50 | 2026-04-19 | Java Core Extras & JVM Tooling | [Memory Leaks, Weak References and Caches](./day-50-memory-leaks-weak-references-and-caches/notes.md) | [`WeakReferenceCaches.java`](./day-50-memory-leaks-weak-references-and-caches/WeakReferenceCaches.java) |
| 51 | 2026-04-20 | Java Core Extras & JVM Tooling | [JVM Flags and Heap Sizing](./day-51-jvm-flags-and-tuning-cheatsheet/notes.md) | [`HeapSizingMath.java`](./day-51-jvm-flags-and-tuning-cheatsheet/HeapSizingMath.java) |
| 52 | 2026-04-21 | Java Core Extras & JVM Tooling | [Profiling, JFR and Thread Dumps](./day-52-profiling-jfr-and-thread-dumps/notes.md) | [`ThreadDumpsProfiling.java`](./day-52-profiling-jfr-and-thread-dumps/ThreadDumpsProfiling.java) |
| 53 | 2026-04-22 | Algorithms & Data Structures Drills | [Two Pointers and Sliding Window](./day-53-two-pointers-and-sliding-window/notes.md) | [`SlidingWindowPatterns.java`](./day-53-two-pointers-and-sliding-window/SlidingWindowPatterns.java) |
| 54 | 2026-04-23 | Algorithms & Data Structures Drills | [Prefix Sums and Difference Arrays](./day-54-prefix-sums-and-difference-arrays/notes.md) | [`PrefixSumsDiffArrays.java`](./day-54-prefix-sums-and-difference-arrays/PrefixSumsDiffArrays.java) |
| 55 | 2026-04-24 | Algorithms & Data Structures Drills | [Graph BFS, DFS and Topological Sort](./day-55-graph-bfs-dfs-and-topological-sort/notes.md) | [`GraphTopologicalSort.java`](./day-55-graph-bfs-dfs-and-topological-sort/GraphTopologicalSort.java) |
| 56 | 2026-04-25 | Algorithms & Data Structures Drills | [Dijkstra and Shortest Paths](./day-56-dijkstra-and-shortest-paths/notes.md) | [`DijkstraShortestPath.java`](./day-56-dijkstra-and-shortest-paths/DijkstraShortestPath.java) |
| 57 | 2026-04-26 | Algorithms & Data Structures Drills | [Union-Find and Connected Components](./day-57-union-find-and-connected-components/notes.md) | [`UnionFindComponents.java`](./day-57-union-find-and-connected-components/UnionFindComponents.java) |
| 58 | 2026-04-27 | Algorithms & Data Structures Drills | [Tries and Autocomplete](./day-58-tries-and-autocomplete/notes.md) | [`TrieAutocomplete.java`](./day-58-tries-and-autocomplete/TrieAutocomplete.java) |
| 59 | 2026-04-28 | Algorithms & Data Structures Drills | [Dynamic Programming Patterns](./day-59-dynamic-programming-patterns/notes.md) | [`DynamicProgrammingPatterns.java`](./day-59-dynamic-programming-patterns/DynamicProgrammingPatterns.java) |
| 60 | 2026-04-29 | Algorithms & Data Structures Drills | [Backtracking and Combinatorics](./day-60-backtracking-and-combinatorics/notes.md) | [`BacktrackingPermutations.java`](./day-60-backtracking-and-combinatorics/BacktrackingPermutations.java) |
| 61 | 2026-04-30 | Algorithms & Data Structures Drills | [Intervals and Sweep Line](./day-61-intervals-and-sweep-line/notes.md) | [`IntervalsSweepLine.java`](./day-61-intervals-and-sweep-line/IntervalsSweepLine.java) |
| 62 | 2026-05-01 | Backend Systems & Security | [HTTP Client, Timeouts and Retries](./day-62-http-client-and-timeouts-retries/notes.md) | [`RetryBackoffClient.java`](./day-62-http-client-and-timeouts-retries/RetryBackoffClient.java) |
| 63 | 2026-05-02 | Backend Systems & Security | [Transactions and Isolation Levels](./day-63-transactions-and-isolation-levels/notes.md) | [`TransactionIsolationToy.java`](./day-63-transactions-and-isolation-levels/TransactionIsolationToy.java) |
| 64 | 2026-05-03 | Backend Systems & Security | [Optimistic Locking and Versioning](./day-64-optimistic-locking-and-versioning/notes.md) | [`OptimisticVersioning.java`](./day-64-optimistic-locking-and-versioning/OptimisticVersioning.java) |
| 65 | 2026-05-04 | Backend Systems & Security | [Idempotency and Exactly-Once Semantics](./day-65-idempotency-and-exactly-once-semantics/notes.md) | [`IdempotencyKeys.java`](./day-65-idempotency-and-exactly-once-semantics/IdempotencyKeys.java) |
| 66 | 2026-05-05 | Backend Systems & Security | [Message Queues and Consumer Groups](./day-66-message-queues-and-consumer-groups/notes.md) | [`PartitionedLogToy.java`](./day-66-message-queues-and-consumer-groups/PartitionedLogToy.java) |
| 67 | 2026-05-06 | Backend Systems & Security | [Event Sourcing and CQRS](./day-67-event-sourcing-and-cqrs/notes.md) | [`EventSourcingReplay.java`](./day-67-event-sourcing-and-cqrs/EventSourcingReplay.java) |
| 68 | 2026-05-07 | Backend Systems & Security | [Rate Limiting Algorithms](./day-68-rate-limiting-algorithms/notes.md) | [`TokenBucketLimiter.java`](./day-68-rate-limiting-algorithms/TokenBucketLimiter.java) |
| 69 | 2026-05-08 | Backend Systems & Security | [Consistent Hashing and Sharding](./day-69-consistent-hashing-and-sharding/notes.md) | [`ConsistentHashRing.java`](./day-69-consistent-hashing-and-sharding/ConsistentHashRing.java) |
| 70 | 2026-05-09 | Backend Systems & Security | [Bulkhead and Backpressure](./day-70-bulkhead-and-backpressure/notes.md) | [`BulkheadBackpressure.java`](./day-70-bulkhead-and-backpressure/BulkheadBackpressure.java) |
| 71 | 2026-05-10 | Backend Systems & Security | [API Security: HMAC and JWT](./day-71-api-security-jwt-and-hmac/notes.md) | [`HmacSignedTokens.java`](./day-71-api-security-jwt-and-hmac/HmacSignedTokens.java) |
| 72 | 2026-05-11 | Backend Systems & Security | [Password Hashing and Secrets](./day-72-password-hashing-and-secrets/notes.md) | [`Pbkdf2Passwords.java`](./day-72-password-hashing-and-secrets/Pbkdf2Passwords.java) |
| 73 | 2026-05-12 | Backend Systems & Security | [Metrics, Histograms and Percentiles](./day-73-observability-metrics-histograms-and-percentiles/notes.md) | [`LatencyPercentiles.java`](./day-73-observability-metrics-histograms-and-percentiles/LatencyPercentiles.java) |
| 74 | 2026-05-13 | Backend Systems & Security | [Distributed ID Generation (Snowflake)](./day-74-distributed-id-generation-snowflake/notes.md) | [`SnowflakeIds.java`](./day-74-distributed-id-generation-snowflake/SnowflakeIds.java) |
| 75 | 2026-05-14 | Backend Systems & Security | [Leader Election and Heartbeats](./day-75-leader-election-and-heartbeats/notes.md) | [`HeartbeatLeaderElection.java`](./day-75-leader-election-and-heartbeats/HeartbeatLeaderElection.java) |
| 76 | 2026-05-15 | Backend Systems & Security | [Docker and Container Memory Limits](./day-76-docker-and-container-memory-limits/notes.md) | [`ContainerMemoryLimits.java`](./day-76-docker-and-container-memory-limits/ContainerMemoryLimits.java) |
| 77 | 2026-05-16 | Machine Learning Foundations & Serving | [Linear Regression with Gradient Descent](./day-77-linear-regression-gradient-descent/notes.md) | [`LinearRegressionGD.java`](./day-77-linear-regression-gradient-descent/LinearRegressionGD.java) |
| 78 | 2026-05-17 | Machine Learning Foundations & Serving | [Logistic Regression from Scratch](./day-78-logistic-regression-from-scratch/notes.md) | [`LogisticRegressionGD.java`](./day-78-logistic-regression-from-scratch/LogisticRegressionGD.java) |
| 79 | 2026-05-18 | Machine Learning Foundations & Serving | [K-Means Clustering](./day-79-k-means-clustering/notes.md) | [`KMeansLite.java`](./day-79-k-means-clustering/KMeansLite.java) |
| 80 | 2026-05-19 | Machine Learning Foundations & Serving | [Decision Trees and Gini Impurity](./day-80-decision-tree-gini/notes.md) | [`GiniSplit.java`](./day-80-decision-tree-gini/GiniSplit.java) |
| 81 | 2026-05-20 | Machine Learning Foundations & Serving | [Naive Bayes Text Classifier](./day-81-naive-bayes-text-classifier/notes.md) | [`NaiveBayesLite.java`](./day-81-naive-bayes-text-classifier/NaiveBayesLite.java) |
| 82 | 2026-05-21 | Machine Learning Foundations & Serving | [Evaluation Metrics: Precision, Recall, AUC](./day-82-evaluation-metrics-precision-recall-auc/notes.md) | [`ClassificationMetrics.java`](./day-82-evaluation-metrics-precision-recall-auc/ClassificationMetrics.java) |
| 83 | 2026-05-22 | Machine Learning Foundations & Serving | [Feature Engineering: One-Hot and Hashing](./day-83-feature-engineering-one-hot-and-hashing/notes.md) | [`FeatureHashing.java`](./day-83-feature-engineering-one-hot-and-hashing/FeatureHashing.java) |
| 84 | 2026-05-23 | Machine Learning Foundations & Serving | [Embeddings, Cosine Similarity and kNN](./day-84-embeddings-cosine-similarity-and-knn/notes.md) | [`CosineNearestNeighbor.java`](./day-84-embeddings-cosine-similarity-and-knn/CosineNearestNeighbor.java) |
| 85 | 2026-05-24 | Machine Learning Foundations & Serving | [TF-IDF and BM25 Search](./day-85-tf-idf-and-bm25-search/notes.md) | [`TfIdfWeights.java`](./day-85-tf-idf-and-bm25-search/TfIdfWeights.java) |
| 86 | 2026-05-25 | Machine Learning Foundations & Serving | [Neural Network Backprop and Gradient Check](./day-86-neural-network-forward-and-backprop-gradient-check/notes.md) | [`BackpropGradientCheck.java`](./day-86-neural-network-forward-and-backprop-gradient-check/BackpropGradientCheck.java) |
| 87 | 2026-05-26 | Machine Learning Foundations & Serving | [Model Serving: Batching and Latency Budgets](./day-87-model-serving-batching-and-latency-budgets/notes.md) | [`ServingBatchBudget.java`](./day-87-model-serving-batching-and-latency-budgets/ServingBatchBudget.java) |
| 88 | 2026-05-27 | Machine Learning Foundations & Serving | [A/B Testing and Statistics](./day-88-ab-testing-and-statistics/notes.md) | [`AbTestZScore.java`](./day-88-ab-testing-and-statistics/AbTestZScore.java) |
| 89 | 2026-05-28 | Machine Learning Foundations & Serving | [Data Drift: PSI and KS](./day-89-data-drift-psi-and-ks/notes.md) | [`PsiDriftMonitor.java`](./day-89-data-drift-psi-and-ks/PsiDriftMonitor.java) |
| 90 | 2026-05-29 | Mock Interviews & Wrap-up | [Mock Coding Round: Drills](./day-90-mock-interview-coding-round-set/notes.md) | [`CodingRoundDrills.java`](./day-90-mock-interview-coding-round-set/CodingRoundDrills.java) |
| 91 | 2026-05-30 | Mock Interviews & Wrap-up | [Mock System Design: Capacity Math](./day-91-mock-interview-system-design-round/notes.md) | [`SystemDesignMath.java`](./day-91-mock-interview-system-design-round/SystemDesignMath.java) |
| 92 | 2026-05-31 | Mock Interviews & Wrap-up | [Final Review and Portfolio Wrap-up](./day-92-final-review-and-portfolio-wrap-up/notes.md) | [`FinalReviewWrapUp.java`](./day-92-final-review-and-portfolio-wrap-up/FinalReviewWrapUp.java) |

## Repository layout

```
enterprise-java-for-ai-analytics/
|-- README.md
|-- metadata.json                  # folder -> backdate + commit message
|-- day-01-jvm-memory-and-jit/
|   |-- notes.md                   # concepts, memory architecture, edge cases, Q&A
|   `-- JvmMemoryAndJit.java       # production-style lab: main + assertions + benchmark
`-- ... (92 folders: day-01 ... day-92)
```

## Study protocol (90-120 min/day)

1. **Read** `notes.md` sections 1-2 (30 min). Draw the memory diagram from memory.
2. **Run** the lab, read the benchmark output, then modify one parameter and predict the effect (30 min).
3. **Break it**: delete a `volatile`, shrink a buffer, change a hash - observe the failure (15 min).
4. **Speak** the Q&A answers aloud (15-20 min). Record yourself on days 29-30.
5. Weekly: re-do the previous week's labs from a blank file in 45 minutes.

## Using `metadata.json` to reproduce the 30-day commit history

`metadata.json` maps each folder to its backdate and a Conventional-Commit message.

```bash
git init && git add README.md metadata.json
GIT_AUTHOR_DATE="2026-05-01T09:00:00+05:30" GIT_COMMITTER_DATE="2026-05-01T09:00:00+05:30" \
  git commit -m "chore: initialise enterprise-java-for-ai-analytics"

jq -r 'to_entries[] | [.key, .value.git_date, .value.commit_message] | @tsv' metadata.json |
while IFS=$'\t' read -r dir date msg; do
  git add "$dir"
  GIT_AUTHOR_DATE="$date" GIT_COMMITTER_DATE="$date" git commit -m "$msg"
done
```

## Backdated commit pipeline (run locally)

`pipeline.py` (in the ZIP, not in the repo) creates one backdated commit history for all 92 days. See `HOW_TO_RUN.md`.

## Contributing

Open an issue for a correction or a new interview question. Keep labs dependency-free and self-asserting.

## License

MIT - use freely for learning and interview preparation.
