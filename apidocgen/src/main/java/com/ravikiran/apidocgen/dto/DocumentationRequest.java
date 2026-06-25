package com.ravikiran.apidocgen.dto;

import lombok.Data;

@Data
public class DocumentationRequest {
    private String javaCode;
    private String fileName;
}