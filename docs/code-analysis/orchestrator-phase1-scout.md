# Phase 1: Scout — Codebase Exploration

**Date:** 2026-06-10
**Agent:** Scout (explore subagent)

---

## Project Name & Purpose
- **Name:** `print-rider`
- **Group:** `dev.deskriders.printrider`
- **Purpose:** A simple API service to save API exchanges (base64-encoded HTML documents) and render them as HTML. Two endpoints: `POST /prints` (save) and `GET /prints/{id}` (retrieve & render).

## Module Structure
- **Single-module project** — no subprojects.

## Source Package Structure
All under `dev.deskriders.printrider`:

| Package / File | Purpose |
|---|---|
| `Application.java` | Micronaut entry point |
| `StreamLambdaHandler.java` | AWS Lambda request-stream handler |
| `api/PrintController.java` | REST controller: POST /prints, GET /prints/{id} |
| `api/GlobalExceptionHandler.java` | Maps BadRequestException → HTTP 400 |
| `api/request/PrintRequest.java` | DTO: document (base64 HTML) |
| `api/exception/BadRequestException.java` | Custom 400 exception |
| `config/AppConfig.java` | @ConfigurationProperties("app") |
| `config/DbConfig.java` | Base class for DynamoDB beans |
| `config/DynamoDbConfig.java` | Production DynamoDB config |
| `config/LocalDynamoDbConfig.java` | Dev/Test DynamoDB (local endpoint) |
| `config/DynamoDbMapperFactory.java` | DynamoDBMapperConfig factory |
| `model/PrintDocumentEntity.java` | DynamoDB entity (DocId hash key, DocumentCode) |
| `repository/PrintDocumentRepository.java` | CRUD against DynamoDB |
| `service/PrintService.java` | Business logic: UUID gen, sanitize, persist |
| `helper/HtmlHelper.java` | Base64 decode + Jsoup sanitize |

## Key Dependencies
| Category | Libraries |
|---|---|
| Framework | Micronaut 1.2.8 (http-server-netty, validation, views-thymeleaf) |
| AWS Lambda | micronaut-function-aws-api-proxy |
| Database | aws-java-sdk-dynamodb 1.11.693 |
| HTML | jsoup 1.12.1 |
| Utils | Lombok, Log4j2 |
| Build | Shadow JAR plugin, GraalVM native-image |
| Test | JUnit 5, EasyRandom, AssertJ |

## Build System
- Gradle 5.6 wrapper, Java 11
- Plugins: shadow (fat JAR), application
- Multi-stage Docker build: Gradle → GraalVM native → Alpine runtime

## Deployment Stack
- **Docker Compose:** app (port 8080) + local DynamoDB (port 8000)
- **AWS Lambda (Serverless Framework):** API Gateway proxy → Lambda, DynamoDB table, custom domain, Route53, ACM
- **Local dev:** AWS SAM template for local API simulation

## Test Structure
- `PrintControllerTest.java` — controller integration test
- `HtmlHelperTest.java` — unit test for HTML sanitization
