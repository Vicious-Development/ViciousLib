package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.ScriptException;
import com.vicious.viciouslib.script.condition.type.Condition;

public class Or implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        for (Object parameter : parameters) {
            if(parameter instanceof Condition){
                if(((Condition) parameter).isValid()){
                    return true;
                }
                else{
                    throw new ScriptException("OR Condition provided non conditional argument.");
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "||";
    }
}
