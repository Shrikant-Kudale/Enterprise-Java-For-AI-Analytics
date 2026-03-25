# Day 25 - Vector and Matrix Manipulation in Java

> **Phase:** AI / ML Integration | **Date:** 2026-03-25 | **Code:** [`MatrixOps.java`](./MatrixOps.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* Store matrices **flat, row-major** in `double[]`/`float[]` (`a[i*n + j]`): one contiguous block, no pointer chasing (unlike `double[][]`).
* **Loop order matters**: `i-j-k` walks `b` by column (cache-hostile); `i-k-j` walks both rows sequentially (3-10x faster). **Blocking/tiling** keeps tiles in L1/L2. Add parallelism across rows last.
* **Numerics**: stable softmax subtracts the max (`exp(1000)` overflows); normalise vectors once so cosine = dot product.
* Vector API (`jdk.incubator.vector`, run with `--add-modules jdk.incubator.vector`) gives SIMD; libraries (ND4J, EJML, ONNX Runtime) use BLAS.

