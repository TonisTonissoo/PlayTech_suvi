package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class OrderingTransformer implements Transformer {
    public static final String NAME = "Ordering";

    private final Column input;
    private final Order order;

    // Konstruktor, mis võtab vastu sisendi ja järjestuskorra (ASC või DESC)
    public OrderingTransformer(Column input, Order order) {
        this.input = input;
        this.order = order;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        // Kasutame Comparatorit, et järjestada andmed vastavalt veeru väärtusele
        Comparator<Map<String, Object>> comparator = (map1, map2) -> {
            Comparable value1 = (Comparable) map1.get(input.getName());
            Comparable value2 = (Comparable) map2.get(input.getName());
            return order == Order.ASC ? value1.compareTo(value2) : value2.compareTo(value1);
        };

        // Järjestame andmed
        rows.sort(comparator);
    }

    // Enum, mis määrab järjestuskorra
    public enum Order {
        ASC,  // Tõusev järjestus
        DESC  // Laskuv järjestus
    }
}
