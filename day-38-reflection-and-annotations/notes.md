# Day 38 - Reflection and Annotations

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-07 | **Code:** [`ReflectionAnnotations.java`](./ReflectionAnnotations.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Reflection inspects classes at runtime (`getDeclaredMethods`, `Method.invoke`); annotations with `RUNTIME` retention drive frameworks (Spring, JPA). It is slower and bypasses compile-time checks - cache lookups, prefer MethodHandles.

## 2. Memory Architecture Breakdown

Class metadata lives in Metaspace; `Method` objects are copied on each `getMethods()` call.

## 3. Interview Edge Cases & Pitfalls

1. `setAccessible` is restricted by modules.
2. Reflection defeats inlining until JIT warms.

## 4. Interview Q&A

**Q1. How does Spring find @Component?**  
Classpath scanning reads annotations via reflection/ASM.

**Q2. Faster than reflection?**  
MethodHandle / generated code.

## 5. Run the Lab

```bash
cd day-38-reflection-and-annotations
javac ReflectionAnnotations.java
java ReflectionAnnotations
```
The program prints benchmark tables and finishes with `Day 38 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
