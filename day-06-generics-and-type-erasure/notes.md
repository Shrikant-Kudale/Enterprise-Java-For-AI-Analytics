# Day 06 - Generics and Type Erasure

> **Phase:** Collections & Data Structures | **Date:** 2026-03-06 | **Code:** [`GenericsAndTypeErasure.java`](./GenericsAndTypeErasure.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Why generics
Compile-time type safety with zero runtime cost *per call* (casts are inserted by the compiler). Generics are implemented by **erasure** to stay binary compatible with pre-5 code.

### Erasure rules
1. Type parameters are replaced by their **leftmost bound** (`Object` if unbounded): `<T extends Comparable<? super T>>` erases to `Comparable`.
2. The compiler inserts `checkcast` where generic results are consumed.
3. **Bridge methods** are synthesised so overriding still works: `compareTo(Version)` gets a bridge `compareTo(Object)`.
4. Consequences: no `new T()`, no `new T[n]`, no `instanceof List<String>`, no generic exceptions, no primitives (`List<int>`), static members can't use class type params, overloads that erase to the same signature clash.

### Variance & wildcards (PECS)
Arrays are covariant (and unsafe: `ArrayStoreException`); generics are **invariant**: `List<Integer>` is not a `List<Number>`.
* `? extends T` - **P**roducer: you can read `T`, can't write (except `null`).
* `? super T` - **C**onsumer: you can write `T`, reads give `Object`.
* `Collections.copy(List<? super T> dest, List<? extends T> src)` is the canonical example.

### Escape hatches
* **Class<T> tokens** (`type.cast(x)`, `Array.newInstance`) to recover runtime type.
* **Super type tokens** (`new TypeRef<List<String>>(){}`) exploit that *anonymous subclasses retain generic superclass info* in class metadata - the trick behind Jackson's `TypeReference` and Guava `TypeToken`.
* **Typesafe heterogeneous container** (`Map<Class<?>,Object>` with `cast`) - Effective Java item 33.

### Heap pollution
A variable of parameterized type referring to an object of a different type (via raw types or unchecked casts). The error surfaces later as a `ClassCastException` at a *distant, innocent-looking* line. `@SafeVarargs` asserts a generic varargs method doesn't pollute.

### AI-backend relevance
Typed pipelines (`Stage<I,O>`), tensors typed by element (`Tensor<Float>`), JSON deserialisation of `List<Prediction>` all rely on these mechanics; boxing in `List<Double>` is also the #1 hidden memory/latency cost in numeric Java.

## 2. Memory Architecture Breakdown

```
Source:   List<String> names = ...; String s = names.get(0);
Bytecode: List names; String s = (String) names.get(0);     // checkcast inserted
Runtime:  ArrayList<String>.class == ArrayList<Integer>.class == ArrayList.class

List<Integer> of 1M:   Object[] 4MB refs + 1M x Integer(16B) = ~20 MB   (and cache-hostile pointer chasing)
int[1M]:               4 MB contiguous
```
* Integer cache: `Integer.valueOf(-128..127)` returns shared instances; `==` on larger boxed values is `false` - always `equals`.
* Generic class metadata survives in the `Signature` attribute of class files (that's what reflection and super type tokens read) even though the verifier/JIT only sees erased types.
* Primitive specialisations (`IntStream`, `Int2ObjectMap` in fastutil/Eclipse Collections) exist solely because erasure forces boxing; Project Valhalla aims to fix this with specialised generics.

## 3. Interview Edge Cases & Pitfalls

1. `List<Object> l = new ArrayList<String>()` does not compile (invariance), but `Object[] o = new String[1]; o[0] = 1;` compiles and throws `ArrayStoreException`.
2. `static <T> void f(List<T>)` vs `static void f(List<?>)`: the wildcard version can't add anything; use a capture-helper method to write.
3. Two methods `void f(List<String>)` and `void f(List<Integer>)` clash after erasure (same erasure `f(List)`).
4. `T[] arr = (T[]) new Object[n]` works internally but returning it as `T[]` to a caller with `String[]` throws `ClassCastException` - use `Array.newInstance(cls, n)`.
5. `instanceof T` and `catch (T e)` are illegal; `T.class` doesn't exist - pass a `Class<T>`.
6. Comparable bound should be `Comparable<? super T>` so `java.sql.Timestamp extends Date` types work.
7. `Integer a=1000,b=1000; a==b` is false; `a.equals(b)` is true.

## 4. Interview Q&A

**Q1. What is type erasure and why does Java have it?**  
The compiler removes type arguments after checking, replacing parameters with bounds and inserting casts. This kept binary compatibility with pre-generics libraries and avoided reified-generic runtime changes.

**Q2. Explain PECS with an example.**  
Producer-extends / Consumer-super. `copy(List<? super T> dst, List<? extends T> src)` reads T from src (producer) and writes T into dst (consumer). It maximises API flexibility while remaining type safe.

**Q3. How can you get the generic type at runtime?**  
Only if it's captured in a class signature: subclass a generic type with a concrete argument (super type token) or pass a `Class<T>`. `getGenericSuperclass()` returns a `ParameterizedType`.

**Q4. What are bridge methods?**  
Synthetic methods generated to preserve polymorphism after erasure, e.g. `compareTo(Object)` delegating to `compareTo(Version)` with a cast. They show up in stack traces and `Method.isBridge()`.

**Q5. What causes heap pollution and how do you avoid it?**  
Mixing raw types / unchecked casts / generic varargs so a typed reference holds a wrong-type object; avoid raw types, minimise `@SuppressWarnings("unchecked")` scope and prefer immutable collections.

**Q6. Why is `List<Double>` slow for numeric work?**  
Each element is a separate boxed object (16 B + 4 B ref), arithmetic requires unboxing, memory is scattered. Use `double[]`, `DoubleStream` or primitive collections.

## 5. Run the Lab

```bash
cd day-06-generics-and-type-erasure
javac GenericsAndTypeErasure.java
java GenericsAndTypeErasure
```
The program prints benchmark tables and finishes with `Day 06 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
