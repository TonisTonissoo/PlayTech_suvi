package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;

public class StringFormatterTransformer implements Transformer {
    public static final String NAME = "StringFormatter";

    private final List<Column> inputs;
    private final String format;
    private final Column output;

    public StringFormatterTransformer(List<Column> inputs, String format, Column output) {
        this.inputs = inputs;
        this.format = format;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            // Koostame väärtuse õigeks vorminguks
            StringBuilder sb = new StringBuilder();
            for (Column input : inputs) {
                Object value = row.get(input.getName());
                if (value != null) {
                    sb.append(String.format(format, value));
                }
            }
            row.put(output.getName(), sb.toString());
        }
    }
}
