package com.vicious.viciouslib.persistence.vson;

import com.vicious.viciouslib.persistence.vson.value.VSONMapping;

import java.util.LinkedHashMap;
import java.util.Map;

public class VSONMap extends LinkedHashMap<String, VSONMapping> {
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, VSONMapping> entry : entrySet()) {
            out.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue().get());
        }
        return out.toString();
    }
    public void put(String key, Object o){
        put(key, new VSONMapping(o));
    }
}
