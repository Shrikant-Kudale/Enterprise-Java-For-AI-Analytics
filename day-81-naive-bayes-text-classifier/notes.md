# Day 81 - Naive Bayes Text Classifier

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-20 | **Code:** [`NaiveBayesLite.java`](./NaiveBayesLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Naive Bayes assumes features are independent given the class: `P(c|doc) ~ P(c) * prod P(w|c)`. Use log-probabilities and **Laplace smoothing**. Fast, strong baseline for spam/sentiment.

## 2. Memory Architecture Breakdown

Word-count table per class.

## 3. Interview Edge Cases & Pitfalls

1. Zero counts without smoothing.
2. Use logs to avoid underflow.

## 4. Interview Q&A

**Q1. Why 'naive'?**  
Independence assumption.

**Q2. Why smoothing?**  
Unseen words would zero the probability.

