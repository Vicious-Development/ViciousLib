package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.impl.ScriptContext;
import com.vicious.viciouslib.script.impl.ScriptVariable;

public class ExecStore extends Execute{
    @Save
    public String varName;
    private ScriptVariable variable;

    public ExecStore(){
    }
    public ExecStore(String varName, String functionName) {
        super(functionName);
        this.varName = varName;
    }

    @Override
    public void setup(ScriptContext context) {
        super.setup(context);
        this.variable=context.getVariable(varName);
    }

    @Override
    public void accept(ScriptContext context) {
        Object[] params = new Object[function.length()];
        for (int i = params.length - 1; i >= 0; i--) {
            params[i] = context.pop();
        }
        variable.setValue(function.apply(params));
    }

    @Override
    public String toString() {
        return "ExecStore " + functionName + "@" + varName;
    }
}
