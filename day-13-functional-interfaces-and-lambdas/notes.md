# Day 13 - Functional Interfaces and Lambdas

> **Phase:** Functional Java & Streams | **Date:** 2026-03-13 | **Code:** [`FunctionalInterfacesLambdas.java`](./FunctionalInterfacesLambdas.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Functional interfaces
An interface with exactly **one abstract method** (default/static/Object-method overrides don't count); `@FunctionalInterface` makes the compiler enforce it. `java.util.function` core set:

| Interface | Shape | Example |
|-----------|-------|---------|
| `Function<T,R>` / `BiFunction` / `UnaryOperator<T>` / `BinaryOperator<T>` | T -> R | `String::length` |
| `Predicate<T>` / `BiPredicate` | T -> boolean | `s -> s.isBlank()` |
| `Supplier<T>` | () -> T | `ArrayList::new`, lazy values |
| `Consumer<T>` / `BiConsumer` | T -> void | `System.out::println` |
| `IntFunction`, `ToDoubleFunction`, `IntUnaryOperator`, ... | primitive specialisations | avoid boxing |
`Runnable`, `Callable`, `Comparator` are functional too.

### Lambdas vs anonymous classes
* A lambda is **not** an inner class: no new `this` scope (`this` = enclosing instance), no separate `.class` file; compiled to a private synthetic method + `invokedynamic` -> `LambdaMetafactory` spins the implementing class at first execution (linkage cost once).
* Non-capturing lambdas are typically singletons (no allocation per evaluation); capturing lambdas allocate an object holding captured values.
* Captured locals must be **effectively final** - the lambda sees a *copy*; to mutate use an `AtomicInteger`/array holder (but think about thread-safety).

### Method references (4 kinds)
`Integer::parseInt` (static), `str::concat` (bound instance), `String::length` (unbound instance, first arg is receiver), `ArrayList::new` (constructor).

### Functional design patterns
* **Composition**: `f.andThen(g)`, `f.compose(g)`, `Predicate.and/or/negate`, `Comparator.thenComparing`.
* **Currying / partial application**: `Function<A,Function<B,C>>`.
* **Memoisation** with `ConcurrentHashMap.computeIfAbsent` (never recursively on the same map).
* **Higher-order strategy**: pass behaviour instead of subclassing (retry policies, scoring functions).
* **Lazy evaluation**: `Supplier<T>` and `Optional.orElseGet`.
* **Checked-exception adapters**: `ThrowingFunction<T,R,E>` wrapper.

### AI backend relevance
Feature transformation pipelines (`standardize -> clip -> log1p`) are function composition; post-processing hooks, retry/fallback policies and metrics callbacks are higher-order functions; reducing boilerplate in strategy-heavy code (day 3).

