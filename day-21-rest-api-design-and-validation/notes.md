# Day 21 - REST API Design and Validation

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-21 | **Code:** [`RestValidation.java`](./RestValidation.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Resources and verbs**: nouns in URLs, `GET` safe/idempotent, `PUT` idempotent, `POST` creates, `DELETE` idempotent; return correct status codes (201, 204, 400, 404, 409, 422, 429).
* **Validation** at the edge (Bean Validation: `@NotBlank`, `@Size`, `@Min`); return **RFC 7807 problem+json** with field errors.
* **Pagination**: prefer opaque *cursors* over `OFFSET` (stable, O(1) seek). **Idempotency-Key** header makes retried POSTs safe.
* Version via URL or header; never break existing fields; document with OpenAPI.

