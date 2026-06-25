package com.ravikiran.apidocgen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ravikiran.apidocgen.dto.DocumentationRequest;
import com.ravikiran.apidocgen.dto.DocumentationResponse;
import com.ravikiran.apidocgen.serviceinf.DocumentationServiceInf;

@RestController
@RequestMapping("/api/docs")
public class DocumentationController {

    private final DocumentationServiceInf documentationService;

    public DocumentationController(DocumentationServiceInf documentationService) {
        this.documentationService = documentationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<DocumentationResponse> generateDocumentation(
            @RequestBody DocumentationRequest request) {
        DocumentationResponse response = documentationService.generateDocumentation(request);
        return ResponseEntity.ok(response);
    }
}