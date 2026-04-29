# Day 60 - Backtracking and Combinatorics

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-29 | **Code:** [`BacktrackingPermutations.java`](./BacktrackingPermutations.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Backtracking builds candidates incrementally and abandons a branch as soon as it cannot work (choose -> explore -> un-choose). Use for permutations, subsets, N-Queens, sudoku; prune early to tame exponential time.

## 2. Memory Architecture Breakdown

Recursion depth = solution size; copy a path only when emitting.

## 3. Interview Edge Cases & Pitfalls

1. Forgetting to undo the choice.
2. Duplicates need sorting + skipping.

