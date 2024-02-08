package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.Constants;
import com.vicious.viciouslib.script.function.ScriptFunction;
import com.vicious.viciouslib.script.impl.ScriptContext;

public class Execute implements Operation {

    @Save
    public String functionName;
    protected ScriptFunction function;

    public Execute(){}

    public Execute(String functionName){
        this.functionName=functionName;
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
        Object result = function.apply(params);
        if(result != Constants.VOID) {
            context.push(result);
        }
    }

    @Override
    public String toString() {
        return "Execute " + functionName;
    }
}
