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

## 2. Memory Architecture Breakdown

```
Client -> ModelRouter --(RoutingStrategy)--> [ Metered -> Caching -> LinearModel ]
                                             [ Metered -> Caching -> RemoteModel ]
Each decorator = 1 object header (12B) + 1 ref (4B) + state; a 3-deep stack adds ~3 virtual calls.
```
* Interface calls are **megamorphic** when >2 implementations are seen at one call-site: the JIT falls back to a vtable/itable dispatch (~3 ns) and loses inlining. Keep hot paths bimorphic or use `final` classes + sealed interfaces so CHA can devirtualise.
* Caches keyed by `List<Double>` boxed per call allocate heavily: in production hash the `double[]` (e.g. `Arrays.hashCode`) into a record key to cut allocation.
* Observers hold strong references to listeners: a forgotten unsubscribe is a classic memory leak (`static EventBus` retains listener graphs).

## 3. Interview Edge Cases & Pitfalls

1. Decorator order matters: `Caching(Metered(model))` measures only misses; `Metered(Caching(model))` measures hit + miss latency.
2. Round-robin with `AtomicInteger.getAndIncrement() % n` goes negative after overflow - use `Math.floorMod`.
3. Builder must validate in `build()`, not in setters, so cross-field rules (timeout vs batch size) are checked once.
4. Caching nondeterministic models (dropout, sampling temperature) returns stale results - include temperature/seed in the key.
5. Observer callbacks run on the publisher thread: a slow listener stalls inference. Dispatch asynchronously or bound the queue.
6. Singleton via static mutable state defeats testing; inject instead.

## 4. Interview Q&A

**Q1. Strategy vs State pattern?**  
Both swap behaviour via composition. Strategy is chosen by the *client* and is stateless/independent; State transitions are driven by the object itself (e.g. circuit breaker CLOSED->OPEN).

**Q2. Why use Decorator rather than AOP/proxies?**  
Decorators are explicit, type-safe, trivially testable and have no reflection. AOP (Spring) is more concise for dozens of cross-cutting methods but hides control flow.

**Q3. How do you implement an A/B test router?**  
Hash a stable key (userId) with a salt into [0,100), compare against traffic split; keep assignment sticky and log the variant with each prediction for analysis.

**Q4. What breaks Liskov in model hierarchies?**  
Subtypes returning different dimensionality, throwing new unchecked exceptions for valid inputs, or requiring pre-warm calls the base type doesn't.

**Q5. Explain the Builder's advantage over telescoping constructors.**  
Named optional parameters, immutability of the product, centralised validation, and readable call-sites; records + builder gives the best of both.

**Q6. Where does dependency injection fit?**  
Composition root builds the decorator graph once; classes receive collaborators via constructor so tests can pass fakes without a container.

## 5. Run the Lab

```bash
cd day-03-oop-and-design-patterns-for-ai-backends
javac DesignPatternsForAiBackends.java
java DesignPatternsForAiBackends
```
The program prints benchmark tables and finishes with `Day 03 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
