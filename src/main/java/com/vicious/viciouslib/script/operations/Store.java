package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.impl.ScriptContext;
import com.vicious.viciouslib.script.impl.ScriptVariable;

public class Store implements Operation {
    @Save
    public String varName;
    private ScriptVariable variable;

    public Store(){}

    public Store(String varName) {
        this.varName = varName;
    }

    @Override
    public void setup(ScriptContext context) {
        this.variable=context.getVariable(varName);
    }

    @Override
    public void accept(ScriptContext context) {
        variable.setValue(context.getVariable(varName));
    }

    @Override
    public String toString() {
        return "Store -> " + varName;
    }
}
