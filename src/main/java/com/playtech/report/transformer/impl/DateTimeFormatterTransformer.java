package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.column.Column.DataType;
import com.playtech.report.transformer.Transformer;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DateTimeFormatterTransformer implements Transformer {
    public static final String NAME = "DateTimeFormatter";

    private final Column input;
    private final String format;
    private final Column output;

    public DateTimeFormatterTransformer(Column input, String format, Column output) {
        this.input = input;
        this.format = format;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        for (Map<String, Object> row : rows) {
            Object value = row.get(input.getName());

            if (value instanceof ZonedDateTime zdt) {
                row.put(output.getName(), zdt.format(formatter));
            } else if (value instanceof LocalDate ld) {
                row.put(output.getName(), ld.format(formatter));
            } else {
                row.put(output.getName(), null); // või logi viga
            }
        }
    }
}
