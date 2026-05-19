# Day 80 - Decision Trees and Gini Impurity

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-19 | **Code:** [`GiniSplit.java`](./GiniSplit.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A tree greedily picks the split that most reduces impurity: **Gini** `2p(1-p)` (binary) or entropy. Leaves predict the majority class. Control depth/min-samples to avoid overfitting; ensembles (random forest, gradient boosting) dominate tabular ML.

## 2. Memory Architecture Breakdown

Tree = arrays of feature, threshold, left, right.

## 3. Interview Edge Cases & Pitfalls

1. Deep trees overfit.
2. Greedy splits are not globally optimal.

## 4. Interview Q&A

**Q1. Gini vs entropy?**  
Similar results; Gini is cheaper.

**Q2. Why ensembles?**  
Averaging reduces variance.

## 5. Run the Lab

```bash
cd day-80-decision-tree-gini
javac GiniSplit.java
java GiniSplit
```
The program prints benchmark tables and finishes with `Day 80 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
