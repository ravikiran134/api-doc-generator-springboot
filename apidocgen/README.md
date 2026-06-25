# API Doc Generator — Spring Boot

An AI-powered API documentation generator built as a production-grade Spring Boot microservice. Accepts Java Spring Boot controller code via REST API, sends it to a local LLM (Ollama), and generates comprehensive Markdown documentation saved to a local output folder.

> **Version 1 (Python script):** [api-doc-generator](https://github.com/ravikiran134/api-doc-generator)  
> **Version 2 (This repo):** Production-grade Spring Boot microservice rebuild

---

## Architecture

```
POST /api/docs/generate (JSON: javaCode + fileName)
        │
        ▼
DocumentationController
        │
        ▼
DocumentationServiceInf (interface)
        │
        ├──► LlmServiceInf ──► OllamaServiceImpl ──► Ollama HTTP API (port 11434)
        │                                                      │
        │                                              Qwen2.5-Coder:3b
        │                                              (local LLM, CPU)
        │
        └──► FileGenerationServiceInf ──► FileGenerationServiceImpl ──► ./apidocs/*.md
```

### Design Decisions

| Decision | Rationale |
|---|---|
| Constructor injection over `@Autowired` | Enforces immutability, improves testability, Spring best practice since 4.x |
| `LlmServiceInf` interface | LLM-agnostic — swap Ollama → Claude API → OpenAI by adding a new implementation |
| Configurable output directory | Externalized via `application.properties`, no code changes needed for deployment |
| Spring Boot 3.3.5 | Production stable — Spring Boot 4.x restructures packages and is not yet production standard |
| Local Ollama over cloud API | Free, private, zero latency cost during development |

---

## Tech Stack

- Java 18
- Spring Boot 3.3.5
- Maven
- Lombok 1.18.30
- RestTemplate (Ollama HTTP integration)
- Ollama (local LLM runtime)
- Qwen2.5-Coder:3b (6–8 tokens/sec on CPU)

---

## Project Structure

```
src/main/java/com/ravikiran/apidocgen/
├── controller/
│   └── DocumentationController.java       # REST endpoint
├── serviceinf/
│   ├── DocumentationServiceInf.java        # Documentation service interface
│   ├── FileGenerationServiceInf.java       # File generation interface
│   └── llm/
│       └── LlmServiceInf.java             # LLM service interface
├── serviceimpl/
│   ├── DocumentationServiceImpl.java       # Orchestrates LLM + file generation
│   ├── FileGenerationServiceImpl.java      # Saves .md files to output folder
│   └── llm/
│       └── OllamaServiceImpl.java         # Ollama REST integration
├── dto/
│   ├── DocumentationRequest.java          # javaCode + fileName
│   └── DocumentationResponse.java         # documentation + success + errorMessage
└── config/
    └── LlmConfig.java                     # RestTemplate bean
```

---

## Prerequisites

- Java 17+
- Maven 3.9+
- [Ollama](https://ollama.ai) installed and running
- Qwen2.5-Coder model pulled

```bash
ollama pull qwen2.5-coder:3b
```

---

## Setup & Run

**1. Clone the repository**
```bash
git clone https://github.com/ravikiran134/api-doc-generator-springboot.git
cd api-doc-generator-springboot
```

**2. Start Ollama**
```bash
ollama run qwen2.5-coder:3b
```

**3. Run the Spring Boot application**
```bash
mvn spring-boot:run
```

App starts on `http://localhost:8081`

---

## Usage

### Generate API Documentation

**Endpoint:** `POST /api/docs/generate`

**Request Body:**
```json
{
    "javaCode": "@RestController @RequestMapping('/api/users') public class UserController { @GetMapping('/{id}') public ResponseEntity<User> getUserById(@PathVariable Long id) { return ResponseEntity.ok(userService.findById(id)); } }",
    "fileName": "UserController.java"
}
```

**Response:**
```json
{
    "fileName": "UserController.java",
    "documentation": "# UserController API Documentation\n\n...",
    "success": true,
    "errorMessage": null
}
```

Generated file is saved to `./apidocs/UserController.md`

---

## Configuration

`src/main/resources/application.properties`

```properties
server.port=8081
ollama.base-url=http://localhost:11434
ollama.model=qwen2.5-coder:3b
apidocs.output-dir=./apidocs
```

To use a different Ollama model, update `ollama.model`. To change output folder, update `apidocs.output-dir`.

---

## Prompt Engineering

The LLM prompt includes anti-hallucination guardrails:

```
STRICT RULES:
- Only document what you explicitly see in the code
- Do not invent endpoints, parameters, or return types
- Do not assume business logic not visible in the code
```

---

## Known Limitations

- **Input is raw string only** — Java code must be pasted as a string in the request body. File upload (`.java` file directly) is not yet supported.
- **Synchronous processing** — caller waits for LLM to complete (1–2 minutes on CPU). Async job pattern planned.
- **Local storage only** — generated docs saved to local disk. Cloud storage (S3) not yet implemented.

---

## Roadmap

- [ ] File upload endpoint — accept `.java` file directly instead of raw string
- [ ] `JavaFileParser` — parse and extract controller metadata from uploaded files
- [ ] Async job processing — `POST /generate` returns jobId, `GET /status/{jobId}` polls result
- [ ] Docker containerization
- [ ] Cloud storage (AWS S3) for generated docs
- [ ] Swagger/OpenAPI documentation for this service itself
- [ ] Claude API / OpenAI as alternative LLM backends

---

## Microservice Upgrade Path

This service is designed to be microservice-ready:

```
Current:  Standalone Spring Boot app
Next:     Docker container
Then:     Eureka service registration
Then:     API Gateway routing
Then:     RabbitMQ async job processing
Then:     S3 doc storage + JWT auth
```

---

## Related Projects

- [api-doc-generator](https://github.com/ravikiran134/api-doc-generator) — Python version (v1)
