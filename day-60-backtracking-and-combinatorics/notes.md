# Day 60 - Backtracking and Combinatorics

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-29 | **Code:** [`BacktrackingPermutations.java`](./BacktrackingPermutations.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Backtracking builds candidates incrementally and abandons a branch as soon as it cannot work (choose -> explore -> un-choose). Use for permutations, subsets, N-Queens, sudoku; prune early to tame exponential time.

## 2. Memory Architecture Breakdown

Recursion depth = solution size; copy a path only when emitting.

## 3. Interview Edge Cases & Pitfalls

1. Forgetting to undo the choice.
2. Duplicates need sorting + skipping.

## 4. Interview Q&A

**Q1. Permutations count?**  
n! - use pruning when possible.

**Q2. Backtracking vs DP?**  
DP reuses overlapping subproblems; backtracking enumerates.

## 5. Run the Lab

```bash
cd day-60-backtracking-and-combinatorics
javac BacktrackingPermutations.java
java BacktrackingPermutations
```
The program prints benchmark tables and finishes with `Day 60 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
