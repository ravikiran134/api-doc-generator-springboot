package com.ravikiran.apidocgen.serviceimpl;

import com.ravikiran.apidocgen.serviceinf.FileGenerationServiceInf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileGenerationServiceImpl implements FileGenerationServiceInf {

    @Value("${apidocs.output-dir:./apidocs}")
    private String outputDir;

    @Override
    public String saveDocumentation(String fileName, String content) {
        try {
            Path dirPath = Paths.get(outputDir);
            System.out.println("Saving to: " + dirPath.toAbsolutePath());

            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String mdFileName = fileName.replace(".java", ".md");
            Path filePath = dirPath.resolve(mdFileName);
            Files.writeString(filePath, content);
            System.out.println("File saved: " + filePath.toAbsolutePath());

            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save documentation: " + e.getMessage());
        }
    }
}