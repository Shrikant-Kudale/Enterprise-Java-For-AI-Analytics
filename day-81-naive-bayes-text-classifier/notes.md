# Day 81 - Naive Bayes Text Classifier

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-20 | **Code:** [`NaiveBayesLite.java`](./NaiveBayesLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Naive Bayes assumes features are independent given the class: `P(c|doc) ~ P(c) * prod P(w|c)`. Use log-probabilities and **Laplace smoothing**. Fast, strong baseline for spam/sentiment.

