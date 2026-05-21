# Day 82 - Evaluation Metrics: Precision, Recall, AUC

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-21 | **Code:** [`ClassificationMetrics.java`](./ClassificationMetrics.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Accuracy misleads on imbalanced data. **Precision** = TP/(TP+FP), **Recall** = TP/(TP+FN), F1 = harmonic mean, **ROC-AUC** = P(score of a random positive > random negative). Use PR-AUC for rare positives.

## 2. Memory Architecture Breakdown

A confusion matrix is 4 counters.

## 3. Interview Edge Cases & Pitfalls

1. Threshold choice changes P/R.
2. Data leakage inflates metrics.

