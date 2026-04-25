# Day 56 - Dijkstra and Shortest Paths

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-25 | **Code:** [`DijkstraShortestPath.java`](./DijkstraShortestPath.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Dijkstra finds shortest paths with **non-negative** weights using a min-heap: pop the closest node, relax neighbours. O((V+E) log V). Use Bellman-Ford for negative edges, A* with a heuristic for goal search.

## 2. Memory Architecture Breakdown

Java's `PriorityQueue` has no decrease-key: push duplicates and skip stale entries.

## 3. Interview Edge Cases & Pitfalls

1. Negative edges break correctness.
2. Use `long` distances for big graphs.

## 4. Interview Q&A

**Q1. Why a heap?**  
Always expand the currently closest unsettled node.

**Q2. A* difference?**  
Adds an admissible heuristic to prune.

## 5. Run the Lab

```bash
cd day-56-dijkstra-and-shortest-paths
javac DijkstraShortestPath.java
java DijkstraShortestPath
```
The program prints benchmark tables and finishes with `Day 56 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
