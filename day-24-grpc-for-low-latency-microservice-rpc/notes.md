# Day 24 - gRPC for Low-Latency Microservice RPC

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-24 | **Code:** [`BinaryRpc.java`](./BinaryRpc.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **gRPC** = HTTP/2 (multiplexed streams, header compression) + **Protocol Buffers** (compact binary, schema-first, forward compatible).
* Four call types: unary, server streaming, client streaming, bidirectional streaming. Deadlines propagate; use `withDeadlineAfter`.
* Protobuf wire format: each field = `tag (field<<3 | wiretype)` + value; ints are **varints** (7 bits per byte); strings/bytes/packed arrays are length-delimited. Unknown fields are skipped -> add fields safely, never reuse numbers.
* Today's lab hand-rolls the wire format and a framed TCP server using virtual threads, then measures p50/p99 latency.

## 2. Memory Architecture Breakdown

Varint: 300 -> 0xAC 0x02 (2 bytes) vs 4/8 bytes fixed. Floats packed as little-endian 4-byte blocks. Reuse `ByteBuffer`s to avoid per-call garbage.

