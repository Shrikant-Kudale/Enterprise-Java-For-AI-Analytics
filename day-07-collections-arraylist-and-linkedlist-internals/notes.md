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

## 2. Memory Architecture Breakdown

```
ArrayList (size=5, cap=10)                 LinkedList (size=3)
 elementData --> [ r0 r1 r2 r3 r4 .. .. ..]   first -> [prev|item|next] <-> [prev|item|next] <-> [..] <- last
                  contiguous refs                      each node 24B scattered in heap

Growth 10 -> 15 -> 22 -> 33 -> 49 -> 73 ...  (each step allocates new array + copies; old array becomes garbage)
```
* Array growth of a 100 MB list allocates a 150 MB array while the old one is still live: peak footprint **2.5x** momentarily - presize large lists.
* After `remove`, ArrayList nulls the vacated slot (`elementData[--size] = null`) so the GC can reclaim the element - forgetting this in custom collections is the classic "loitering" memory leak.
* Because linked nodes are allocated at different times they are scattered; a traversal is a chain of dependent loads (~100 cycles each on cache miss) while array scan is prefetched at ~1 element/cycle.

## 3. Interview Edge Cases & Pitfalls

1. `list.remove(int)` vs `list.remove(Object)`: for `List<Integer>`, `remove(1)` removes *index* 1; use `remove(Integer.valueOf(1))`.
2. Removing inside for-each throws CME - use `Iterator.remove()` or `removeIf` (single pass, O(n)).  The notorious exception: removing the *second-to-last* element silently ends the loop without CME.
3. `Arrays.asList(arr).add(x)` throws `UnsupportedOperationException`; `List.of(...).contains(null)` throws NPE.
4. `ArrayList.removeAll(Collection)` is O(n*m) if the argument is a List - pass a `HashSet`.
5. `new ArrayList<>(Collections.nCopies(n, null))` gives size n; `new ArrayList<>(n)` gives size 0 - `set(0, x)` throws `IndexOutOfBounds`.
6. `LinkedList.get(i)` in a for-loop makes iteration O(n^2).
7. Sublist of a sublist after parent modification -> CME on any access.

## 4. Interview Q&A

**Q1. ArrayList vs LinkedList - which would you pick for a queue of 1M items?**  
Neither: `ArrayDeque`. For random-access lists, `ArrayList`. LinkedList's per-node allocation and poor locality make it slower than ArrayList even for many mid-list insertions.

**Q2. How does ArrayList grow and what is the amortised cost of add?**  
Capacity grows by 1.5x using `Arrays.copyOf`. Total copying over n appends is bounded by a geometric series, so amortised O(1).

**Q3. What is fail-fast and is it thread-safe?**  
Iterators compare `modCount` to an expected value and throw CME on mismatch. It's a best-effort bug detector, not synchronisation; use `CopyOnWriteArrayList` or locks for concurrency.

**Q4. How would you remove all elements matching a predicate efficiently?**  
`list.removeIf(pred)` - ArrayList implements it with a single compaction pass (O(n)) instead of repeated shifting (O(n^2)).

**Q5. Why might `ArrayList<Integer>` with 10M entries blow the heap even though `int[]` wouldn't?**  
Each entry costs ~20 bytes (ref + 16-byte Integer) vs 4 bytes, plus resize doubling; use primitive arrays or primitive collections.

**Q6. Implement a dynamic array interview-style - what do you watch for?**  
Index checks, null-ing removed slots, overflow in capacity computation, modCount, amortised growth factor, and iterator remove.

