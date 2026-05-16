# Day 77 - Linear Regression with Gradient Descent

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-16 | **Code:** [`LinearRegressionGD.java`](./LinearRegressionGD.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Model `y = w*x + b`; loss = mean squared error; gradient descent updates `w -= lr * dL/dw`. Normalise features, choose a learning rate that converges, and monitor loss. Closed form (normal equations) exists for small problems.

## 2. Memory Architecture Breakdown

Parameters are two doubles; data fits in `double[]`.

## 3. Interview Edge Cases & Pitfalls

1. Learning rate too high diverges.
2. Unscaled features slow convergence.

