# Day 71 - API Security: HMAC and JWT

> **Phase:** Backend Systems & Security | **Date:** 2026-05-10 | **Code:** [`HmacSignedTokens.java`](./HmacSignedTokens.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A JWT = base64url(header).base64url(payload).signature. **HS256** signs with a shared secret (HMAC-SHA256); RS256 uses a key pair. Always verify signature, `exp`, `aud`, `iss`; compare signatures in constant time; use HTTPS; keep tokens short-lived.

