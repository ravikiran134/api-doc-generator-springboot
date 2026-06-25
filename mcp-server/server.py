from mcp.server.fastmcp import FastMCP
import requests
import os

mcp = FastMCP("api-doc-generator")

SPRING_BOOT_URL = "http://localhost:8081/api/docs/generate"
PROJECT_ROOT = "F:\\api-doc-generator-springboot\\apidocgen\\src\\main\\java\\com\\ravikiran\\apidocgen"
DTO_PATH = os.path.join(PROJECT_ROOT, "dto")

def load_dtos() -> str:
    """Read all DTO files and return their contents combined."""
    dto_code = ""
    if os.path.exists(DTO_PATH):
        for file in os.listdir(DTO_PATH):
            if file.endswith(".java"):
                with open(os.path.join(DTO_PATH, file), "r") as f:
                    dto_code += f"\n// {file}\n" + f.read()
    return dto_code

@mcp.tool()
def generate_api_docs(java_code: str, file_name: str) -> str:
    """Generate Markdown API documentation for a Java Spring Boot controller."""
    try:
        # Normalize filename — strip any extension, Spring Boot adds .md
        file_name = file_name.replace(".java", "").replace(".md", "") + ".java"

        # Include DTOs so Ollama sees actual field names
        dto_code = load_dtos()
        full_code = java_code
        if dto_code:
            full_code += "\n\n// --- DTOs ---\n" + dto_code

        response = requests.post(SPRING_BOOT_URL, json={
            "javaCode": full_code,
            "fileName": file_name
        }, timeout=180)

        data = response.json()
        if data.get("success"):
            return f"Documentation generated and saved for {file_name}."
        else:
            return f"Failed: {data.get('errorMessage')}"

    except requests.exceptions.Timeout:
        return "Request timed out — Ollama is still processing. Try again or check the apidocs folder manually."
    except Exception as e:
        return f"Error: {str(e)}"

if __name__ == "__main__":
    mcp.run()