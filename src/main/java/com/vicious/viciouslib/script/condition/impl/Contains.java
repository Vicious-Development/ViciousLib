package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.ScriptException;
import com.vicious.viciouslib.script.condition.type.Condition;

import java.util.Collection;

public class Contains implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        if(parameters.length < 2){
            throw new ScriptException("CONTAINS condition requires at least two arguments. " + parameters.length + " was supplied.");
        }
        Collection<?> collection = (Collection<?>) parameters[0];
        for (Object parameter : parameters) {
            if(!collection.contains(parameter)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "CONTAINS";
    }
}
