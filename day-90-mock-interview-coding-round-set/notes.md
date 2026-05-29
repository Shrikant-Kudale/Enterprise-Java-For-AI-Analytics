# Day 90 - Mock Coding Round: Drills

> **Phase:** Mock Interviews & Wrap-up | **Date:** 2026-05-29 | **Code:** [`CodingRoundDrills.java`](./CodingRoundDrills.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Warm-up set: two-sum (hash map), valid parentheses (stack), LRU (LinkedHashMap with access order). Speak the approach and complexity before coding; test edge cases; then discuss thread-safety and scale.

## 2. Memory Architecture Breakdown

O(n) time / O(n) space for hash-map and stack solutions.

## 3. Interview Edge Cases & Pitfalls

1. Empty input.
2. Duplicates and negative numbers.

## 4. Interview Q&A

**Q1. Two-sum complexity?**  
O(n) time with a hash map.

**Q2. LRU in Java quickly?**  
LinkedHashMap(accessOrder=true) + removeEldestEntry.

