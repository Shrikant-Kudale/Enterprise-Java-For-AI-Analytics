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

