package com.vicious.viciouslib.script.function;

import com.vicious.viciouslib.script.Constants;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ScriptFunction {
    Object apply(Object[] arguments);
    int length();

    static ScriptFunction of(Function<Object[],Object> func, int argCount){
        return new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                return func.apply(arguments);
            }

            @Override
            public int length() {
                return argCount;
            }
        };
    }

    static ScriptFunction noArgs(Supplier<Object> supplier){
        return new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                return supplier.get();
            }

            @Override
            public int length() {
                return 0;
            }
        };
    }

    static ScriptFunction ofVoid(Consumer<Object[]> func, int argCount){
        return new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                func.accept(arguments);
                return Constants.VOID;
            }

            @Override
            public int length() {
                return argCount;
            }
        };
    }

    static ScriptFunction noArgsVoid(Runnable runnable){
        return new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                runnable.run();
                return Constants.VOID;
            }

            @Override
            public int length() {
                return 0;
            }
        };
    }
}
