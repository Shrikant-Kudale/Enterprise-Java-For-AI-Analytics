# Day 88 - A/B Testing and Statistics

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-27 | **Code:** [`AbTestZScore.java`](./AbTestZScore.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Compare conversion rates with a **two-proportion z-test**: `z = (p2-p1)/sqrt(p(1-p)(1/n1+1/n2))`. Decide sample size up front, avoid peeking, check sample-ratio mismatch, and report effect size with confidence intervals. |z|>1.96 ~ p<0.05.

## 2. Memory Architecture Breakdown

Only counts needed: successes and trials per arm.

