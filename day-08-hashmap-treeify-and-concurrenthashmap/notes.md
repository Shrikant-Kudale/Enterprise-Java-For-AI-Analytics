# Day 08 - HashMap, Treeification and ConcurrentHashMap

> **Phase:** Collections & Data Structures | **Date:** 2026-03-08 | **Code:** [`HashMapInternals.java`](./HashMapInternals.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### HashMap algorithm (Java 8+)
1. `h = key.hashCode(); h ^= h >>> 16` - **spread** high bits into low bits because the index uses only the low bits.
2. `index = (n - 1) & h` - table length `n` is always a power of two so `&` replaces `%`.
3. Bucket holds a singly-linked list; new nodes are appended at the **tail**.
4. If a chain reaches **TREEIFY_THRESHOLD = 8** *and* table length >= **MIN_TREEIFY_CAPACITY = 64**, the bin becomes a **red-black tree** (`TreeNode`), ordered by hash then `Comparable` (if the key's class implements `Comparable<itself>`) then identity tie-break. If the table is smaller than 64 it **resizes instead**. Trees shrink back to lists at **UNTREEIFY_THRESHOLD = 6**.
5. `size > threshold (capacity * 0.75)` -> **resize** to 2x; each bin splits into a "lo" and "hi" list preserving order (no re-hash needed: the new bit decides).
6. Defaults: capacity 16, load factor 0.75, lazily allocated. `new HashMap<>(expected / 0.75 + 1)` avoids resizes (`HashMap.newHashMap(n)` in Java 19+).

Complexity: average O(1); worst-case O(log n) per lookup thanks to treeification (was O(n) before Java 8) - **only** if keys are `Comparable`; for non-comparable keys with equal hashes lookups in a tree bin may still scan O(n).

### Hash-flooding (algorithmic complexity attack)
An attacker submitting thousands of keys with identical hashes (JSON field names, query params) degrades a service to O(n^2). Java 8 treeification is the mitigation; also cap request sizes and prefer `Comparable` keys.

### ConcurrentHashMap (Java 8+)
* **No segments** any more: CAS to insert into an empty bin, `synchronized` on the *first node of the bin* for collisions - lock granularity = one bucket.
* Reads are lock-free (`volatile` table/next/val fields).
* Atomic compound ops: `putIfAbsent`, `compute`, `computeIfAbsent`, `merge` (the mapping function runs under the bin lock - keep it short, never touch the same map inside it).
* Size is a striped counter (`CounterCell[]`, like `LongAdder`): `size()` is an *estimate* under concurrent updates; use `mappingCount()` (long).
* No null keys or values (null would be ambiguous with "absent" in concurrent `get`).
* Resizing is cooperative: threads that find a forwarding node help transfer bins.
* Iterators are **weakly consistent** (never throw CME, may or may not reflect concurrent updates).

### Siblings
`LinkedHashMap` (insertion/access order -> LRU via `removeEldestEntry`), `TreeMap` (red-black, sorted, O(log n)), `IdentityHashMap`, `EnumMap`, `WeakHashMap`, `ConcurrentSkipListMap` (sorted + concurrent).

## 2. Memory Architecture Breakdown

```
table (n=16)      Node: [hash 4B][key ref][val ref][next ref] = 32B (12 hdr + 4 + 4*3 -> 32)
 [0] -> null
 [1] -> N --> N --> N            chain <8   : linked list
 [5] -> TreeNode(red/black)      chain>=8 & n>=64 : TreeNode = 56B+ (parent,left,right,prev,red)
 ...
Resize 16->32: bin j splits into j (lo) and j+16 (hi) depending on (hash & oldCap)

Per-entry cost (HashMap<Integer,Integer>): 32B node + 16B key + 16B value + 4B table slot ~ 70B   (vs 8B in int[]/int[])
```
* HashMap is **not thread-safe**: concurrent resize in Java 7 could create a cyclic bucket list (100% CPU infinite loop); Java 8 can instead lose updates or return stale/null values. Never share without synchronisation.
* `ConcurrentHashMap.CounterCell[]` adds padding (`@Contended`) to avoid false sharing - one reason it's larger than HashMap.
* `HashSet` is a `HashMap` with a dummy `PRESENT` value - 1 extra ref per entry versus a purpose-built open-addressing set.

