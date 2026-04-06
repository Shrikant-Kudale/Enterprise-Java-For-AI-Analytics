# Day 37 - Nested Classes and Variable Capture

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-06 | **Code:** [`NestedClassCapture.java`](./NestedClassCapture.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Inner (non-static) classes hold a hidden reference to the outer instance - a classic leak. Prefer `static` nested classes. Lambdas capture effectively-final locals or fields.

## 2. Memory Architecture Breakdown

Inner class instance = object + `this$0` reference to outer; a lambda capturing only locals does not retain `this`.

