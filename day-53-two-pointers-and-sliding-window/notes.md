# Day 53 - Two Pointers and Sliding Window

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-22 | **Code:** [`SlidingWindowPatterns.java`](./SlidingWindowPatterns.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Two pointers shrink O(n^2) scans to O(n) on sorted/structured data; a sliding window maintains an aggregate while moving left/right edges. Use a map for 'longest substring without repeats'.

## 2. Memory Architecture Breakdown

O(1) extra state (running sum or last-seen map).

## 3. Interview Edge Cases & Pitfalls

1. Off-by-one when shrinking the window.
2. Negative numbers break 'sum >= target' windows.

## 4. Interview Q&A

**Q1. When does sliding window apply?**  
Contiguous subarray/substring problems with monotone constraints.

**Q2. Complexity?**  
O(n) - each index enters/leaves once.

## 5. Run the Lab

```bash
cd day-53-two-pointers-and-sliding-window
javac SlidingWindowPatterns.java
java SlidingWindowPatterns
```
The program prints benchmark tables and finishes with `Day 53 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
