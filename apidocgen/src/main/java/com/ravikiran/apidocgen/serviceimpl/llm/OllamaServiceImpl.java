package com.ravikiran.apidocgen.serviceimpl.llm;

import com.ravikiran.apidocgen.serviceinf.llm.LlmServiceInf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class OllamaServiceImpl implements LlmServiceInf {

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate;

    public OllamaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generateDocumentation(String javaCode, String fileName) {
        String prompt = buildPrompt(javaCode, fileName);

        Map<String, Object> requestBody = Map.of(
            "model", model,
            "prompt", prompt,
            "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            ollamaBaseUrl + "/api/generate",
            HttpMethod.POST,
            entity,
            Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        return responseBody != null ? (String) responseBody.get("response") : "No response from LLM";
    }

    private String buildPrompt(String javaCode, String fileName) {
        return """
            You are a technical documentation expert.
            Analyze the following Spring Boot REST controller and generate comprehensive API documentation in Markdown format.
            
            STRICT RULES:
            - Only document what you explicitly see in the code
            - Do not invent endpoints, parameters, or return types
            - Do not assume business logic not visible in the code
            
            File: %s
            
            Code:
            %s
            
            Generate documentation now:
            """.formatted(fileName, javaCode);
    }
}