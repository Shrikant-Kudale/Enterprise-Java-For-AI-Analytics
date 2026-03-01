# Day 79 - K-Means Clustering

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-18 | **Code:** [`KMeansLite.java`](./KMeansLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

K-means alternates **assign** each point to the nearest centroid and **update** centroids to cluster means until stable. Initialise well (k-means++), scale features, choose k with the elbow/silhouette. O(n*k*d) per iteration.

## 2. Memory Architecture Breakdown

Centroids k*d doubles + assignments n ints.

## 3. Interview Edge Cases & Pitfalls

1. Local optima - run multiple seeds.
2. Sensitive to outliers/scale.

## 4. Interview Q&A

**Q1. k-means limitation?**  
Assumes spherical, similar-size clusters.

**Q2. Use in vector search?**  
IVF coarse quantiser (day 28).

## 5. Run the Lab

```bash
cd day-79-k-means-clustering
javac KMeansLite.java
java KMeansLite
```
The program prints benchmark tables and finishes with `Day 79 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
