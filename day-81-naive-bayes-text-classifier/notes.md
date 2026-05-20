# Day 81 - Naive Bayes Text Classifier

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-20 | **Code:** [`NaiveBayesLite.java`](./NaiveBayesLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Naive Bayes assumes features are independent given the class: `P(c|doc) ~ P(c) * prod P(w|c)`. Use log-probabilities and **Laplace smoothing**. Fast, strong baseline for spam/sentiment.

## 2. Memory Architecture Breakdown

Word-count table per class.

## 3. Interview Edge Cases & Pitfalls

1. Zero counts without smoothing.
2. Use logs to avoid underflow.

## 4. Interview Q&A

**Q1. Why 'naive'?**  
Independence assumption.

**Q2. Why smoothing?**  
Unseen words would zero the probability.

## 5. Run the Lab

```bash
cd day-81-naive-bayes-text-classifier
javac NaiveBayesLite.java
java NaiveBayesLite
```
The program prints benchmark tables and finishes with `Day 81 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
