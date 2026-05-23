# Day 84 - Embeddings, Cosine Similarity and kNN

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-23 | **Code:** [`CosineNearestNeighbor.java`](./CosineNearestNeighbor.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Embeddings map items to dense vectors where distance = semantic similarity. **Cosine** = dot/(|a||b|); normalise once and use dot product. kNN returns the closest vectors; production uses ANN indexes (day 28).

## 2. Memory Architecture Breakdown

d floats per item (e.g. 768 x 4 B = 3 KB).

