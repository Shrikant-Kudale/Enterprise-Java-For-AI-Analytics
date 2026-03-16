# Day 16 - Multithreading, Synchronization and Locks

> **Phase:** Concurrency | **Date:** 2026-03-16 | **Code:** [`ConcurrencyPrimitives.java`](./ConcurrencyPrimitives.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Java Memory Model**: without `volatile`/locks, one thread's writes may never become visible to another; `happens-before` edges (lock release->acquire, volatile write->read, `Thread.start/join`) define visibility.
* **Atomicity**: `i++` is read-modify-write, not atomic. Fix with `synchronized`, `ReentrantLock`, `AtomicLong` (CAS) or `LongAdder` (striped cells, best under heavy contention).
* **Deadlock** needs mutual exclusion + hold-and-wait + no preemption + circular wait. Break circular wait with a global **lock order**; or use `tryLock(timeout)`.
* `ReentrantLock` adds `tryLock`, fairness, `Condition`s, interruptible locking; `ReadWriteLock`/`StampedLock` favour read-heavy data.

## 2. Memory Architecture Breakdown

```
synchronized -> object header mark word (thin lock CAS -> inflated ObjectMonitor under contention)
AtomicLong   -> one volatile long + CAS loop; LongAdder -> Cell[] (padded, @Contended) summed on read
```
Contended CAS on one cache line bounces it between cores; striping (LongAdder) trades read cost for write scalability.

## 3. Interview Edge Cases & Pitfalls

1. `volatile` gives visibility, not atomicity (`count++` still races).
2. Double-checked locking is only correct with a `volatile` field.
3. Never call alien methods while holding a lock (deadlock/latency).
4. `wait()` must be in a `while` loop (spurious wakeups).

