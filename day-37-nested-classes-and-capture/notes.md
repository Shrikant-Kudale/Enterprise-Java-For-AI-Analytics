# Day 37 - Nested Classes and Variable Capture

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-06 | **Code:** [`NestedClassCapture.java`](./NestedClassCapture.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Inner (non-static) classes hold a hidden reference to the outer instance - a classic leak. Prefer `static` nested classes. Lambdas capture effectively-final locals or fields.

## 2. Memory Architecture Breakdown

Inner class instance = object + `this$0` reference to outer; a lambda capturing only locals does not retain `this`.

## 3. Interview Edge Cases & Pitfalls

1. Inner class keeps outer alive.
2. Captured locals must be effectively final.

## 4. Interview Q&A

**Q1. static vs inner nested class?**  
Static has no outer reference; inner does.

**Q2. What does a lambda capture?**  
Values of locals; `this` only if used.

## 5. Run the Lab

```bash
cd day-37-nested-classes-and-capture
javac NestedClassCapture.java
java NestedClassCapture
```
The program prints benchmark tables and finishes with `Day 37 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
