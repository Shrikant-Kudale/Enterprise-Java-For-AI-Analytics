# Day 77 - Linear Regression with Gradient Descent

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-16 | **Code:** [`LinearRegressionGD.java`](./LinearRegressionGD.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Model `y = w*x + b`; loss = mean squared error; gradient descent updates `w -= lr * dL/dw`. Normalise features, choose a learning rate that converges, and monitor loss. Closed form (normal equations) exists for small problems.

