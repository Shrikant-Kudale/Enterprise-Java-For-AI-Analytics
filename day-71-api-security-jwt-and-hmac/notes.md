# Day 71 - API Security: HMAC and JWT

> **Phase:** Backend Systems & Security | **Date:** 2026-05-10 | **Code:** [`HmacSignedTokens.java`](./HmacSignedTokens.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A JWT = base64url(header).base64url(payload).signature. **HS256** signs with a shared secret (HMAC-SHA256); RS256 uses a key pair. Always verify signature, `exp`, `aud`, `iss`; compare signatures in constant time; use HTTPS; keep tokens short-lived.

## 2. Memory Architecture Breakdown

HMAC is a keyed hash: no key, no valid tag.

## 3. Interview Edge Cases & Pitfalls

1. `alg=none` attacks - pin allowed algorithms.
2. Never put secrets in the payload (it is only encoded).

## 4. Interview Q&A

**Q1. HS256 vs RS256?**  
Shared secret vs public/private keys - RS256 lets services verify without signing power.

**Q2. Why constant-time compare?**  
Avoid timing attacks.

