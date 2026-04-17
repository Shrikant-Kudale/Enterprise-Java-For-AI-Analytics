# Day 48 - Garbage Collection: Mark-Sweep Simulation

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-17 | **Code:** [`MarkSweepSimulation.java`](./MarkSweepSimulation.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Tracing GC: **mark** everything reachable from roots, **sweep** (or copy/compact) the rest. Cycles are collected (unlike ref counting). Generational hypothesis: most objects die young. G1/ZGC add regions and concurrency.

## 2. Memory Architecture Breakdown

Roots = thread stacks, statics, JNI refs. Mark bits live in the header or side bitmaps.

## 3. Interview Edge Cases & Pitfalls

1. Unreachable cycles are garbage.
2. Long-lived caches keep objects reachable (leaks).

## 4. Interview Q&A

**Q1. Mark-sweep vs copying?**  
Copying compacts and allocates fast; mark-sweep needs free lists.

**Q2. What is a GC root?**  
A reference the GC treats as live by definition.

## 5. Run the Lab

```bash
cd day-48-garbage-collection-algorithms-simulation
javac MarkSweepSimulation.java
java MarkSweepSimulation
```
The program prints benchmark tables and finishes with `Day 48 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
