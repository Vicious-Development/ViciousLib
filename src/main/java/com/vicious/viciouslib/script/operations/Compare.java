package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.condition.type.Condition;
import com.vicious.viciouslib.script.impl.ScriptContext;

public class Compare implements Operation {
    @Save
    public Condition condition;

    public Compare(){}

    public Compare(Condition condition) {
        this.condition = condition;
    }

    @Override
    public void accept(ScriptContext context) {
        Object[] params = new Object[context.stackSize()];
        for (int i = params.length - 1; i >= 0; i--) {
            params[i] = context.pop();
        }
        context.push(condition.isValid(params));
    }

    @Override
    public String toString() {
        return "Compare[" + condition +"]";
    }
}
