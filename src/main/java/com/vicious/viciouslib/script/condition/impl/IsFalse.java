package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.condition.type.Condition;

public class IsFalse implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        return !(Boolean)parameters[0];
    }

    @Override
    public String toString() {
        return "== false";
    }
}
