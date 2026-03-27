# Day 27 - Deep Java Library (DJL) Inference Pipeline

> **Phase:** AI / ML Integration | **Date:** 2026-03-27 | **Code:** [`DjlStylePipeline.java`](./DjlStylePipeline.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **DJL** abstractions: `Criteria` (what model to load + engine) -> `ZooModel` -> `Predictor<I,O>` (not thread-safe: one per thread or pool) with a **`Translator<I,O>`** (`processInput`: raw -> `NDList`, `processOutput`: `NDList` -> result).
* **NDManager** owns native tensor memory; scope it in try-with-resources.
* **Dynamic micro-batching**: collect concurrent requests for a few ms, run one batched forward pass, scatter results. Raises throughput on GPU/CPU models with high per-call overhead; costs a bounded latency wait.
* Engines: PyTorch, TensorFlow, ONNX Runtime, MXNet - pick via `optEngine`.

