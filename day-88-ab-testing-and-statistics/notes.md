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

## 5. Run the Lab

```bash
cd day-88-ab-testing-and-statistics
javac AbTestZScore.java
java AbTestZScore
```
The program prints benchmark tables and finishes with `Day 88 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
