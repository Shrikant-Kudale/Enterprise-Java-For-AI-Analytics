# Day 59 - Dynamic Programming Patterns

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-28 | **Code:** [`DynamicProgrammingPatterns.java`](./DynamicProgrammingPatterns.java) | **Runtime:** JDK 21+

## 1. Core Concepts

DP = overlapping subproblems + optimal substructure. Define the state, the recurrence, the base case and the order. Patterns: 1-D (coin change), 2-D (edit distance, LCS), knapsack, intervals. Memoise (top-down) or tabulate (bottom-up).

## 2. Memory Architecture Breakdown

Tables can often be rolled to O(n) memory.

## 3. Interview Edge Cases & Pitfalls

1. Wrong base case/initial 'infinity' overflow.
2. State must capture everything the future needs.

## 4. Interview Q&A

**Q1. Memo vs tabulation?**  
Memo is easier to write; tabulation avoids recursion depth.

**Q2. Coin change recurrence?**  
dp[a]=min(dp[a-c]+1).

## 5. Run the Lab

```bash
cd day-59-dynamic-programming-patterns
javac DynamicProgrammingPatterns.java
java DynamicProgrammingPatterns
```
The program prints benchmark tables and finishes with `Day 59 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
