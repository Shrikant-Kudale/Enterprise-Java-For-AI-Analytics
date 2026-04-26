# Day 57 - Union-Find and Connected Components

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-26 | **Code:** [`UnionFindComponents.java`](./UnionFindComponents.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Disjoint-set union with **path compression** and union by rank gives near O(1) `find/union` (inverse Ackermann). Used for connected components, Kruskal MST, clustering and entity resolution.

## 2. Memory Architecture Breakdown

One `int[] parent` (plus rank).

## 3. Interview Edge Cases & Pitfalls

1. Recursion depth without compression.
2. Union the roots, not the nodes.

## 4. Interview Q&A

**Q1. Complexity?**  
Amortised O(alpha(n)) per operation.

**Q2. Use case in analytics?**  
Deduplicating entities / merging user identities.

## 5. Run the Lab

```bash
cd day-57-union-find-and-connected-components
javac UnionFindComponents.java
java UnionFindComponents
```
The program prints benchmark tables and finishes with `Day 57 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
