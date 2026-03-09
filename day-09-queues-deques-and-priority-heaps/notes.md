# Day 09 - Queues, Deques and Priority Heaps

> **Phase:** Collections & Data Structures | **Date:** 2026-03-09 | **Code:** [`QueuesDequesHeaps.java`](./QueuesDequesHeaps.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### The Queue family
| Type | Structure | Notes |
|------|-----------|-------|
| `ArrayDeque` | circular array, power-of-two capacity | fastest stack/queue/deque; no nulls; not thread-safe |
| `LinkedList` | doubly-linked | allows nulls; avoid as a queue |
| `PriorityQueue` | binary min-heap in `Object[]` | `offer/poll` O(log n), `peek` O(1), `remove(Object)` O(n); iteration order is *not* sorted |
| `ArrayBlockingQueue` | bounded circular array, one lock | back-pressure for producer/consumer |
| `LinkedBlockingQueue` | linked, two locks (put/take) | optionally bounded; higher throughput under contention |
| `PriorityBlockingQueue` | heap + lock | unbounded |
| `ConcurrentLinkedQueue/Deque` | lock-free (Michael-Scott CAS) | unbounded, weakly consistent iteration |
| `SynchronousQueue` | no capacity - hand-off | used by `newCachedThreadPool` |
| `DelayQueue` | heap ordered by expiry | schedulers, TTL eviction |

### Binary heap
Complete binary tree stored in an array: parent `(k-1)/2`, children `2k+1`, `2k+2`. `siftUp` after append, `siftDown` after replacing the root with the last element. Build-heap from n items is **O(n)** (sift down from n/2 down to 0), heapsort O(n log n) in place.

### ArrayDeque ring buffer
`head` and `tail` indices wrap with `& (cap-1)`. Full when `head == tail` after insertion -> double capacity and re-linearise. No per-element allocation => ideal cache behaviour.

### Algorithms you must have ready
* **Top-K** of a stream: min-heap of size K -> O(n log K), O(K) memory (never sort the full stream).
* **K-way merge** of sorted runs (external sort, merging per-shard results): heap of cursors -> O(N log K).
* **Sliding window maximum**: monotonic deque of indices, amortised O(n).
* **Running median**: two heaps (day 30). **Dijkstra / A\***: PQ keyed by distance; use lazy deletion because `decreaseKey` isn't supported.
* **Task scheduling / rate shaping**: `DelayQueue`, hierarchical timing wheels.

### AI backend relevance
Top-K nearest neighbours (vector search), beam search decoding (keep best K hypotheses), merge of ranked results from sharded indices, priority inference queues (premium tenants first), bounded queues for micro-batching back-pressure.

