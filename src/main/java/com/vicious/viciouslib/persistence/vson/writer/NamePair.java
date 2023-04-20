package com.vicious.viciouslib.persistence.vson.writer;

import com.vicious.viciouslib.persistence.vson.value.VSONValue;

public class NamePair {
    private final String name;
    private final VSONValue value;

    public NamePair(String name, VSONValue value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public VSONValue getValue() {
        return value;
    }
}
