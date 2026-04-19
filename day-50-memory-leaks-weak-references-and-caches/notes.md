# Day 50 - Memory Leaks, Weak References and Caches

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-19 | **Code:** [`WeakReferenceCaches.java`](./WeakReferenceCaches.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Leaks in Java = *unintended reachability*: static maps, listeners, ThreadLocals, unbounded caches. Use bounded caches (Caffeine), `WeakHashMap` for canonical maps, `try-with-resources`, and heap dumps (MAT) to find dominators.

## 2. Memory Architecture Breakdown

Reference strength: strong > soft (cleared under memory pressure) > weak (next GC) > phantom (cleanup hooks).

