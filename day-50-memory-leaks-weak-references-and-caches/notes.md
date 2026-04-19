# Day 50 - Memory Leaks, Weak References and Caches

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-19 | **Code:** [`WeakReferenceCaches.java`](./WeakReferenceCaches.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Leaks in Java = *unintended reachability*: static maps, listeners, ThreadLocals, unbounded caches. Use bounded caches (Caffeine), `WeakHashMap` for canonical maps, `try-with-resources`, and heap dumps (MAT) to find dominators.

## 2. Memory Architecture Breakdown

Reference strength: strong > soft (cleared under memory pressure) > weak (next GC) > phantom (cleanup hooks).

## 3. Interview Edge Cases & Pitfalls

1. GC timing is not guaranteed - never assert on it.
2. ThreadLocals in pools outlive requests.

## 4. Interview Q&A

**Q1. How to find a leak?**  
Compare heap dumps; inspect the dominator tree and GC roots.

**Q2. WeakHashMap use?**  
Metadata keyed by objects you do not want to keep alive.

## 5. Run the Lab

```bash
cd day-50-memory-leaks-weak-references-and-caches
javac WeakReferenceCaches.java
java WeakReferenceCaches
```
The program prints benchmark tables and finishes with `Day 50 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
