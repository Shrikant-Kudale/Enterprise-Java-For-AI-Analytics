# Day 85 - TF-IDF and BM25 Search

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-24 | **Code:** [`TfIdfWeights.java`](./TfIdfWeights.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**TF-IDF** weights a term by frequency in the doc times `log(N/df)` (rare terms matter more). **BM25** adds term-frequency saturation and length normalisation - the default in Lucene/Elasticsearch. Hybrid search blends BM25 with vector similarity.

