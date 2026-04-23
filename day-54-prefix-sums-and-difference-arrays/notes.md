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

## 5. Run the Lab

```bash
cd day-54-prefix-sums-and-difference-arrays
javac PrefixSumsDiffArrays.java
java PrefixSumsDiffArrays
```
The program prints benchmark tables and finishes with `Day 54 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
