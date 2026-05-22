# Day 83 - Feature Engineering: One-Hot and Hashing

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-22 | **Code:** [`FeatureHashing.java`](./FeatureHashing.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Categorical features: **one-hot** (exact, high-dimensional), **hashing trick** (fixed size, collisions), target/frequency encoding, embeddings. Scale numeric features; handle missing values; keep train/serve transforms identical (feature store).

## 2. Memory Architecture Breakdown

Hashing keeps memory fixed: `index = floorMod(hash, dim)`.

## 3. Interview Edge Cases & Pitfalls

1. Training-serving skew.
2. Target leakage in target encoding.

## 4. Interview Q&A

**Q1. One-hot vs hashing?**  
One-hot is lossless; hashing bounds dimension at the cost of collisions.

**Q2. Why feature stores?**  
Consistent features online and offline.

## 5. Run the Lab

```bash
cd day-83-feature-engineering-one-hot-and-hashing
javac FeatureHashing.java
java FeatureHashing
```
The program prints benchmark tables and finishes with `Day 83 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
