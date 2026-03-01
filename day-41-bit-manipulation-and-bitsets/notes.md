# Day 41 - Bit Manipulation and BitSets

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-10 | **Code:** [`BitManipulation.java`](./BitManipulation.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Bit tricks: `x & (x-1)` clears the lowest set bit, `x & -x` isolates it, power of two test `x>0 && (x&(x-1))==0`. `BitSet` stores flags at 1 bit each - great for dense id sets and Bloom filters.

## 2. Memory Architecture Breakdown

`BitSet` = `long[]`; 1M flags = 125 KB vs 1 MB for `boolean[]`.

## 3. Interview Edge Cases & Pitfalls

1. `>>` is arithmetic, `>>>` logical.
2. Shifts are masked to 5/6 bits.

## 4. Interview Q&A

**Q1. Count set bits?**  
`Integer.bitCount` (POPCNT intrinsic).

**Q2. Swap without temp?**  
Three XORs - clever but not faster.

## 5. Run the Lab

```bash
cd day-41-bit-manipulation-and-bitsets
javac BitManipulation.java
java BitManipulation
```
The program prints benchmark tables and finishes with `Day 41 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
