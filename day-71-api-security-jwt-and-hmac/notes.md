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

## 5. Run the Lab

```bash
cd day-71-api-security-jwt-and-hmac
javac HmacSignedTokens.java
java HmacSignedTokens
```
The program prints benchmark tables and finishes with `Day 71 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
