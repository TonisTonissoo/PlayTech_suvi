package com.playtech.report;

import com.playtech.report.column.Column;
import com.playtech.report.column.Column.DataType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public List<TypedRecord> readCsv(String filePath, List<Column> inputColumns) throws IOException {
        List<TypedRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("CSV file is empty");

            String[] headers = headerLine.split(",", -1);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",", -1);
                if (values.length != headers.length) continue; // Skip faulty lines

                TypedRecord record = new TypedRecord();
                for (int i = 0; i < headers.length; i++) {
                    String columnName = headers[i];
                    String rawValue = values[i];

                    // Leia vastav Column
                    Column column = inputColumns.stream()
                            .filter(col -> col.getName().equals(columnName))
                            .findFirst()
                            .orElse(null);
                    if (column == null) continue;

                    Object typedValue = parseValue(rawValue, column.getType());
                    record.put(columnName, typedValue);
                }
                records.add(record);
            }
        }

        return records;
    }

    private Object parseValue(String raw, DataType type) {
        try {
            return switch (type) {
                case STRING -> raw;
                case INTEGER -> raw.isBlank() ? null : Integer.parseInt(raw);
                case DOUBLE -> raw.isBlank() ? null : Double.parseDouble(raw);
                case DATE -> raw.isBlank() ? null : LocalDate.parse(raw);
                case DATETIME -> raw.isBlank() ? null : ZonedDateTime.parse(raw);
            };
        } catch (Exception e) {
            return null; // Võib logida, kui vaja
        }
    }
}
