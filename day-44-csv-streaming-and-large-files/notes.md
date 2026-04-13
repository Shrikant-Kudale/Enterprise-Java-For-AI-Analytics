# Day 44 - CSV Streaming and Large Files

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-13 | **Code:** [`CsvStreaming.java`](./CsvStreaming.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Stream line by line (`BufferedReader.lines()`, `Files.lines`) so memory is O(1). Parse into primitives, skip header, handle quotes with a real CSV lib for production. Never `readAllLines` a huge file.

## 2. Memory Architecture Breakdown

`Files.lines` holds an open file handle - close it (try-with-resources).

## 3. Interview Edge Cases & Pitfalls

1. Quoted commas break `split`.
2. Charset must be explicit.

## 4. Interview Q&A

**Q1. How to read a 50 GB CSV?**  
Stream + chunk + parallel by file split; or load into Parquet.

**Q2. Why close Files.lines?**  
It keeps the file open.

## 5. Run the Lab

```bash
cd day-44-csv-streaming-and-large-files
javac CsvStreaming.java
java CsvStreaming
```
The program prints benchmark tables and finishes with `Day 44 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
