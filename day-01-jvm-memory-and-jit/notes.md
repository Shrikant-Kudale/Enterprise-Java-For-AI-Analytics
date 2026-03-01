# Day 01 - JVM Memory Model and JIT Compilation

> **Phase:** Java Foundations | **Date:** 2026-03-01 | **Code:** [`JvmMemoryAndJit.java`](./JvmMemoryAndJit.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Runtime data areas
| Area | Scope | Holds | Failure mode |
|------|-------|-------|--------------|
| Heap | shared | objects, arrays | `OutOfMemoryError: Java heap space` |
| Metaspace | shared (native memory) | class metadata, method bytecode | `OutOfMemoryError: Metaspace` |
| Thread stack | per thread | frames, locals, operand stack | `StackOverflowError` |
| PC register | per thread | next bytecode index | - |
| Code cache | shared | JIT-compiled machine code | "CodeCache is full", compilation stops |
| Direct memory | shared (native) | `ByteBuffer.allocateDirect`, NIO, Netty | `OutOfMemoryError: Direct buffer memory` |

### Generational heap (G1 default since Java 9)
G1 divides the heap into ~2048 equal **regions** tagged Eden / Survivor / Old / Humongous. Young collections copy live objects Eden -> Survivor -> Old (age threshold, `-XX:MaxTenuringThreshold`, default 15). Objects larger than half a region are *humongous* and allocated directly in contiguous old regions - a classic source of fragmentation when you allocate big `float[]` tensors per request.

### Allocation fast path: TLABs
Each thread owns a Thread-Local Allocation Buffer inside Eden. `new` is a pointer bump (~10 instructions), no lock. When the TLAB is exhausted the thread requests a new one (slow path, CAS on Eden top).

### Object layout (64-bit HotSpot, compressed oops)
`[mark word 8B][klass pointer 4B][fields...][padding to 8B]`. A `record Point(double x,double y)` = 12 + 16 = 28 -> **32 bytes**. An `Integer` is 16 bytes, so `List<Integer>` costs ~20 bytes/element vs 4 for `int[]`. Compressed oops work up to ~32 GB heap; beyond that references double to 8 bytes (use 30-31 GB max heaps as a sweet spot).

### Tiered JIT
Level 0 interpreter -> Levels 1-3 C1 (quick, with profiling at level 3) -> Level 4 C2 (aggressive). Triggers are invocation + back-edge counters (`-XX:Tier4InvocationThreshold`). Key optimisations: inlining (`MaxInlineSize=35` bytes, `FreqInlineSize=325`), escape analysis + scalar replacement, loop unrolling, range-check elimination, devirtualisation via class hierarchy analysis / type profiles, lock elision/coarsening. **OSR** (on-stack replacement) swaps a long-running interpreted loop into compiled code mid-execution. **Deoptimisation** discards compiled code when an assumption breaks (new subclass loaded, uncommon trap hit).

### Why this matters for AI backends
* Tensor buffers belong off-heap (direct / `MemorySegment`) so GC never copies them and native libs (ONNX, DJL) can read them without a copy.
* Latency SLOs (p99) are dominated by GC pauses and JIT warm-up: pre-warm with synthetic traffic before joining the load balancer.
* In containers use `-XX:MaxRAMPercentage=70` (not a fixed `-Xmx`) and leave room for Metaspace + direct memory + thread stacks.

## 2. Memory Architecture Breakdown

```
 +--------------------------- JVM process (RSS) ---------------------------+
 |  Java Heap (-Xms/-Xmx)         |  Metaspace   |  Code Cache  |  Threads  |
 |  +-------+--------+---------+  |  (native)    |  (C1+C2 code)|  stacks   |
 |  | Eden  | Surv   |  Old    |  |              |              |  1MB each |
 |  +-------+--------+---------+  |              |              |           |
 |                                 +--------------+--------------+-----------+
 |  Direct buffers / mmap (native, -XX:MaxDirectMemorySize)  | GC structures |
 +-----------------------------------------------------------+---------------+
```
* `RSS = heap + metaspace + code cache + thread stacks + direct + GC remembered sets + libc arenas`. A 4 GB `-Xmx` JVM routinely uses 5-5.5 GB RSS - size container limits accordingly.
* **Remembered sets / card tables** let a young GC avoid scanning the whole old gen; they cost ~5-10 % of heap in G1.
* **Safepoints** - GC, deopt and biased-lock revocation stop all threads at safepoints; a counted loop without safepoint polls (`int` loop) delays them (time-to-safepoint problem).

