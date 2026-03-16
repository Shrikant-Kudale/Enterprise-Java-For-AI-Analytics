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

## 4. Interview Q&A

**Q1. synchronized vs ReentrantLock?**  
Same mutual exclusion/visibility; ReentrantLock adds tryLock, timeouts, fairness, multiple conditions and must be unlocked in `finally`.

**Q2. How do you prevent deadlock in a money transfer?**  
Always acquire locks in a total order (e.g. by account id), or use tryLock with back-off.

**Q3. When LongAdder over AtomicLong?**  
Write-heavy counters/metrics where exact value is read rarely.

## 5. Run the Lab

```bash
cd day-16-multithreading-synchronization-and-locks
javac ConcurrencyPrimitives.java
java ConcurrencyPrimitives
```
The program prints benchmark tables and finishes with `Day 16 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
