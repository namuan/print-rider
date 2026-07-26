# Phase 5: Trace — Critical Path Execution Analysis

**Date:** 2026-06-10
**Agent:** Trace (explore subagent)

---

## Traced Flows

### Flow 1: POST /prints — Successful Creation
API Gateway → Lambda → StreamLambdaHandler → Micronaut → PrintController → PrintService → HtmlHelper (Base64 decode → Jsoup sanitize) → PrintDocumentRepository → DynamoDBMapper.save() → 201 Created + Location header

### Flow 2: GET /prints/{id} — Successful Retrieval
API Gateway → Lambda → Micronaut → PrintController → PrintService → PrintDocumentRepository → DynamoDBMapper.load() → Optional mapping → Thymeleaf View (print.html, th:utext) → 200 OK + text/html

### Flow 3: POST /prints — Invalid Base64 (Error)
Base64.decode() throws IllegalArgumentException → HtmlHelper catches → throws BadRequestException → GlobalExceptionHandler → 400 + JSON `{"error":"Invalid base64 encoded code: ..."}`

### Flow 4: GET /prints/{unknown-id} — Not Found (Error)
DynamoDBMapper.load() returns null → Optional.empty() → orElseThrow(BadRequestException) → GlobalExceptionHandler → **400 Bad Request** (should be 404)

### Flow 5: Lambda Cold Start
Static init → ApplicationContext.build() → config scanning → bean creation (AppConfig → DynamoDBMapperConfig → DynamoDbConfig → PrintDocumentRepository(@Context) → PrintService(@Context) → PrintController → GlobalExceptionHandler) → MicronautLambdaContainerHandler ready. Estimated 3-8s cold start.

### Flow 6: Unhandled DynamoDB Exception
AmazonDynamoDBException propagates from SDK → through Repository/Service/Controller → no handler registered → Micronaut default error handling → **500 Internal Server Error** (potentially leaking SDK error details to user)

## Key Finding: 400 vs 404
PrintService.renderMarkdown() throws `BadRequestException("Print {id} not available")` which is mapped to HTTP 400. REST best practice requires 404 for missing resources. The test asserts 400, so this appears intentional.

## Key Finding: No Error Handler for DynamoDB
If DynamoDB is unreachable, AWS SDK exceptions propagate uncaught to Micronaut's default error handler. This can leak infrastructure details (table names, AWS error codes) to API consumers.

## Bean Initialization Order
1. AppConfig → 2. DynamoDbMapperConfig → 3. DynamoDbConfig/LocalDynamoDbConfig → 4. PrintDocumentRepository (@Context) → 5. PrintService (@Context) → 6. PrintController → 7. GlobalExceptionHandler
