# Day 78 - Logistic Regression from Scratch

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-17 | **Code:** [`LogisticRegressionGD.java`](./LogisticRegressionGD.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Logistic regression = linear score passed through the **sigmoid**; trained with cross-entropy; gradient is `(p - y) * x`. Outputs calibrated-ish probabilities; threshold trades precision vs recall.

## 2. Memory Architecture Breakdown

Weights + bias only; inference is a dot product.

## 3. Interview Edge Cases & Pitfalls

1. Separable data -> weights grow (use L2).
2. Class imbalance needs weighting/threshold tuning.

## 4. Interview Q&A

**Q1. Why sigmoid?**  
Maps scores to (0,1) and gives a convex loss.

**Q2. Decision threshold?**  
Pick from the PR/ROC curve per cost.

## 5. Run the Lab

```bash
cd day-78-logistic-regression-from-scratch
javac LogisticRegressionGD.java
java LogisticRegressionGD
```
The program prints benchmark tables and finishes with `Day 78 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
