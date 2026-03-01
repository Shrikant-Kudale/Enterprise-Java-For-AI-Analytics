# Day 82 - Evaluation Metrics: Precision, Recall, AUC

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-21 | **Code:** [`ClassificationMetrics.java`](./ClassificationMetrics.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Accuracy misleads on imbalanced data. **Precision** = TP/(TP+FP), **Recall** = TP/(TP+FN), F1 = harmonic mean, **ROC-AUC** = P(score of a random positive > random negative). Use PR-AUC for rare positives.

## 2. Memory Architecture Breakdown

A confusion matrix is 4 counters.

## 3. Interview Edge Cases & Pitfalls

1. Threshold choice changes P/R.
2. Data leakage inflates metrics.

## 4. Interview Q&A

**Q1. Precision vs recall?**  
Precision: how many flagged are right; recall: how many true cases were found.

**Q2. AUC meaning?**  
Ranking quality independent of threshold.

## 5. Run the Lab

```bash
cd day-82-evaluation-metrics-precision-recall-auc
javac ClassificationMetrics.java
java ClassificationMetrics
```
The program prints benchmark tables and finishes with `Day 82 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
