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

