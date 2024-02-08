package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.script.impl.ScriptContext;

import java.util.function.Consumer;

public interface Operation extends Consumer<ScriptContext> {
    default void setup(ScriptContext context){}
}
