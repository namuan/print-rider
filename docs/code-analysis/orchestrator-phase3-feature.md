# Phase 3: Feature — Capability Inventory

**Date:** 2026-06-10
**Agent:** Feature (explore subagent)

---

## API Surface

| Endpoint | Method | Request | Success | Errors |
|---|---|---|---|---|
| `/prints` | POST | `{"document": "<base64 HTML>"}` | 201 Created + Location header | 400: missing/null document, 400: invalid Base64 |
| `/prints/{id}` | GET | Path param: UUID | 200 OK + text/html (Thymeleaf) | 400: print not found |

## Business Logic Flow

**Save (POST):** Base64 decode → Jsoup sanitize (Whitelist.relaxed + allow `class` on all tags) → UUID generation → DynamoDB save → return UUID.

**Retrieve (GET):** Load from DynamoDB by UUID → if missing, throw BadRequestException → inject sanitized HTML into Thymeleaf template as unescaped HTML.

## Features

1. **Save Print Document** — Accept Base64 HTML, sanitize, persist, return Location URL.
2. **Render Print Document** — Retrieve by ID, render as full HTML page with syntax-highlighting CSS.
3. **HTML Sanitization** — Jsoup relaxed allow-list; strips scripts, event handlers, style/iframe/form tags.
4. **DynamoDB Persistence** — Configurable table name, local/production endpoint switching.
5. **Configurable Domain** — Location header uses configured base URL.
6. **Syntax Highlighting CSS** — Thymeleaf template includes Pygments-compatible CSS classes.

## Security

- **XSS Prevention:** Jsoup allow-list sanitization (strips scripts, event handlers, iframes, style tags, forms, etc.)
- **Input Validation:** `@NotNull` on request body + Base64 decode validation
- **Authentication:** None — API is open

## Test Coverage

- 4 test scenarios: create, missing document, retrieve, unknown ID
- Gaps: malformed Base64, XSS payloads, large payloads, DynamoDB failures, null document

## Error Handling

| Scenario | Exception | HTTP Status |
|---|---|---|
| Missing/null document | Constraint violation (framework) | 400 |
| Invalid Base64 | BadRequestException | 400 |
| Print not found | BadRequestException | 400 |
| DynamoDB unavailable | AmazonDynamoDBException (unhandled) | 500 |
