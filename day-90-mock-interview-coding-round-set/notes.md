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

## 5. Run the Lab

```bash
cd day-90-mock-interview-coding-round-set
javac CodingRoundDrills.java
java CodingRoundDrills
```
The program prints benchmark tables and finishes with `Day 90 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
