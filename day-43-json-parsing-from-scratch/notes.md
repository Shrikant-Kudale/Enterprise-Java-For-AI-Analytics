# Day 43 - JSON Parsing from Scratch

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-12 | **Code:** [`JsonParserLite.java`](./JsonParserLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A recursive-descent parser: look at one char, dispatch to `parseArray/parseString/parseNumber`, advance an index. Production code uses Jackson (streaming `JsonParser`, `ObjectMapper`, `@JsonCreator`).

## 2. Memory Architecture Breakdown

The parse tree is nested `List/Map` objects; streaming parsers avoid materialising it.

## 3. Interview Edge Cases & Pitfalls

1. Escapes and unicode in strings.
2. Depth limits to stop stack overflow.

## 4. Interview Q&A

**Q1. Jackson streaming vs tree vs databind?**  
Streaming = lowest memory, tree = flexible, databind = typed POJOs.

**Q2. Recursive descent?**  
One function per grammar rule.

## 5. Run the Lab

```bash
cd day-43-json-parsing-from-scratch
javac JsonParserLite.java
java JsonParserLite
```
The program prints benchmark tables and finishes with `Day 43 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
