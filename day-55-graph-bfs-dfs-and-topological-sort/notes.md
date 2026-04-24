# Day 55 - Graph BFS, DFS and Topological Sort

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-24 | **Code:** [`GraphTopologicalSort.java`](./GraphTopologicalSort.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Model graphs as adjacency lists. BFS = shortest path in unweighted graphs; DFS = connectivity/cycles; **Kahn's algorithm** orders a DAG by repeatedly removing zero in-degree nodes (build pipelines, ML DAGs).

## 2. Memory Architecture Breakdown

Adjacency list O(V+E) memory; use `ArrayDeque` for the queue.

## 3. Interview Edge Cases & Pitfalls

1. Cycle => topological order impossible (result shorter than V).
2. Recursive DFS can overflow the stack.

