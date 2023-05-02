package com.vicious.viciouslib.persistence.vson.writer;

public class NamePair {
    private final String name;
    private final Object value;

    public NamePair(String name, Object value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
