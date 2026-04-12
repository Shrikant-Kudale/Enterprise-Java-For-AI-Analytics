# Day 43 - JSON Parsing from Scratch

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-12 | **Code:** [`JsonParserLite.java`](./JsonParserLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A recursive-descent parser: look at one char, dispatch to `parseArray/parseString/parseNumber`, advance an index. Production code uses Jackson (streaming `JsonParser`, `ObjectMapper`, `@JsonCreator`).

## 2. Memory Architecture Breakdown

The parse tree is nested `List/Map` objects; streaming parsers avoid materialising it.

## 3. Interview Edge Cases & Pitfalls

1. Escapes and unicode in strings.
2. Depth limits to stop stack overflow.

