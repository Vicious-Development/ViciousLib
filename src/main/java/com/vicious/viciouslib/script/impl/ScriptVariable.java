package com.vicious.viciouslib.script.impl;

public class ScriptVariable {
    private Object value;
    public ScriptVariable(){

    }

    public ScriptVariable(Object defaultValue){
        value=defaultValue;
    }

    public void setValue(Object value){
        this.value=value;
    }

    public Object getValue() {
        return value;
    }
}
