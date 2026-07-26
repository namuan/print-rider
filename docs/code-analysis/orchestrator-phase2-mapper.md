# Phase 2: Mapper — Dependency Analysis

**Date:** 2026-06-10
**Agent:** Mapper (explore subagent)

---

## Key Findings

### Layering Violations (2 found)
1. **Service → API Exception:** `PrintService.java` imports `api.exception.BadRequestException` — service layer depends on API layer construct.
2. **Helper → API Exception:** `HtmlHelper.java` imports `api.exception.BadRequestException` — infrastructure layer depends on API layer construct.

Both are upward dependencies (lower layer depends on higher layer). Fix: move `BadRequestException` to a neutral package (e.g., `dev.deskriders.printrider.exception`).

### No Circular Dependencies
The dependency graph is a DAG — no cycles found.

### No Interfaces for Decoupling
All internal dependencies are on concrete classes. No interfaces or abstractions between layers. This limits testability and makes the code tightly coupled to Micronaut's DI.

### Model Lacks Persistence Ignorance
`PrintDocumentEntity` doubles as both domain model and DynamoDB entity (annotated with `@DynamoDBTable`, `@DynamoDBHashKey`, `@DynamoDBAttribute`).

### Consistent Constructor Injection
All Micronaut beans use constructor injection — good practice.

### High Framework Coupling
Heavy reliance on Micronaut (1.2.8) and AWS SDK V1 (1.11.693) annotations across all layers.

### Dependency Flow
```
EntryPoints → Controllers → Services → Repositories → Model
                                    ↘
                               Infrastructure/Config
```

See full report for complete external dependency list, internal import map, and layered architecture diagram.
