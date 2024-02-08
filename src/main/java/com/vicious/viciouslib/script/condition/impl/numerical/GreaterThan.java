package com.vicious.viciouslib.script.condition.impl.numerical;

import com.vicious.viciouslib.script.condition.type.Condition;

public class GreaterThan implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        return ((Number)parameters[0]).doubleValue() > ((Number)parameters[1]).doubleValue();
    }

    @Override
    public String toString() {
        return ">";
    }
}
