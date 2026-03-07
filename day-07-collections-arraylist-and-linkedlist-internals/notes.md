# Day 07 - Collections: ArrayList and LinkedList Internals

> **Phase:** Collections & Data Structures | **Date:** 2026-03-07 | **Code:** [`ListInternals.java`](./ListInternals.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### ArrayList
* Backed by `Object[] elementData`; **default capacity 10 allocated lazily on first add**.
* Growth: `newCapacity = old + (old >> 1)` (1.5x) via `Arrays.copyOf` -> amortised **O(1)** append, worst-case O(n) copy.
* `get/set` O(1); `add(i,e)`/`remove(i)` O(n) via `System.arraycopy` (a fast memmove, very cache friendly); `remove(Object)` O(n) scan + shift.
* `trimToSize()`, `ensureCapacity(n)` and `new ArrayList<>(n)` avoid regrowth when the size is known.
* `modCount` increments on structural change; iterators snapshot it and throw `ConcurrentModificationException` (fail-fast, best-effort, not a thread-safety guarantee).
* `subList` is a *view*; structural changes to the parent invalidate it.

### LinkedList
* Doubly-linked `Node{item, prev, next}`; implements both `List` and `Deque`.
* `addFirst/addLast/removeFirst` O(1); `get(i)` O(n) (walks from the nearer end, so at most n/2 hops); insertion at an iterator position O(1) *after* O(n) to find it.
* Per element: 24 bytes node (12 header + 3x4 refs) **+** the boxed element -> ~2-3x ArrayList memory.

### Conventional wisdom vs hardware reality
Textbooks say LinkedList wins for insert-in-middle. In practice **ArrayList wins almost always**: contiguous memory + prefetching makes `arraycopy` of tens of thousands of elements faster than pointer-chasing a few hundred nodes cache-miss by cache-miss. Use `ArrayDeque` (not LinkedList) for stacks/queues.

### Other list types to know
`CopyOnWriteArrayList` (read-heavy snapshot iteration, O(n) writes), `Vector` (legacy synchronized), `Arrays.asList` (fixed-size view over the array), `List.of` (immutable, null-hostile), `Collections.unmodifiableList` (read-only *view*), `ArrayDeque`.

