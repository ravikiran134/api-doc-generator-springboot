# API Doc Generator — Spring Boot + MCP

Generate Markdown API documentation for Java Spring Boot controllers using a local LLM, triggered with a single natural-language command from Claude Desktop or Claude Code.

## What It Does

You describe a controller and Claude handles the rest:

1. The **MCP server** receives the request from Claude and forwards the controller code (plus any DTOs) to the Spring Boot service
2. The **apidocgen** Spring Boot service sends the code to a local **Ollama** LLM (Qwen2.5-Coder)
3. The LLM generates structured Markdown documentation
4. The file is written automatically to `apidocgen/apidocs/<ControllerName>.md`

No cloud API keys. No manual curl commands. One sentence to Claude and the doc is on disk.

---

## Monorepo Structure

```
api-doc-generator-springboot/
├── apidocgen/              # Spring Boot service — LLM orchestration + file generation
│   ├── src/
│   │   └── main/java/com/ravikiran/apidocgen/
│   │       ├── controller/     # REST endpoint (POST /api/docs/generate)
│   │       ├── serviceinf/     # Service + LLM interfaces
│   │       ├── serviceimpl/    # Ollama integration, file writer
│   │       ├── dto/            # DocumentationRequest / DocumentationResponse
│   │       └── config/         # RestTemplate bean
│   ├── apidocs/            # Generated documentation output (.md files)
│   ├── pom.xml
│   └── README.md           # apidocgen service documentation
└── mcp-server/
    └── server.py           # MCP server — bridges Claude ↔ Spring Boot
```

---

## Prerequisites

- Java 17+, Maven 3.9+
- Python 3.10+
- [Ollama](https://ollama.ai) installed and on your `PATH`

---

## Setup

### 1. Pull the LLM model

```bash
ollama pull qwen2.5-coder:3b
```

### 2. Configure the MCP server

Open `mcp-server/server.py` and update `PROJECT_ROOT` to match your local checkout:

```python
PROJECT_ROOT = "C:\\path\\to\\api-doc-generator-springboot\\apidocgen\\src\\main\\java\\com\\ravikiran\\apidocgen"
```

This path is used to auto-load DTOs so the LLM sees actual request/response field names.

### 3. Install MCP server dependencies

```bash
pip install "mcp[cli]" requests
```

---

## Running Both Services

### Terminal 1 — Start Ollama

```bash
ollama run qwen2.5-coder:3b
```

### Terminal 2 — Start the Spring Boot service

```bash
cd apidocgen
mvn spring-boot:run
```

The service starts on `http://localhost:8081`.

The MCP server (`mcp-server/server.py`) does **not** need to be started manually — Claude Desktop and Claude Code launch it automatically over `stdio`.

---

## Connecting Claude to the MCP Server

### Claude Desktop

Add the following to `%APPDATA%\Claude\claude_desktop_config.json` (create it if it does not exist):

```json
{
  "mcpServers": {
    "api-doc-generator": {
      "command": "python",
      "args": ["C:\\path\\to\\api-doc-generator-springboot\\mcp-server\\server.py"]
    }
  }
}
```

Restart Claude Desktop. The `generate_api_docs` tool appears automatically in every conversation.

### Claude Code

Add the following to `~/.claude/settings.json`:

```json
{
  "mcpServers": {
    "api-doc-generator": {
      "command": "python",
      "args": ["C:\\path\\to\\api-doc-generator-springboot\\mcp-server\\server.py"]
    }
  }
}
```

The tool is available in the next Claude Code session. You can verify it loaded with `/mcp` in the Claude Code prompt.

---

## Generating Documentation

With Ollama and the Spring Boot service running, tell Claude:

> Generate API documentation for UserController.java and save it to the apidocs folder

Claude calls the `generate_api_docs` tool, the MCP server forwards the code to Spring Boot, and the finished Markdown is written to `apidocgen/apidocs/UserController.md`.

### Tool parameters

| Parameter   | Description                                                      |
|-------------|------------------------------------------------------------------|
| `java_code` | Full source of the Spring Boot controller                        |
| `file_name` | Controller class name, with or without extension (`UserController` or `UserController.java`) |

The MCP server automatically appends DTOs from the project so the LLM sees complete request/response field names without you having to include them manually.

---

## End-to-End Flow

```
Claude (Desktop or Code)
        │  natural-language prompt
        ▼
  mcp-server/server.py          ← stdio transport, launched by Claude
        │  POST /api/docs/generate
        ▼
  apidocgen (Spring Boot)        ← localhost:8081
        │
        ├──► Ollama                ← localhost:11434  (Qwen2.5-Coder:3b)
        │
        └──► apidocgen/apidocs/<Controller>.md
```

---

## Configuration Reference

| Setting             | File                                              | Default                                    |
|---------------------|---------------------------------------------------|--------------------------------------------|
| Spring Boot port    | `apidocgen/src/main/resources/application.properties` | `8081`                                 |
| Ollama base URL     | `apidocgen/src/main/resources/application.properties` | `http://localhost:11434`               |
| Ollama model        | `apidocgen/src/main/resources/application.properties` | `qwen2.5-coder:3b`                     |
| Docs output folder  | `apidocgen/src/main/resources/application.properties` | `./apidocs`                            |
| Spring Boot URL     | `mcp-server/server.py`                            | `http://localhost:8081/api/docs/generate`  |
| DTO source path     | `mcp-server/server.py`                            | `PROJECT_ROOT` (update after cloning)      |

---

For full apidocgen service documentation — architecture, design decisions, and roadmap — see [apidocgen/README.md](apidocgen/README.md).
