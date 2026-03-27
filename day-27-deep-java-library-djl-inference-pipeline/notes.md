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

