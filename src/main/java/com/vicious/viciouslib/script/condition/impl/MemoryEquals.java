package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.ScriptException;
import com.vicious.viciouslib.script.condition.type.Condition;

public class MemoryEquals implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        if(parameters.length < 2){
            throw new ScriptException("EQUALS condition requires at least two arguments. " + parameters.length + " was supplied.");
        }
        Object prev = parameters[0];
        for (int i = 1; i < parameters.length; i++) {
            if(prev != parameters[i]){
                return false;
            }
            else{
                prev=parameters[i];
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "==";
    }
}
