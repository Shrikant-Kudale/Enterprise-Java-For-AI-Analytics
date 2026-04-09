# Day 40 - BigDecimal and Numeric Precision

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-09 | **Code:** [`BigDecimalPrecision.java`](./BigDecimalPrecision.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`double` is binary floating point (0.1+0.2 != 0.3). Use `BigDecimal` (string constructor, explicit `RoundingMode`) for money; compare with `compareTo`, not `equals` (scale matters).

