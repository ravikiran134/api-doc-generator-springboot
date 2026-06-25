package com.ravikiran.apidocgen.serviceimpl;

import org.springframework.stereotype.Service;

import com.ravikiran.apidocgen.dto.DocumentationRequest;
import com.ravikiran.apidocgen.dto.DocumentationResponse;
import com.ravikiran.apidocgen.serviceinf.DocumentationServiceInf;
import com.ravikiran.apidocgen.serviceinf.FileGenerationServiceInf;
import com.ravikiran.apidocgen.serviceinf.llm.LlmServiceInf;

@Service
public class DocumentationServiceImpl implements DocumentationServiceInf {

    private final LlmServiceInf llmService;
    private final FileGenerationServiceInf fileGenerationService;

    public DocumentationServiceImpl(LlmServiceInf llmService,
                                    FileGenerationServiceInf fileGenerationService) {
        this.llmService = llmService;
        this.fileGenerationService = fileGenerationService;
    }

    @Override
    public DocumentationResponse generateDocumentation(DocumentationRequest documentationRequest) {
        DocumentationResponse response = new DocumentationResponse();
        response.setFileName(documentationRequest.getFileName());

        try {
            String documentation = llmService.generateDocumentation(
                documentationRequest.getJavaCode(),
                documentationRequest.getFileName()
            );
            fileGenerationService.saveDocumentation(
                documentationRequest.getFileName(),
                documentation
            );
            response.setDocumentation(documentation);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }
}