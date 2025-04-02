package com.playtech.report;

import java.util.HashMap;
import java.util.Map;

public class TypedRecord {
    private final Map<String, Object> values = new HashMap<>();

    public void put(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Map<String, Object> getAll() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
