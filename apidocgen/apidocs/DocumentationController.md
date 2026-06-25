# API Documentation for `DocumentationController`

This document provides comprehensive API documentation for the `DocumentationController` in the specified package. The controller is responsible for generating documentation based on Java code and file names.

## Endpoints

- **POST /api/docs/generate**

  - **Description**: This endpoint generates documentation for a given Java code snippet.
  
  - **Request Body**:
    ```json
    {
      "javaCode": "<your-java-code-here>",
      "fileName": "<your-file-name-here>"
    }
    ```
    
    - `javaCode` (string): The Java code to be documented.
    - `fileName` (string): The name of the file associated with the Java code.

  - **Response**:
    ```json
    {
      "fileName": "<generated-file-name>",
      "documentation": "<generated-documentation-text>",
      "success": true/false,
      "errorMessage": "<error-message-if-any>"
    }
    ```

- **Status Codes**
  - **200 OK**: The documentation was generated successfully.
  - **500 Internal Server Error**: An error occurred during the documentation generation process.

## Example Usage

### Request

```bash
POST /api/docs/generate

{
  "javaCode": "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}",
  "fileName": "HelloWorld.java"
}
```

### Response

```json
{
  "fileName": "HelloWorld.md",
  "documentation": "# HelloWorld\nThis is a simple example of a Java class that prints \"Hello, World!\" to the console.",
  "success": true,
  "errorMessage": ""
}
```

---

For any further details or assistance with using this API, please refer to the provided examples and ensure you have the necessary dependencies in place.