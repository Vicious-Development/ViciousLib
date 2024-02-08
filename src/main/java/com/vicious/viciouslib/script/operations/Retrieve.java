package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.impl.ScriptContext;
import com.vicious.viciouslib.script.impl.ScriptVariable;

public class Retrieve implements Operation {
    @Save
    public String varName;
    private ScriptVariable variable;

    public Retrieve(){}

    public Retrieve(String varName) {
        this.varName = varName;
    }

    @Override
    public void setup(ScriptContext context) {
        this.variable=context.getVariable(varName);
    }

    @Override
    public void accept(ScriptContext context) {
        context.push(variable.getValue());
    }

    @Override
    public String toString() {
        return "Retrieve: " + varName;
    }
}
