package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatorTransformer implements Transformer {
    public static final String NAME = "Aggregator";

    private final Column groupByColumn;
    private final List<AggregateBy> aggregateColumns;

    public AggregatorTransformer(Column groupByColumn, List<AggregateBy> aggregateColumns) {
        this.groupByColumn = groupByColumn;
        this.aggregateColumns = aggregateColumns;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        Map<Object, Map<String, Object>> groupedData = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Object groupValue = row.get(groupByColumn.getName());
            Map<String, Object> groupData = groupedData.computeIfAbsent(groupValue, k -> new HashMap<>());

            for (AggregateBy aggregateBy : aggregateColumns) {
                String aggregateColumnName = aggregateBy.getOutput().getName();
                Object value = row.get(aggregateBy.getInput().getName());

                if (!groupData.containsKey(aggregateColumnName)) {
                    groupData.put(aggregateColumnName, 0.0);
                }

                double currentValue = (Double) groupData.get(aggregateColumnName);
                if (aggregateBy.getMethod() == Method.SUM) {
                    groupData.put(aggregateColumnName, currentValue + (value instanceof Number ? ((Number) value).doubleValue() : 0));
                } else if (aggregateBy.getMethod() == Method.AVG) {
                    groupData.put(aggregateColumnName, currentValue + (value instanceof Number ? ((Number) value).doubleValue() : 0));
                }
            }
        }

        for (Map.Entry<Object, Map<String, Object>> entry : groupedData.entrySet()) {
            for (Map.Entry<String, Object> aggEntry : entry.getValue().entrySet()) {
                String aggregateColumnName = aggEntry.getKey();
                double sum = (Double) aggEntry.getValue();
                int count = (int) rows.stream().filter(row -> row.get(groupByColumn.getName()).equals(entry.getKey())).count();
                double avg = sum / count;
                aggEntry.setValue(avg);
            }
        }

        for (Map<String, Object> row : rows) {
            Object groupValue = row.get(groupByColumn.getName());
            if (groupedData.containsKey(groupValue)) {
                Map<String, Object> groupData = groupedData.get(groupValue);
                for (AggregateBy aggregateBy : aggregateColumns) {
                    row.put(aggregateBy.getOutput().getName(), groupData.get(aggregateBy.getOutput().getName()));
                }
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AggregateBy {
        @XmlIDREF
        private Column input;

        private Method method;

        @XmlIDREF
        private Column output;

        public Column getInput() {
            return input;
        }

        public Column getOutput() {
            return output;
        }

        public Method getMethod() {
            return method;
        }
    }

    public enum Method {
        SUM,
        AVG
    }
}
