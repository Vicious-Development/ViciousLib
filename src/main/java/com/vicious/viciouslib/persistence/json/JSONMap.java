package com.vicious.viciouslib.persistence.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONMap extends LinkedHashMap<String, JSONMapping> {
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, JSONMapping> entry : entrySet()) {
            out.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue());
        }
        return out.toString();
    }
    public void put(String key, Object o){
        put(key, new JSONMapping(o));
    }
}
