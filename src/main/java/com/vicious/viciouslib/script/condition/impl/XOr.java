package com.vicious.viciouslib.script.condition.impl;

import com.vicious.viciouslib.script.ScriptException;
import com.vicious.viciouslib.script.condition.type.Condition;

public class XOr implements Condition {
    @Override
    public boolean isValid(Object... parameters) {
        boolean isTrue = false;
        for (Object parameter : parameters) {
            if(parameter instanceof Condition){
                if(((Condition) parameter).isValid()){
                    if(isTrue){
                        return false;
                    }
                    isTrue=true;
                }
                else{
                    throw new ScriptException("XOR Condition provided non conditional argument.");
                }
            }
        }
        return isTrue;
    }

    @Override
    public String toString() {
        return "XOR";
    }
}
