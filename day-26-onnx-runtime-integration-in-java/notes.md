# Day 26 - ONNX Runtime Integration in Java

> **Phase:** AI / ML Integration | **Date:** 2026-03-26 | **Code:** [`MiniOnnxRuntime.java`](./MiniOnnxRuntime.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **ONNX** = portable model graph: *initializers* (weights), *inputs/outputs*, and *nodes* (op type + input/output names). A runtime executes nodes in topological order.
* **ONNX Runtime Java**: `OrtEnvironment` (one per process) -> `OrtSession` (loaded model, thread-safe `run`) -> `OnnxTensor` inputs -> `Result`. Reuse sessions; create tensors from direct buffers to avoid copies.
* Tune: intra/inter-op threads, execution providers (CPU/CUDA), graph optimisation level, **dynamic batch axis**.
* Today: a mini executor implementing Gemm, Relu, Softmax over `float[]` tensors, with input validation and batch-size scaling.

## 2. Memory Architecture Breakdown

Tensor = shape + contiguous float buffer. Native ORT holds weights off-heap; `OnnxTensor.createTensor(env, FloatBuffer)` wraps a direct buffer (no copy). Close tensors/results (try-with-resources) or native memory leaks.

## 3. Interview Edge Cases & Pitfalls

1. Close `OnnxTensor` and `Result` - GC does not free native memory promptly.
2. Input name/shape/dtype must match the model exactly.
3. Batch 1 is latency-optimal; larger batches maximise throughput.

## 4. Interview Q&A

**Q1. Why ONNX?**  
Framework-neutral export (PyTorch/TF/sklearn) and one optimised runtime for serving without Python.

**Q2. How do you serve concurrently?**  
A shared `OrtSession` is thread-safe; call `run` from many threads or micro-batch requests.

## 5. Production Mapping (real libraries)

```xml
<dependency><groupId>com.microsoft.onnxruntime</groupId><artifactId>onnxruntime</artifactId><version>1.18.0</version></dependency>
```
```java
OrtEnvironment env = OrtEnvironment.getEnvironment();
try (OrtSession s = env.createSession("model.onnx", new OrtSession.SessionOptions());
     OnnxTensor in = OnnxTensor.createTensor(env, new float[][]{{1f,2f,3f,4f}});
     OrtSession.Result out = s.run(Map.of("input", in))) {
    float[][] probs = (float[][]) out.get(0).getValue();
}
```

## 6. Run the Lab

```bash
cd day-26-onnx-runtime-integration-in-java
javac MiniOnnxRuntime.java
java MiniOnnxRuntime
```
The program prints benchmark tables and finishes with `Day 26 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
