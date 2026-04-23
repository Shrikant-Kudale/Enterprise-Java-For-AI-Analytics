# Day 54 - Prefix Sums and Difference Arrays

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-23 | **Code:** [`PrefixSumsDiffArrays.java`](./PrefixSumsDiffArrays.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Prefix sums answer range-sum queries in O(1) after O(n) build: `sum(i..j) = p[j+1]-p[i]`. Difference arrays apply many range updates in O(1) each, then one pass rebuilds values.

## 2. Memory Architecture Breakdown

One extra `int[n+1]`.

## 3. Interview Edge Cases & Pitfalls

1. Use `long` to avoid overflow.
2. Index shift by one is the classic bug.

## 4. Interview Q&A

**Q1. Range add updates?**  
Difference array: d[l]+=v, d[r+1]-=v.

**Q2. 2D version?**  
Prefix over rows and columns with inclusion-exclusion.

