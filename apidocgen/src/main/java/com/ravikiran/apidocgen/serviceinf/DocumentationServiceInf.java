package com.ravikiran.apidocgen.serviceinf;

import com.ravikiran.apidocgen.dto.DocumentationRequest;
import com.ravikiran.apidocgen.dto.DocumentationResponse;

public interface DocumentationServiceInf {


    DocumentationResponse generateDocumentation(DocumentationRequest documentationRequest);

}
