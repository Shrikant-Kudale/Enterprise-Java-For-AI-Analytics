# Day 03 - OOP and Design Patterns for AI Backends

> **Phase:** Java Foundations | **Date:** 2026-03-03 | **Code:** [`DesignPatternsForAiBackends.java`](./DesignPatternsForAiBackends.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### SOLID, applied to a model-serving backend
* **S** - `ModelRouter` routes, `Model` predicts, `MeteredModel` measures: one reason to change each.
* **O** - add a new routing strategy or decorator without modifying existing classes.
* **L** - any `Model` (linear, cached, remote) must honour the same contract (same output length, no new checked exceptions).
* **I** - small interfaces (`Model`, `RoutingStrategy`) rather than a god `ModelService`.
* **D** - the router depends on the `Model` abstraction; wiring happens in a factory/DI container.

### Patterns used today
| Pattern | Role in an AI backend |
|---------|-----------------------|
| **Strategy** | pick a model replica: round-robin, tenant-hash affinity, least-loaded, A/B bucket |
| **Decorator** | cross-cutting layers around a model: caching, metrics, timeout, retry, logging |
| **Factory** | build a `Model` from config (`"linear"`, `"onnx"`, `"remote"`) |
| **Builder** | immutable `InferenceRequest` with validation on `build()` |
| **Observer** | publish prediction events to metrics, audit log, drift monitor |
| **Chain of Responsibility** | request validators / guardrails, short-circuiting on first failure |

### Composition over inheritance
Decorators stack at runtime (`Metered(Caching(Remote))`), while inheritance would need `MeteredCachingRemoteModel`. Prefer `final` classes + interfaces; use `sealed` when the set of implementations is closed.

### Equals/hashCode contract & immutability
Request/feature objects used as cache keys must be immutable and have value-based `equals/hashCode`; mutable keys silently vanish from `HashMap`s.

