# Day 57 - Union-Find and Connected Components

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-26 | **Code:** [`UnionFindComponents.java`](./UnionFindComponents.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Disjoint-set union with **path compression** and union by rank gives near O(1) `find/union` (inverse Ackermann). Used for connected components, Kruskal MST, clustering and entity resolution.

## 2. Memory Architecture Breakdown

One `int[] parent` (plus rank).

