package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.script.impl.ScriptContext;

import java.util.function.Supplier;

public class Push implements Operation{
    private final Supplier<?> value;

    public Push(Object value) {
        this.value = ()->value;
    }
    public Push(Supplier<?> value){
        this.value=value;
    }

    @Override
    public void accept(ScriptContext context) {
        context.push(value.get());
    }

    @Override
    public String toString() {
        return "Push [computedValue]";
    }
}
