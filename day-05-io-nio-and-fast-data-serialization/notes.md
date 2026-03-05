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

## 2. Memory Architecture Breakdown

```
Heap stream write:   float[] -> byte[] (heap) -> [copy] -> kernel page cache -> disk
Direct buffer write: float -> direct ByteBuffer (native) ----------> kernel page cache -> disk
mmap read:           disk -> page cache <==== mapped into process address space (no copy; page-fault on touch)
sendfile:            page cache --------------------------------------> NIC (never enters JVM)
```
* A `MappedByteBuffer` is unmapped only when its Java object is GC'd (no public `unmap` before `MemorySegment`/Arena in Java 21+ FFM); on Windows this blocks file deletion.
* Direct buffers have a `Cleaner` (phantom reference) that frees native memory; heavy churn can hit `OutOfMemoryError: Direct buffer memory` before heap GC runs - pool and reuse them (Netty `PooledByteBufAllocator`).
* Page cache means *repeat* reads are served from RAM; benchmark with cold cache (`echo 3 > /proc/sys/vm/drop_caches`) to see disk reality.

## 3. Interview Edge Cases & Pitfalls

1. Forgetting `buffer.flip()` between write and read yields zero-length reads; `clear()` doesn't erase data, only resets indices.
2. `InputStream.read(byte[])` may return fewer bytes than requested - loop or use `readNBytes`/`readFully`.
3. `FileChannel.write(buffer)` may write partially - loop `while (buf.hasRemaining())`.
4. Always specify charset (`StandardCharsets.UTF_8`); platform default changed in Java 18 (JEP 400) but older runtimes differ.
5. `Files.readAllBytes` on a multi-GB file allocates an equally large array -> OOM. Stream or map.
6. `float` text round-trip is exact only with `Float.toString`/`parseFloat` (shortest repr) - `%.3f` formatting loses data.
7. Mapped files: writes aren't durable until `force()`; crash mid-write leaves torn records - add a length/CRC footer.
8. `BufferedWriter.close()` flushes; forgetting to close/flush truncates the tail of the file.

## 4. Interview Q&A

**Q1. Why is NIO faster for servers?**  
Selectors let one thread multiplex thousands of connections and buffers/channels avoid intermediate copies. Thread-per-connection blocking I/O scales to thousands of threads at best (virtual threads now close that gap for simple code).

**Q2. Heap vs direct buffer - when to use each?**  
Direct for long-lived, I/O- or JNI-bound buffers (network, ONNX tensors); heap for short-lived small buffers where allocation speed and GC management win.

**Q3. How does `transferTo` work?**  
Delegates to `sendfile`/`splice`, letting the kernel move page-cache pages to the socket directly, eliminating two copies and two context switches.

**Q4. Why columnar formats for analytics?**  
Queries touch a few columns; columnar layout reads only those, compresses similar values together and enables SIMD. Row formats win for point lookups and writes.

**Q5. How would you ship 1 TB of training features to a Java inference trainer?**  
Sharded Parquet/Arrow files, memory-map or stream row-groups, parallel readers with bounded queues, avoid per-row object creation (read into primitive arrays).

**Q6. What's wrong with Java serialization?**  
Large, slow, brittle across versions and a security vulnerability class (gadget chains). Use schema'd formats like Protobuf/Avro.

## 5. Run the Lab

```bash
cd day-05-io-nio-and-fast-data-serialization
javac FastDataSerialization.java
java FastDataSerialization
```
The program prints benchmark tables and finishes with `Day 05 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
