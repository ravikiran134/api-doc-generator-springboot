package com.ravikiran.apidocgen.dto;
import lombok.Data;

@Data
public class DocumentationResponse {

    private String fileName;
    private String documentation;
    private boolean success;
    private String errorMessage;

}
