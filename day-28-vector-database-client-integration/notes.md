# Day 28 - Vector Database Client Integration

> **Phase:** AI / ML Integration | **Date:** 2026-03-28 | **Code:** [`VectorSearchClient.java`](./VectorSearchClient.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* A **vector database** stores embeddings and answers *k-nearest-neighbour* queries by cosine / dot / L2 distance, with optional **metadata filters**.
* **Exact (flat)** search is O(N*d). **ANN** indexes trade recall for speed: **IVF** (k-means partitions, probe `nprobe` lists), **HNSW** (navigable small-world graph, best recall/latency), **PQ** (compress vectors).
* Measure **recall@k** against exact search; tune `nprobe` / `ef_search`.
* Client patterns: upsert in batches, idempotent ids, normalise vectors once, timeouts + retries, and hybrid search (BM25 + vectors).
* Qdrant, Milvus, pgvector, Pinecone, Weaviate all expose REST/gRPC clients; today builds Qdrant-style payloads and an IVF index from scratch.

## 2. Memory Architecture Breakdown

1M x 768 float32 = 3 GB raw. IVF adds centroid table + id lists; HNSW adds ~M*2 neighbour ids per vector; PQ cuts vectors to 8-64 bytes.

## 3. Interview Edge Cases & Pitfalls

1. Normalise vectors if using dot product as cosine.
2. Filtered ANN can lose recall - test with filters.
3. Embedding model change = full re-index.

## 4. Interview Q&A

**Q1. IVF vs HNSW?**  
IVF is memory-light and simple but needs training and probes; HNSW gives better recall/latency at higher memory and build cost.

**Q2. How do you evaluate an ANN index?**  
recall@k versus exact search on held-out queries plus p50/p99 latency.

## 5. Production Mapping (real libraries)

```xml
<dependency><groupId>io.qdrant</groupId><artifactId>client</artifactId><version>1.9.1</version></dependency>
```
REST: `PUT /collections/docs/points` body `{"points":[{"id":1,"vector":[...],"payload":{"tenant":"a"}}]}`; search `POST /collections/docs/points/search` body `{"vector":[...],"limit":10,"filter":{"must":[{"key":"tenant","match":{"value":"a"}}]}}`.

## 6. Run the Lab

```bash
cd day-28-vector-database-client-integration
javac VectorSearchClient.java
java VectorSearchClient
```
The program prints benchmark tables and finishes with `Day 28 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
