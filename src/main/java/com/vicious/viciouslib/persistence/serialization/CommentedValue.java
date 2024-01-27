package com.vicious.viciouslib.persistence.serialization;

import com.vicious.viciouslib.persistence.vson.value.IHasDescription;

public class CommentedValue implements IHasDescription {
    private final Object value;
    private final String description;

    public CommentedValue(Object value, String description){
        this.value = value;
        this.description = description;
    }
    @Override
    public String getDescription() {
        return description;
    }

    public Object getValue() {
        return value;
    }
}
