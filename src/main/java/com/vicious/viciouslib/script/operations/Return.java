package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.script.impl.ScriptContext;

public class Return implements Operation{
    @Override
    public void accept(ScriptContext context) {
        context.exit();
    }

    @Override
    public String toString() {
        return "RETURN";
    }
}
