# Day 77 - Linear Regression with Gradient Descent

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-16 | **Code:** [`LinearRegressionGD.java`](./LinearRegressionGD.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Model `y = w*x + b`; loss = mean squared error; gradient descent updates `w -= lr * dL/dw`. Normalise features, choose a learning rate that converges, and monitor loss. Closed form (normal equations) exists for small problems.

## 2. Memory Architecture Breakdown

Parameters are two doubles; data fits in `double[]`.

## 3. Interview Edge Cases & Pitfalls

1. Learning rate too high diverges.
2. Unscaled features slow convergence.

## 4. Interview Q&A

**Q1. Why gradient descent?**  
Scales to huge data via mini-batches.

**Q2. Overfitting fix?**  
Regularisation, more data, simpler model.

## 5. Run the Lab

```bash
cd day-77-linear-regression-gradient-descent
javac LinearRegressionGD.java
java LinearRegressionGD
```
The program prints benchmark tables and finishes with `Day 77 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
