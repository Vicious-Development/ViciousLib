package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.function.ScriptFunction;
import com.vicious.viciouslib.script.impl.ScriptContext;

public class ExecReject implements Operation {

    @Save
    public String functionName;
    protected ScriptFunction function;

    public ExecReject(){}

    public ExecReject(String functionName){
        this.functionName=functionName;
    }

    public ExecReject(ScriptFunction function){
        this.function = function;
    }

    public void setup(ScriptContext context){
        function = context.getFunction(functionName);
    }

    @Override
    public void accept(ScriptContext context) {
        Object[] params = new Object[function.length()];
        for (int i = params.length - 1; i >= 0; i--) {
            params[i] = context.pop();
        }
        function.apply(params);
    }

    @Override
    public String toString() {
        return "ExecReject " + functionName;
    }
}
