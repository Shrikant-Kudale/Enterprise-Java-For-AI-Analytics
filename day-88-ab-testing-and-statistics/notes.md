# Day 88 - A/B Testing and Statistics

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-27 | **Code:** [`AbTestZScore.java`](./AbTestZScore.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Compare conversion rates with a **two-proportion z-test**: `z = (p2-p1)/sqrt(p(1-p)(1/n1+1/n2))`. Decide sample size up front, avoid peeking, check sample-ratio mismatch, and report effect size with confidence intervals. |z|>1.96 ~ p<0.05.

## 2. Memory Architecture Breakdown

Only counts needed: successes and trials per arm.

## 3. Interview Edge Cases & Pitfalls

1. Peeking inflates false positives.
2. Multiple metrics need correction.

## 4. Interview Q&A

**Q1. p-value meaning?**  
Probability of data this extreme if there were no effect.

**Q2. Why pre-register sample size?**  
Prevents optional stopping.

