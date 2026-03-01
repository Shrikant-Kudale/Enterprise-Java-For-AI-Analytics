# Day 86 - Neural Network Backprop and Gradient Check

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-25 | **Code:** [`BackpropGradientCheck.java`](./BackpropGradientCheck.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Forward pass computes activations; **backpropagation** applies the chain rule layer by layer to get gradients. A **gradient check** compares the analytic gradient with a finite difference `(L(w+e)-L(w-e))/2e` - the standard way to validate hand-written backprop.

## 2. Memory Architecture Breakdown

Backprop stores forward activations (memory ~ depth x batch).

## 3. Interview Edge Cases & Pitfalls

1. Use a small epsilon (1e-5..1e-7).
2. Vanishing/exploding gradients in deep nets.

## 4. Interview Q&A

**Q1. Why gradient check?**  
Catches backprop bugs.

**Q2. Sigmoid derivative?**  
p(1-p); for cross-entropy the output gradient is p - y.

## 5. Run the Lab

```bash
cd day-86-neural-network-forward-and-backprop-gradient-check
javac BackpropGradientCheck.java
java BackpropGradientCheck
```
The program prints benchmark tables and finishes with `Day 86 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
