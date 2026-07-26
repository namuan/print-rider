# Print-Rider: Comprehensive Code Analysis — Final Synthesis

**Date:** 2026-06-10
**Orchestrator Agent:** Coordinated 6-agent analysis pipeline
**Agents deployed:** Scout → Mapper → Feature → Architecture → Trace → Judge
**Output files:** `docs/code-analysis/orchestrator-phase{1-7}*.md`

---

## Executive Summary

**print-rider** is a single-module Java/Micronaut 1.2.8 microservice (~350 lines of production code across 15 classes) that stores base64-encoded HTML documents in DynamoDB, sanitizes them via Jsoup, and renders them via a Thymeleaf template. It deploys to AWS Lambda + API Gateway with a Serverless Framework configuration.

**Overall Quality Score: 3.2 / 10** — well-structured prototype, not production-ready.

### Verdict

> **Do not deploy to production in its current state.** The codebase is a well-organized prototype that demonstrates solid Micronaut fundamentals but has accumulated severe technical debt: every major dependency is 5+ years out of date with known vulnerabilities, there is zero authentication, DynamoDB failures leak raw SDK errors to clients, and only 2 log statements exist in the entire application. With a focused 2-week sprint, critical issues can be resolved to reach a 6/10 rating. With the full 3-month roadmap, it can reach 8/10.

---

## 1. What It Does

Two REST endpoints:

| Endpoint | Method | Input | Output |
|---|---|---|---|
| `/prints` | POST | `{"document": "<base64-encoded HTML>"}` | 201 Created + `Location: {domain}/prints/{uuid}` |
| `/prints/{id}` | GET | Path param: UUID | 200 OK + `text/html` rendered page with syntax-highlighting CSS |

**Processing pipeline:** Base64 decode → Jsoup sanitize (`Whitelist.relaxed()` + `class` attr on all elements) → DynamoDB save/load → Thymeleaf render with `th:utext`.

**Deployment paths:**
- **Primary:** AWS Lambda (Java 11) + API Gateway (proxy integration, custom domain) + DynamoDB (1 RCU / 1 WCU provisioned)
- **Alternative:** Docker container running GraalVM native-image binary with local DynamoDB emulator

---

## 2. Architecture at a Glance

```
API Gateway ──→ Lambda (StreamLambdaHandler) ──→ Micronaut (Netty)
                                                      │
  ┌───────────────────────────────────────────────────┤
  │  Controller (PrintController)                     │
  │     │                                              │
  │     ├── PrintService (@Context, eager init)        │
  │     │     ├── HtmlHelper.sanitiseHtml() (static)   │
  │     │     └── PrintDocumentRepository (@Context)   │
  │     │           └── DynamoDBMapper                 │
  │     │                 └── AmazonDynamoDB (SDK V1)  │
  │     │                                              │
  │     └── AppConfig (@ConfigurationProperties)       │
  │                                                    │
  │  Error: GlobalExceptionHandler → BadRequestException → 400
  └────────────────────────────────────────────────────┘
```

- **Style:** 3-layer monolith (Controller → Service → Repository)
- **DI:** Micronaut compile-time, constructor injection
- **Environment switching:** `@Requires(env=DEVELOPMENT/TEST)` + `@Replaces` for local vs. production DynamoDB
- **No interfaces** — all dependencies on concrete classes
- **Model = Persistence entity** — `PrintDocumentEntity` carries both domain data and `@DynamoDBTable` annotations

---

## 3. Critical Findings

### 3.1 Security & Stability Blockers

| # | Issue | Severity | Impact |
|---|---|---|---|
| 1 | **Micronaut 1.2.8** — 5+ years old, no security patches, known CVEs | Critical | RCE or data exfiltration risk |
| 2 | **DynamoDB SDK V1** — deprecated, no longer receiving updates | Critical | Will eventually break as AWS evolves |
| 3 | **No authentication** — anyone can POST/GET without credentials | Critical | Abuse vector, data leakage |
| 4 | **Unhandled DynamoDB exceptions** — raw SDK errors leak to clients on 500 | High | Security + UX issue |
| 5 | **400 instead of 404** for missing documents | High | Breaks REST semantics |
| 6 | **No input size validation** — multi-GB payloads can OOM the Lambda | High | DoS vector |
| 7 | **Log4j 2.13.0** — pre-Log4Shell, vulnerable (CVE-2021-44228) | Medium | RCE risk (limited attack surface) |
| 8 | **DynamoDB 1 RCU/1 WCU** — any real traffic will throttle | Medium | Production outage |

### 3.2 Layering Violations

Two upward dependencies found:
- `PrintService.java` (service layer) imports `BadRequestException` from `api.exception` (API layer)
- `HtmlHelper.java` (infrastructure) imports `BadRequestException` from `api.exception` (API layer)

Root cause: `BadRequestException` lives in the wrong package. It should be a shared/domain exception.

### 3.3 Zero Observability

Exactly **2 log statements** exist in the entire production codebase (both in `PrintDocumentRepository` at INFO level). No health endpoint, no metrics, no tracing, no request/response logging. Debugging a production incident with this codebase is effectively impossible.

### 3.4 Test Poverty

Only 5 tests covering happy paths and one error case. No tests for: malformed Base64, XSS payloads, large payloads, DynamoDB failures, concurrent access, or the not-found case with correct HTTP semantics.

---

## 4. Full Scorecard

| Category | Score |
|---|---|
| Layering & Separation of Concerns | 5/10 |
| Test Coverage & Quality | 3/10 |
| Error Handling & Resilience | 2/10 |
| Security | 3/10 |
| Observability (Logging/Monitoring) | 1/10 |
| Dependency Management & Version Currency | 1/10 |
| Deployment & DevOps Maturity | 5/10 |
| API Design (RESTfulness) | 3/10 |
| Configuration Management | 5/10 |
| Documentation | 4/10 |
| **Overall** | **3.2/10** |

