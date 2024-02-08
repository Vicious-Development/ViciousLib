package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.ScriptException;
import com.vicious.viciouslib.script.condition.type.Condition;

public class Not implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        for (Object parameter : parameters) {
            if(parameter instanceof Condition){
                if(((Condition) parameter).isValid()){
                    return false;
                }
                else{
                    throw new ScriptException("NOT Condition provided non conditional argument.");
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "!";
    }
}
