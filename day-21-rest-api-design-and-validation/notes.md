# Day 21 - REST API Design and Validation

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-21 | **Code:** [`RestValidation.java`](./RestValidation.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Resources and verbs**: nouns in URLs, `GET` safe/idempotent, `PUT` idempotent, `POST` creates, `DELETE` idempotent; return correct status codes (201, 204, 400, 404, 409, 422, 429).
* **Validation** at the edge (Bean Validation: `@NotBlank`, `@Size`, `@Min`); return **RFC 7807 problem+json** with field errors.
* **Pagination**: prefer opaque *cursors* over `OFFSET` (stable, O(1) seek). **Idempotency-Key** header makes retried POSTs safe.
* Version via URL or header; never break existing fields; document with OpenAPI.

## 2. Memory Architecture Breakdown

Validation by reflection reads annotation metadata once per class (cache it in production); a cursor is just base64 of the last seen key.

## 3. Interview Edge Cases & Pitfalls

1. Validate *and* bound request size (arrays, strings) to avoid DoS.
2. Do not leak stack traces in error bodies.
3. Idempotency store needs TTL and must be atomic (putIfAbsent).

## 4. Interview Q&A

**Q1. PUT vs PATCH?**  
PUT replaces the full resource (idempotent); PATCH applies a partial change.

**Q2. Why cursor pagination?**  
OFFSET scans and skips rows (O(n)) and drifts when data changes; cursors seek by key.

## 5. Run the Lab

```bash
cd day-21-rest-api-design-and-validation
javac RestValidation.java
java RestValidation
```
The program prints benchmark tables and finishes with `Day 21 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
