# Day 79 - K-Means Clustering

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-18 | **Code:** [`KMeansLite.java`](./KMeansLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

K-means alternates **assign** each point to the nearest centroid and **update** centroids to cluster means until stable. Initialise well (k-means++), scale features, choose k with the elbow/silhouette. O(n*k*d) per iteration.

## 2. Memory Architecture Breakdown

Centroids k*d doubles + assignments n ints.

## 3. Interview Edge Cases & Pitfalls

1. Local optima - run multiple seeds.
2. Sensitive to outliers/scale.