---

## 5. What Works Well

Despite the low score, the project has notable strengths:

- **Clean environment switching:** `@Requires`/`@Replaces` for dev-test vs. production DynamoDB configuration is elegant and type-safe.
- **Proper Lambda integration:** `StreamLambdaHandler` correctly bridges API Gateway to Micronaut, with SAM local detection.
- **GraalVM native-image support:** Multi-stage Docker build produces a fast-starting (~10ms) native binary.
- **Consistent constructor injection:** All beans use Micronaut constructor injection — idiomatic and clean.
- **Good deployment configs:** Serverless Framework template, SAM template, and Docker Compose are well-structured.
- **Makefile:** Provides convenient commands for common operations.
- **Small codebase:** ~350 lines means refactoring is manageable.

---

## 6. Quick Wins (Do First)

All under 1 hour each. Combined effort: ~3-4 hours.

| # | Fix | Time | Impact |
|---|---|---|---|
| 1 | Change 400 to 404 for "not found" — create `NotFoundException`, add 404 handler | 30 min | High |
| 2 | Add `@Size(max=1_048_576)` to `PrintRequest.document` | 10 min | High |
| 3 | Switch DynamoDB to on-demand capacity mode in `serverless.yml` | 10 min | High |
| 4 | Add health endpoint `@Get("/health")` | 15 min | Medium |
| 5 | Add request logging in `PrintController` | 20 min | Medium |
| 6 | Add `.env.example` documenting all required environment variables | 30 min | Medium |
| 7 | Remove JCenter from `build.gradle` repositories | 5 min | Low |
| 8 | Specify `StandardCharsets.UTF_8` explicitly in `HtmlHelper` | 5 min | Low |
| 9 | Add explicit `Content-Type: application/json` header in test POST requests | 10 min | Low |
| 10 | Document the table-name-override hack with a comment in `PrintDocumentEntity` | 5 min | Low |

---

## 7. Migration Roadmap

### Phase 1: Quick Wins (Week 1, Days 1-2)
Execute all 10 Quick Wins above. Expected outcome: service is REST-compliant, protected against trivial DoS, and has basic observability.

### Phase 2: Short Term (Week 1-2)
- Add comprehensive exception handling (DynamoDB exceptions, fallback 500 handler)
- Add structured logging with MDC (request IDs, timing)
- Write unit tests for `HtmlHelper` and `PrintService`
- Add integration tests for DynamoDB failure scenarios
- Set up CI pipeline (GitHub Actions: `./gradlew test`)

### Phase 3: Medium Term (Week 3-4)
- Upgrade Micronaut from 1.2.8 to 3.x
- Migrate from AWS SDK V1 to V2 for DynamoDB
- Upgrade Log4j, Jsoup, Lombok, AssertJ to latest versions
- Extract interfaces for `PrintDocumentRepository` and `PrintService`
- Convert `HtmlHelper` from static utility to injectable `@Singleton` bean
- Add API Gateway Lambda authorizer for authentication

### Phase 4: Long Term (Month 2-3)
- Upgrade to Micronaut 4.x (jakarta namespace migration)
- Add comprehensive test coverage (>80% line)
- Implement observability stack: AWS X-Ray, CloudWatch custom metrics, structured JSON logging
- Add OpenAPI spec with Swagger UI
- Implement application-level rate limiting
- Add GraalVM native-image build to CI with build caching

---

## 8. Key Metrics

| Metric | Value |
|---|---|
| Production Java classes | 15 |
| Lines of production code | ~350 |
| Test classes | 2 |
| Lines of test code | ~120 |
| Test assertions | ~12 |
| Micronaut version | 1.2.8 (Dec 2019) |
| Java version | 11 |
| DynamoDB SDK | V1 1.11.693 |
| Vulnerable dependencies | 4+ (Micronaut, Log4j, Jsoup, SDK V1) |
| Log statements in production | 2 |
| Interfaces defined | 0 |
| Circular dependencies | 0 |
| Layering violations | 2 |

---

## 9. Risk Register (Top 10)

| # | Risk | Severity |
|---|---|---|
| R1 | Micronaut 1.2.8 CVEs | Critical |
| R2 | DynamoDB SDK V1 deprecation | Critical |
| R3 | No authentication/access control | Critical |
| R4 | Unhandled DynamoDB exceptions | High |
| R5 | 400 instead of 404 for not found | High |
| R6 | No input size validation | High |
| R7 | Zero interfaces / poor testability | High |
| R8 | Near-zero observability | High |
| R9 | Table-name-override hack | High |
| R10 | No CI/CD pipeline | Medium |

Full risk register with mitigations in Phase 6 report.

---

## 10. Conclusion

Print-Rider is a microservice that does exactly one thing and does it with clean fundamentals — proper DI, framework-idiomatic configuration, and well-structured deployment configs. However, it was built in 2019-2020 and appears to have received no maintenance since. The dependency rot alone is a production blocker.

The good news: the codebase is small enough that a single developer can bring it to production-grade quality in 2-3 weeks of focused work. The architecture is simple and the design decisions are sound for the project's scope. This is not a rewrite situation — it's a targeted modernization effort.

**Priority order for remediation:**
1. Fix dependency vulnerabilities (upgrade or patch everything outdated)
2. Add authentication (at minimum, API Gateway authorizer)
3. Fix exception handling (proper 404, sanitized 500, DynamoDB error handling)
4. Add observability (logging, health endpoint, metrics)
5. Build out test coverage
6. Extract interfaces for testability and future-proofing
