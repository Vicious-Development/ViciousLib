package com.vicious.viciouslib.util.reflect.deep;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodSearchContext extends SearchContext<Method>{
    public Class<?> returnType;
    public Class<?>[] params;
    public MethodSearchContext() {
    }

    /**
     * Will match a method.
     * If you don't want a name to be used do not use a name.
     */
    @Override
    public boolean matches(Method in) {
        if(name != null){
            return in.getName().equals(name);
        }
        else{
            if(returnType != null && returnType != in.getReturnType()) return false;
            Class<?>[] inParams = in.getParameterTypes();
            if(inParams.length != params.length) return false;
            for (int i = 0; i < params.length; i++) {
                if(params[i] != inParams[i]) return false;
            }
            return true;
        }
    }

    @Override
    public MethodSearchContext after(SearchContext<Method> ctx) {
        super.after(ctx);
        return this;
    }
    @Override
    public MethodSearchContext before(SearchContext<Method> ctx) {
        super.before(ctx);
        return this;
    }

    @Override
    public MethodSearchContext name(String name) {
        super.name(name);
        return this;
    }

    public MethodSearchContext returns(Class<?> type){
        returnType=type;
        return this;
    }
    public MethodSearchContext accepts(Class<?>... params){
        this.params=params;
        return this;
    }

    @Override
    public String toString() {
        return "MethodSearchContext{" +
                "returnType=" + returnType +
                ", params=" + Arrays.toString(params) +
                ", after=" + after +
                ", before=" + before +
                ", name='" + name + '\'' +
                '}';
    }
}
