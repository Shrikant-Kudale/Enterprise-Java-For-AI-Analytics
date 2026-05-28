# Day 89 - Data Drift: PSI and KS

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-28 | **Code:** [`PsiDriftMonitor.java`](./PsiDriftMonitor.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Monitor features and predictions for **drift**. **PSI** = sum((a-e) * ln(a/e)) over bins: <0.1 stable, 0.1-0.25 moderate, >0.25 major. KS test compares CDFs. Alert, retrain, or roll back; log inputs and model version.

## 2. Memory Architecture Breakdown

Keep reference histograms from training; compare windows of live traffic.

## 3. Interview Edge Cases & Pitfalls

1. Empty bins -> add epsilon.
2. Label drift appears later than feature drift.

