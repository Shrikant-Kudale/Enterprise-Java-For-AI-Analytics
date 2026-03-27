# Day 27 - Deep Java Library (DJL) Inference Pipeline

> **Phase:** AI / ML Integration | **Date:** 2026-03-27 | **Code:** [`DjlStylePipeline.java`](./DjlStylePipeline.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **DJL** abstractions: `Criteria` (what model to load + engine) -> `ZooModel` -> `Predictor<I,O>` (not thread-safe: one per thread or pool) with a **`Translator<I,O>`** (`processInput`: raw -> `NDList`, `processOutput`: `NDList` -> result).
* **NDManager** owns native tensor memory; scope it in try-with-resources.
* **Dynamic micro-batching**: collect concurrent requests for a few ms, run one batched forward pass, scatter results. Raises throughput on GPU/CPU models with high per-call overhead; costs a bounded latency wait.
* Engines: PyTorch, TensorFlow, ONNX Runtime, MXNet - pick via `optEngine`.

## 2. Memory Architecture Breakdown

Per-call fixed overhead (kernel launch, JNI, tensor alloc) is amortised across the batch: cost ~ fixed + perItem*batch.

## 3. Interview Edge Cases & Pitfalls

1. Predictor is not thread-safe - pool them.
2. Close NDManager/NDArray (native memory).
3. Batch wait time adds to p50; cap it (1-10 ms) and the batch size.

## 4. Interview Q&A

**Q1. Why micro-batch?**  
Per-inference fixed cost dominates for small inputs; batching amortises it and improves accelerator utilisation.

**Q2. What does a Translator do?**  
Pre/post-processing: tokenisation or resize/normalise on the way in, softmax/label mapping on the way out.

## 5. Production Mapping (real libraries)

```xml
<dependency><groupId>ai.djl</groupId><artifactId>api</artifactId><version>0.28.0</version></dependency>
<dependency><groupId>ai.djl.pytorch</groupId><artifactId>pytorch-engine</artifactId><version>0.28.0</version></dependency>
```
```java
Criteria<String, Classification> c = Criteria.builder().setTypes(String.class, Classification.class)
    .optModelPath(Paths.get("model")).optTranslator(new SentimentTranslator()).build();
try (ZooModel<String, Classification> m = c.loadModel(); Predictor<String, Classification> p = m.newPredictor()) { p.predict("great"); }
```

## 6. Run the Lab

```bash
cd day-27-deep-java-library-djl-inference-pipeline
javac DjlStylePipeline.java
java DjlStylePipeline
```
The program prints benchmark tables and finishes with `Day 27 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
