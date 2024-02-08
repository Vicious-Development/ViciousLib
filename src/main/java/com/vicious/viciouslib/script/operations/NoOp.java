package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.script.impl.ScriptContext;

public class NoOp implements Operation{
    @Override
    public void accept(ScriptContext context) {

    }

    @Override
    public String toString() {
        return "NOOP";
    }
}
