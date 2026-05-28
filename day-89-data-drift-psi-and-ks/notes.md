# Day 89 - Data Drift: PSI and KS

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-28 | **Code:** [`PsiDriftMonitor.java`](./PsiDriftMonitor.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Monitor features and predictions for **drift**. **PSI** = sum((a-e) * ln(a/e)) over bins: <0.1 stable, 0.1-0.25 moderate, >0.25 major. KS test compares CDFs. Alert, retrain, or roll back; log inputs and model version.

## 2. Memory Architecture Breakdown

Keep reference histograms from training; compare windows of live traffic.

## 3. Interview Edge Cases & Pitfalls

1. Empty bins -> add epsilon.
2. Label drift appears later than feature drift.

## 4. Interview Q&A

**Q1. PSI thresholds?**  
0.1 and 0.25.

**Q2. Data vs concept drift?**  
Inputs change vs the input-output relation changes.

## 5. Run the Lab

```bash
cd day-89-data-drift-psi-and-ks
javac PsiDriftMonitor.java
java PsiDriftMonitor
```
The program prints benchmark tables and finishes with `Day 89 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
