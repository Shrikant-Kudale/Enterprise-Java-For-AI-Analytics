# Day 86 - Neural Network Backprop and Gradient Check

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-25 | **Code:** [`BackpropGradientCheck.java`](./BackpropGradientCheck.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Forward pass computes activations; **backpropagation** applies the chain rule layer by layer to get gradients. A **gradient check** compares the analytic gradient with a finite difference `(L(w+e)-L(w-e))/2e` - the standard way to validate hand-written backprop.

## 2. Memory Architecture Breakdown

Backprop stores forward activations (memory ~ depth x batch).

