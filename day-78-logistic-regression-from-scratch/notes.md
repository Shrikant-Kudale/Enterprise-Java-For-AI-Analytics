# Day 78 - Logistic Regression from Scratch

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-17 | **Code:** [`LogisticRegressionGD.java`](./LogisticRegressionGD.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Logistic regression = linear score passed through the **sigmoid**; trained with cross-entropy; gradient is `(p - y) * x`. Outputs calibrated-ish probabilities; threshold trades precision vs recall.

## 2. Memory Architecture Breakdown

Weights + bias only; inference is a dot product.

## 3. Interview Edge Cases & Pitfalls

1. Separable data -> weights grow (use L2).
2. Class imbalance needs weighting/threshold tuning.

