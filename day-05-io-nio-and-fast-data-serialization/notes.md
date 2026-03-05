# Day 05 - I/O, NIO and Fast Data Serialization

> **Phase:** Java Foundations | **Date:** 2026-03-05 | **Code:** [`FastDataSerialization.java`](./FastDataSerialization.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### java.io vs java.nio
* **java.io**: blocking byte/char *streams*, one thread per connection/file op, small copies through user-space arrays. Always buffer (`BufferedInputStream`, 8 KB default).
* **java.nio**: **Buffers** (position/limit/capacity), **Channels** (bulk, can be non-blocking, `FileChannel`, `SocketChannel`), **Selectors** (readiness multiplexing - how Netty/Tomcat NIO scale to 100k sockets).
* **java.nio.file**: `Path`, `Files.*` (walk, lines, readAllBytes, atomic move), `WatchService`.

### Buffer kinds
| Kind | Memory | Pros | Cons |
|------|--------|------|------|
| Heap `ByteBuffer.allocate` | Java heap | cheap alloc, GC managed | JDK copies to a native temp buffer on I/O |
| Direct `allocateDirect` | native | zero-copy to OS/JNI (ONNX/DJL), no GC copy | expensive alloc/free, counted by `MaxDirectMemorySize` |
| Mapped `FileChannel.map` | OS page cache | random access to huge files, no read syscalls | page faults, 2 GB per mapping (int index), unmapping is GC-driven |

### Zero copy
`FileChannel.transferTo(pos, count, target)` uses `sendfile(2)` on Linux: file -> socket without entering user space. This is how static file servers and Kafka brokers move data.

### Serialization formats for analytics
* **CSV/JSON**: human readable, 3-10x larger, slow float parse. Great for debugging, bad for features at scale.
* **Java serialization**: avoid (slow, bloated, security risk - deserialization gadgets).
* **Protobuf/Avro/FlatBuffers/MessagePack**: schema'd, compact, versionable. Avro/Parquet for data lakes.
* **Parquet / Arrow**: *columnar* - read only needed columns, vectorised, compress well (dictionary/RLE). Arrow IPC for zero-copy interchange with Python.
* **Fixed-width binary records** (today's lab): fastest for homogeneous numeric feature rows; trivially mmap-able: `offset = row * RECORD_SIZE`.

### Endianness
`ByteBuffer` defaults to BIG_ENDIAN (network order, same as `DataOutputStream`). Native libraries (ONNX, NumPy `.npy`) are usually little-endian: call `order(ByteOrder.LITTLE_ENDIAN)`.

