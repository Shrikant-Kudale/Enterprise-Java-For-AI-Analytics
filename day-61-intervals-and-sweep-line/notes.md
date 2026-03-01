# Day 61 - Intervals and Sweep Line

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-30 | **Code:** [`IntervalsSweepLine.java`](./IntervalsSweepLine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Sort intervals by start, then merge in one pass. **Sweep line**: turn intervals into +1/-1 events, sort, and track the running count (meeting rooms, max concurrency, overlapping sessions).

## 2. Memory Architecture Breakdown

O(n log n) for sorting, O(n) extra.

## 3. Interview Edge Cases & Pitfalls

1. Touching intervals: decide whether [1,2],[2,3] merge.
2. Sort ends before starts at equal times if closed-open.

## 4. Interview Q&A

**Q1. Min meeting rooms?**  
Max overlap from a sweep.

**Q2. Merge intervals complexity?**  
O(n log n).

## 5. Run the Lab

```bash
cd day-61-intervals-and-sweep-line
javac IntervalsSweepLine.java
java IntervalsSweepLine
```
The program prints benchmark tables and finishes with `Day 61 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
