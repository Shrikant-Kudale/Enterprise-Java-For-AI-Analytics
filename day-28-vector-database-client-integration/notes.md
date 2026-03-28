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

