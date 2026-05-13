package com.fitness.fitnessapp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportFileResponse {
    private String fileName;

    private String contentType;

    private byte[] content;

    public static String getExtension(String format) {
        return "pdf".equalsIgnoreCase(format) ? ".pdf" : ".txt";
    }
}
