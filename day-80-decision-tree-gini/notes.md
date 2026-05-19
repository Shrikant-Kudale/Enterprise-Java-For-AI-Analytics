# Day 80 - Decision Trees and Gini Impurity

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-19 | **Code:** [`GiniSplit.java`](./GiniSplit.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A tree greedily picks the split that most reduces impurity: **Gini** `2p(1-p)` (binary) or entropy. Leaves predict the majority class. Control depth/min-samples to avoid overfitting; ensembles (random forest, gradient boosting) dominate tabular ML.

## 2. Memory Architecture Breakdown

Tree = arrays of feature, threshold, left, right.

