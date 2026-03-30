# Day 30 - FAANG Interview Deep Dive and Mock Round

> **Phase:** System Design & Interview Capstone | **Date:** 2026-03-30 | **Code:** [`MockInterviewRound.java`](./MockInterviewRound.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Round structure (45 min)**: clarify (3) -> approach + complexity (7) -> code (20) -> test with examples/edge cases (8) -> optimise/follow-ups (7).
* **Pattern list**: hash map index, two pointers/sliding window, heap (top-K, median), intervals, BFS/DFS, DP, binary search on answer, design (LRU, rate limiter, bounded queue).
* **Backend/AI follow-ups**: make it thread-safe, make it distributed, what if data doesn't fit memory, how do you test and monitor it.
* Communicate: think aloud, state complexity, write tests first for edge cases (empty, one element, duplicates, overflow).

## 2. Memory Architecture Breakdown

LRU = HashMap + doubly linked list (O(1) get/put). Median = max-heap (lower half) + min-heap (upper half). Quickselect = in-place partition, O(n) average, O(1) extra space.

## 3. Interview Edge Cases & Pitfalls

1. State assumptions out loud.
2. Handle empty input and duplicates.
3. Concurrent versions need locks around compound actions.

## 4. Interview Q&A

**Q1. Median of a stream?**  
Two heaps: lower half max-heap, upper half min-heap, sizes differ by at most 1; O(log n) add, O(1) median.

**Q2. K-th largest fastest on average?**  
Quickselect O(n) average; heap of size k is O(n log k) and streaming-friendly.

