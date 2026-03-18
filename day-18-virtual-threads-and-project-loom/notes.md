# Day 18 - Virtual Threads and Project Loom

> **Phase:** Concurrency | **Date:** 2026-03-18 | **Code:** [`VirtualThreadsLoom.java`](./VirtualThreadsLoom.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Virtual threads** (final in Java 21) are JVM-scheduled threads multiplexed onto a few **carrier** (platform) threads. Blocking I/O or `sleep` *unmounts* the virtual thread; the carrier runs another.
* Model: **one thread per request**, written as plain blocking code - no reactive callbacks. Millions are feasible.
* Create with `Thread.ofVirtual().start(r)` or `Executors.newVirtualThreadPerTaskExecutor()`. **Never pool them**; pool the *scarce resource* (use `Semaphore`).
* Helps I/O-bound workloads only; CPU-bound work still needs cores.
* **Pinning** (Java 21): blocking inside `synchronized` (or native frames) pins the carrier. Prefer `ReentrantLock` in hot blocking paths (fixed in JDK 24).

