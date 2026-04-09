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

## 5. Run the Lab

```bash
cd day-40-bigdecimal-and-numeric-precision
javac BigDecimalPrecision.java
java BigDecimalPrecision
```
The program prints benchmark tables and finishes with `Day 40 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
