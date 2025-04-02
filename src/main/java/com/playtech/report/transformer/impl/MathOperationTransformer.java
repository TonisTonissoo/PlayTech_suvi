package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;

public class MathOperationTransformer implements Transformer {
    public static final String NAME = "MathOperation";

    private final List<Column> inputs;
    private final MathOperation operation;
    private final Column output;


    public MathOperationTransformer(List<Column> inputs, MathOperation operation, Column output) {
        this.inputs = inputs;
        this.operation = operation;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {

            Object value1 = row.get(inputs.get(0).getName());
            Object value2 = row.get(inputs.get(1).getName());

            if (value1 instanceof Number && value2 instanceof Number) {

                Number result = performOperation((Number) value1, (Number) value2);

                row.put(output.getName(), result);
            } else {
                row.put(output.getName(), null);
            }
        }
    }


    private Number performOperation(Number value1, Number value2) {
        switch (operation) {
            case ADD:
                return value1.doubleValue() + value2.doubleValue();
            case SUBTRACT:
                return value1.doubleValue() - value2.doubleValue();
            default:
                return null;
        }
    }


    public enum MathOperation {
        ADD,
        SUBTRACT,
    }
}
