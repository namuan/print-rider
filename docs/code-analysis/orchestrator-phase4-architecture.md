# Phase 4: Architecture — Patterns & Design Decisions

**Date:** 2026-06-10
**Agent:** Architecture (explore subagent)

---

## Architectural Style
- **3-layer monolithic** (Controller → Service → Repository)
- No hexagonal/ports-and-adapters — all concrete classes, zero interfaces
- Micronaut compile-time DI with `@Singleton`, `@Context`, `@Factory`, `@Replaces`

## Environment Switching
- `@Requires(env=DEVELOPMENT/TEST)` + `@Replaces` for local DynamoDB config
- `AWS_SAM_LOCAL` env var detected by `StreamLambdaHandler` to activate dev environment
- Clean, type-safe, no external config server required

## Deployment Architecture
**Two paths:**
1. **Serverless (primary):** Lambda + API Gateway via Serverless Framework, custom domain `printrider.bettercallbots.com`
2. **Docker:** Multi-stage GraalVM native-image → Alpine binary, Docker Compose with local DynamoDB

**Bridge:** `StreamLambdaHandler` uses `MicronautLambdaContainerHandler` from `micronaut-function-aws-api-proxy`

## Critical Weaknesses
| # | Issue | Severity |
|---|---|---|
| 1 | Micronaut 1.2.8 is severely outdated (no security patches) | Critical |
| 2 | DynamoDB SDK V1 is deprecated/legacy | Critical |
| 3 | Domain entity = Persistence entity (PrintDocumentEntity has DynamoDB annotations) | Critical |
| 4 | Zero interfaces — no ports, no mockability | High |
| 5 | Table-name-override hack (`"SEE_DYNAMO_DB_MAPPER_FACTORY"`) | High |
| 6 | Only one exception type; 400 used instead of 404 for "not found" | High |
| 7 | Only 2 log statements in entire codebase | Medium |
| 8 | @Context on service/repository increases Lambda cold-start | Medium |
| 9 | Static HtmlHelper — not mockable, hard-coded config | Medium |
| 10 | No table-creation automation for local dev | Medium |

## Migration Priority
1. Upgrade Micronaut 1.2.8 → 4.x
2. Migrate DynamoDB SDK V1 → V2
3. Introduce interfaces for repository and service
4. Add 404/500 exception handling
5. Convert HtmlHelper to injectable bean
6. Remove @Context, improve logging
