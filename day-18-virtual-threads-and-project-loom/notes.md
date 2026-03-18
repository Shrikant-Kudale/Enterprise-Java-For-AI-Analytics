# Day 18 - Virtual Threads and Project Loom

> **Phase:** Concurrency | **Date:** 2026-03-18 | **Code:** [`VirtualThreadsLoom.java`](./VirtualThreadsLoom.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Virtual threads** (final in Java 21) are JVM-scheduled threads multiplexed onto a few **carrier** (platform) threads. Blocking I/O or `sleep` *unmounts* the virtual thread; the carrier runs another.
* Model: **one thread per request**, written as plain blocking code - no reactive callbacks. Millions are feasible.
* Create with `Thread.ofVirtual().start(r)` or `Executors.newVirtualThreadPerTaskExecutor()`. **Never pool them**; pool the *scarce resource* (use `Semaphore`).
* Helps I/O-bound workloads only; CPU-bound work still needs cores.
* **Pinning** (Java 21): blocking inside `synchronized` (or native frames) pins the carrier. Prefer `ReentrantLock` in hot blocking paths (fixed in JDK 24).

## 2. Memory Architecture Breakdown

```
Platform thread: ~1 MB reserved stack, OS scheduled         Virtual thread: stack chunks on the HEAP (starts ~1 KB), grows as needed
carrier pool = ForkJoinPool (parallelism = cores)           mount/unmount copies frames to/from heap (cheap, no syscall)
```
ThreadLocals work but cost memory per thread - avoid large ones; ScopedValue is the successor (preview).

## 3. Interview Edge Cases & Pitfalls

1. Do not use thread pools of virtual threads.
2. Pinned carriers + many blocking tasks can stall everything.
3. Unbounded concurrency overloads downstream (DB, model server): add a Semaphore.
4. Thread dumps show only mounted virtual threads; use `jcmd Thread.dump_to_file`.

## 4. Interview Q&A

**Q1. Why are virtual threads cheaper?**  
Tiny heap-allocated stacks and user-mode scheduling; blocking parks the continuation instead of an OS thread.

**Q2. Do virtual threads replace reactive programming?**  
For most request/response services yes: same scalability with simple blocking code and normal stack traces.

**Q3. How do you limit concurrency?**  
A `Semaphore` around the scarce resource, not a smaller thread pool.

## 5. Run the Lab

```bash
cd day-18-virtual-threads-and-project-loom
javac VirtualThreadsLoom.java
java VirtualThreadsLoom
```
The program prints benchmark tables and finishes with `Day 18 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
