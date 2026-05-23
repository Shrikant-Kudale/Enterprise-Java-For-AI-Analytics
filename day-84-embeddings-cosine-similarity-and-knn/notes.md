# Day 84 - Embeddings, Cosine Similarity and kNN

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-23 | **Code:** [`CosineNearestNeighbor.java`](./CosineNearestNeighbor.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Embeddings map items to dense vectors where distance = semantic similarity. **Cosine** = dot/(|a||b|); normalise once and use dot product. kNN returns the closest vectors; production uses ANN indexes (day 28).

## 2. Memory Architecture Breakdown

d floats per item (e.g. 768 x 4 B = 3 KB).

## 3. Interview Edge Cases & Pitfalls

1. Compare vectors from the same model/version.
2. Curse of dimensionality for raw features.

## 4. Interview Q&A

**Q1. Cosine vs L2?**  
Cosine ignores magnitude; equals L2 on normalised vectors.

**Q2. Why normalise?**  
Turns cosine into a fast dot product.

## 5. Run the Lab

```bash
cd day-84-embeddings-cosine-similarity-and-knn
javac CosineNearestNeighbor.java
java CosineNearestNeighbor
```
The program prints benchmark tables and finishes with `Day 84 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
