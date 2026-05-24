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

## 5. Run the Lab

```bash
cd day-85-tf-idf-and-bm25-search
javac TfIdfWeights.java
java TfIdfWeights
```
The program prints benchmark tables and finishes with `Day 85 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
