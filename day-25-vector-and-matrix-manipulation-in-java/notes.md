# Day 25 - Vector and Matrix Manipulation in Java

> **Phase:** AI / ML Integration | **Date:** 2026-03-25 | **Code:** [`MatrixOps.java`](./MatrixOps.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* Store matrices **flat, row-major** in `double[]`/`float[]` (`a[i*n + j]`): one contiguous block, no pointer chasing (unlike `double[][]`).
* **Loop order matters**: `i-j-k` walks `b` by column (cache-hostile); `i-k-j` walks both rows sequentially (3-10x faster). **Blocking/tiling** keeps tiles in L1/L2. Add parallelism across rows last.
* **Numerics**: stable softmax subtracts the max (`exp(1000)` overflows); normalise vectors once so cosine = dot product.
* Vector API (`jdk.incubator.vector`, run with `--add-modules jdk.incubator.vector`) gives SIMD; libraries (ND4J, EJML, ONNX Runtime) use BLAS.

## 2. Memory Architecture Breakdown

Cache line = 64 B = 8 doubles. Row-wise access uses every byte loaded; column-wise strided access wastes 7/8 of each line.
`double[n][n]` = array of row references (extra indirection, rows scattered); flat `double[n*n]` is one allocation.

## 3. Interview Edge Cases & Pitfalls

1. Floating-point addition is not associative: parallel sums differ in the last bits.
2. float vs double: float halves memory/bandwidth but accumulates error - accumulate in double.
3. NaN from `0/0` or `inf-inf` propagates silently.

## 4. Interview Q&A

**Q1. Why is i-k-j faster than i-j-k?**  
It streams rows of B and C sequentially, so each cache line is fully used and the JIT can vectorise the inner loop.

**Q2. How do you compute softmax safely?**  
Subtract max(x) first: softmax(x) = softmax(x - max); then exp/sum.

## 5. Run the Lab

```bash
cd day-25-vector-and-matrix-manipulation-in-java
javac MatrixOps.java
java MatrixOps
```
The program prints benchmark tables and finishes with `Day 25 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
