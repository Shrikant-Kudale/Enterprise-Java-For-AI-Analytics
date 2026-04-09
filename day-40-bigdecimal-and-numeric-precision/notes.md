# Day 40 - BigDecimal and Numeric Precision

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-09 | **Code:** [`BigDecimalPrecision.java`](./BigDecimalPrecision.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`double` is binary floating point (0.1+0.2 != 0.3). Use `BigDecimal` (string constructor, explicit `RoundingMode`) for money; compare with `compareTo`, not `equals` (scale matters).

## 2. Memory Architecture Breakdown

BigDecimal = unscaled BigInteger + scale: far slower and larger than double.

## 3. Interview Edge Cases & Pitfalls

1. `new BigDecimal(0.1)` captures the binary error.
2. divide() without a scale can throw.

## 4. Interview Q&A

**Q1. Why not double for money?**  
Representation error and rounding drift.

**Q2. equals vs compareTo?**  
equals compares scale too (1.10 != 1.1).

