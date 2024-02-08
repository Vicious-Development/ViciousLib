package com.vicious.viciouslib.script.function;

public class ScriptFunctions {
    public static final ScriptFunction add = new ScriptFunction() {
        @Override
        public Object apply(Object[] arguments) {
            Number n1 = (Number) arguments[0];
            Number n2 = (Number) arguments[1];
            return n1.doubleValue()+n2.doubleValue();
        }

        @Override
        public int length() {
            return 2;
        }
    };
}
