# Day 43 - JSON Parsing from Scratch

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-12 | **Code:** [`JsonParserLite.java`](./JsonParserLite.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A recursive-descent parser: look at one char, dispatch to `parseArray/parseString/parseNumber`, advance an index. Production code uses Jackson (streaming `JsonParser`, `ObjectMapper`, `@JsonCreator`).

