package com.vicious.viciouslib.persistence.vson.value;

public interface IHasDescription {
    default boolean hasDescription(){
        return getDescription() != null && !getDescription().isEmpty();
    }
    String getDescription();
}
