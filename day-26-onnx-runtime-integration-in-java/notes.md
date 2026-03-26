# Day 26 - ONNX Runtime Integration in Java

> **Phase:** AI / ML Integration | **Date:** 2026-03-26 | **Code:** [`MiniOnnxRuntime.java`](./MiniOnnxRuntime.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **ONNX** = portable model graph: *initializers* (weights), *inputs/outputs*, and *nodes* (op type + input/output names). A runtime executes nodes in topological order.
* **ONNX Runtime Java**: `OrtEnvironment` (one per process) -> `OrtSession` (loaded model, thread-safe `run`) -> `OnnxTensor` inputs -> `Result`. Reuse sessions; create tensors from direct buffers to avoid copies.
* Tune: intra/inter-op threads, execution providers (CPU/CUDA), graph optimisation level, **dynamic batch axis**.
* Today: a mini executor implementing Gemm, Relu, Softmax over `float[]` tensors, with input validation and batch-size scaling.

## 2. Memory Architecture Breakdown

Tensor = shape + contiguous float buffer. Native ORT holds weights off-heap; `OnnxTensor.createTensor(env, FloatBuffer)` wraps a direct buffer (no copy). Close tensors/results (try-with-resources) or native memory leaks.

