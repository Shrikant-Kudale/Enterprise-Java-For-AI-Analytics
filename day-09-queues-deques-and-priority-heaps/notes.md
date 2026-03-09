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

## 2. Memory Architecture Breakdown

```
Heap in array (min-heap)               ArrayDeque ring buffer (cap=8)
        1                              idx: 0 1 2 3 4 5 6 7
      /   \                                [d e . . . a b c]
     3     2        [1,3,2,7,4,5,9]                 ^tail    ^head
    / \   / \
   7   4 5   9                         addFirst: head=(head-1)&7 ; addLast: tail=(tail+1)&7
```
* PriorityQueue stores references: 1M `Integer`s = 4 MB array + 16 MB boxed objects. A primitive `int[]`-based heap (or fastutil `IntHeapPriorityQueue`) cuts memory 5x.
* Sift operations touch O(log n) cache lines: for big heaps, a **4-ary heap** or B-heap improves locality (children share a cache line).
* `PriorityQueue` growth: doubles when < 64 elements else +50% - similar amortised behaviour to ArrayList.
* Blocking queues: `ArrayBlockingQueue` pre-allocates its array (no GC churn, bounded memory); `LinkedBlockingQueue` allocates a node per element (garbage).

## 3. Interview Edge Cases & Pitfalls

1. `PriorityQueue.iterator()` / `toString()` does not return sorted order - only `poll()` does.
2. `PriorityQueue` is not stable: equal priorities pop in arbitrary order. Add a sequence number tiebreaker for FIFO among equals.
3. Comparators that overflow (`a - b` with ints) corrupt heap order - use `Integer.compare`.
4. Mutating an element's priority while it's inside the heap breaks invariants; remove + re-insert.
5. `ArrayDeque` rejects `null`; `offer(null)` NPE. `LinkedList` allows it - and then `poll()` ambiguity appears.
6. `Queue.add` throws on capacity failure, `offer` returns false; `remove` throws on empty, `poll` returns null; `element`/`peek` similar.
7. A **max**-heap via `Collections.reverseOrder()` / `Comparator.reverseOrder()`; to keep the K *largest* use a **min**-heap of size K.
8. Unbounded queues hide overload until OOM - prefer bounded + rejection policy.

