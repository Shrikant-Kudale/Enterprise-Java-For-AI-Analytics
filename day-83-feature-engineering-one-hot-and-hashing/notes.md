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

