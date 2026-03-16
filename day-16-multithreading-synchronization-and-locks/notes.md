# Day 16 - Multithreading, Synchronization and Locks

> **Phase:** Concurrency | **Date:** 2026-03-16 | **Code:** [`ConcurrencyPrimitives.java`](./ConcurrencyPrimitives.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Java Memory Model**: without `volatile`/locks, one thread's writes may never become visible to another; `happens-before` edges (lock release->acquire, volatile write->read, `Thread.start/join`) define visibility.
* **Atomicity**: `i++` is read-modify-write, not atomic. Fix with `synchronized`, `ReentrantLock`, `AtomicLong` (CAS) or `LongAdder` (striped cells, best under heavy contention).
* **Deadlock** needs mutual exclusion + hold-and-wait + no preemption + circular wait. Break circular wait with a global **lock order**; or use `tryLock(timeout)`.
* `ReentrantLock` adds `tryLock`, fairness, `Condition`s, interruptible locking; `ReadWriteLock`/`StampedLock` favour read-heavy data.

