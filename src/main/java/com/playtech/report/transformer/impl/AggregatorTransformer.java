package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatorTransformer implements Transformer {
    public static final String NAME = "Aggregator";

    private final Column groupByColumn;
    private final List<AggregateBy> aggregateColumns;

    // Konstruktor, mis võtab vastu gruppimise veeru ja agregeerimisveergude nimekirja
    public AggregatorTransformer(Column groupByColumn, List<AggregateBy> aggregateColumns) {
        this.groupByColumn = groupByColumn;
        this.aggregateColumns = aggregateColumns;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        // Määrame gruppide hoidmiseks Map
        Map<Object, Map<String, Object>> groupedData = new HashMap<>();

        // Läbime kõik read
        for (Map<String, Object> row : rows) {
            // Rühmitame andmed `groupByColumn` väärtuse järgi
            Object groupValue = row.get(groupByColumn.getName());
            Map<String, Object> groupData = groupedData.computeIfAbsent(groupValue, k -> new HashMap<>());

            // Läbime kõik `aggregateColumns`, et teha arvutused (SUM/AVG)
            for (AggregateBy aggregateBy : aggregateColumns) {
                String aggregateColumnName = aggregateBy.getOutput().getName();
                Object value = row.get(aggregateBy.getInput().getName());

                // Kontrollime, kas rühmas on juba väärtus olemas, kui pole, siis alustame arvutust
                if (!groupData.containsKey(aggregateColumnName)) {
                    groupData.put(aggregateColumnName, 0.0);
                }

                // Sõltuvalt metodist sooritame summat või keskmist
                double currentValue = (Double) groupData.get(aggregateColumnName);
                if (aggregateBy.getMethod() == Method.SUM) {
                    groupData.put(aggregateColumnName, currentValue + (value instanceof Number ? ((Number) value).doubleValue() : 0));
                } else if (aggregateBy.getMethod() == Method.AVG) {
                    groupData.put(aggregateColumnName, currentValue + (value instanceof Number ? ((Number) value).doubleValue() : 0));
                }
            }
        }

        // Korrigeerime AVG väärtused (jagame kogusummad ridade arvuga)
        for (Map.Entry<Object, Map<String, Object>> entry : groupedData.entrySet()) {
            for (Map.Entry<String, Object> aggEntry : entry.getValue().entrySet()) {
                String aggregateColumnName = aggEntry.getKey();
                double sum = (Double) aggEntry.getValue();
                // Kui meetod oli AVG, jagame summa ridade arvuga
                int count = (int) rows.stream().filter(row -> row.get(groupByColumn.getName()).equals(entry.getKey())).count();
                double avg = sum / count;
                aggEntry.setValue(avg);  // Salvestame keskmise
            }
        }

        // Üksikute ridade väärtused, kuhu lisatakse lõpuks arvutatud tulemused
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

    public static class AggregateBy {
        private Column input;
        private Method method;
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
