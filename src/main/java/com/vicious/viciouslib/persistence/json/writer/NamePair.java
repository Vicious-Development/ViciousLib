package com.vicious.viciouslib.persistence.json.writer;

import com.vicious.viciouslib.persistence.json.value.JSONValue;

public class NamePair {
    private final String name;
    private final JSONValue value;

    public NamePair(String name, JSONValue value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public JSONValue getValue() {
        return value;
    }
}
