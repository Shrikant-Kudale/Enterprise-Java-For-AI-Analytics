# Day 85 - TF-IDF and BM25 Search

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-24 | **Code:** [`TfIdfWeights.java`](./TfIdfWeights.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**TF-IDF** weights a term by frequency in the doc times `log(N/df)` (rare terms matter more). **BM25** adds term-frequency saturation and length normalisation - the default in Lucene/Elasticsearch. Hybrid search blends BM25 with vector similarity.

## 2. Memory Architecture Breakdown

Inverted index: term -> posting list of (doc, tf).

## 3. Interview Edge Cases & Pitfalls

1. Stop words and stemming change scores.
2. Normalise doc length.

## 4. Interview Q&A

**Q1. Why IDF?**  
Down-weights common words.

**Q2. BM25 vs TF-IDF?**  
BM25 saturates tf and normalises length.

