# Day 24 - gRPC for Low-Latency Microservice RPC

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-24 | **Code:** [`BinaryRpc.java`](./BinaryRpc.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **gRPC** = HTTP/2 (multiplexed streams, header compression) + **Protocol Buffers** (compact binary, schema-first, forward compatible).
* Four call types: unary, server streaming, client streaming, bidirectional streaming. Deadlines propagate; use `withDeadlineAfter`.
* Protobuf wire format: each field = `tag (field<<3 | wiretype)` + value; ints are **varints** (7 bits per byte); strings/bytes/packed arrays are length-delimited. Unknown fields are skipped -> add fields safely, never reuse numbers.
* Today's lab hand-rolls the wire format and a framed TCP server using virtual threads, then measures p50/p99 latency.

## 2. Memory Architecture Breakdown

Varint: 300 -> 0xAC 0x02 (2 bytes) vs 4/8 bytes fixed. Floats packed as little-endian 4-byte blocks. Reuse `ByteBuffer`s to avoid per-call garbage.

## 3. Interview Edge Cases & Pitfalls

1. Set a deadline on every call; propagate cancellation.
2. Never renumber proto fields.
3. Large messages (>4 MB default) need streaming.
4. Client channels are heavy - reuse one `ManagedChannel`.

## 4. Interview Q&A

**Q1. gRPC vs REST?**  
gRPC: binary, typed contracts, streaming, HTTP/2 multiplexing - best for internal service-to-service; REST/JSON for public APIs and browsers.

**Q2. How does protobuf stay backward compatible?**  
Field numbers are the identity; unknown fields are ignored and missing fields default.

## 5. Production Mapping (real libraries)

```proto
service Inference { rpc Predict (PredictRequest) returns (PredictResponse); }
message PredictRequest { string model = 1; int64 id = 2; repeated float x = 3; }
```
```xml
<dependency><groupId>io.grpc</groupId><artifactId>grpc-netty-shaded</artifactId><version>1.64.0</version></dependency>
<dependency><groupId>io.grpc</groupId><artifactId>grpc-protobuf</artifactId><version>1.64.0</version></dependency>
<dependency><groupId>io.grpc</groupId><artifactId>grpc-stub</artifactId><version>1.64.0</version></dependency>
```

## 6. Run the Lab

```bash
cd day-24-grpc-for-low-latency-microservice-rpc
javac BinaryRpc.java
java BinaryRpc
```
The program prints benchmark tables and finishes with `Day 24 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
