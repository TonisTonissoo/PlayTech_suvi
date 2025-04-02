package com.playtech.report;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonlOutputGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public void writeToJsonl(List<Object> data, String outputPath) throws IOException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs(); // Veendu, et väljundkaust on olemas
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Object record : data) {
                String json = mapper.writeValueAsString(record);
                writer.write(json);
                writer.newLine(); // Lisa uus rida iga kirje järel
            }
        }
    }
}
