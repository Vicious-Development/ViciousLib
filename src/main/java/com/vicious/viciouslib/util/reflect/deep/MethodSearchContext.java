package com.vicious.viciouslib.util.reflect.deep;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MethodSearchContext extends SearchContext<Method>{
    public Class<?>[] params;
    public Class<?>[] exceptions;

    public MethodSearchContext() {
    }

    /**
     * Will match a method.
     * If you don't want a name to be used do not use a name.
     */
    @Override
    public boolean matches(Method in) {
        //Did this to shut intellij up, same as &&.
        if(!super.matches(in)) return false;
        if(type != null && type != in.getReturnType()) return false;
        if(params != null) {
            Class<?>[] inParams = in.getParameterTypes();
            if (inParams.length != params.length) return false;
            for (int i = 0; i < params.length; i++) {
                if (params[i] != inParams[i]) return false;
            }
        }
        if(exceptions != null){
            Class<?>[] inExceptions = in.getExceptionTypes();
            if(exceptions.length != inExceptions.length) return false;
            for (int i = 0; i < exceptions.length; i++) {
                if (exceptions[i] != inExceptions[i]) return false;
            }
        }
        if(superType != null){
            if(!superType.isAssignableFrom(in.getReturnType())){
                return false;
            }
        }
        return true;
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
    public MethodSearchContext exceptions(Class<?>... exceptions){
        this.exceptions=exceptions;
        return this;
    }
    /**
     * Takes a list of modifier predicators to decide if the method matches the modifiers.
     * @see java.lang.reflect.Modifier for information on what to use.
     */
    public MethodSearchContext withAccess(List<Predicate<Integer>> modifierPredicators){
        this.modifierPredicators=modifierPredicators;
        return this;
    }


    @Override
    public MethodSearchContext name(String name) {
        super.name(name);
        return this;
    }

    public MethodSearchContext returns(Class<?> type){
        this.type =type;
        return this;
    }
    public MethodSearchContext accepts(Class<?>... params){
        this.params=params;
        return this;
    }

    @Override
    public MethodSearchContext annotated(Class<? extends Annotation>... annotations) {
        super.annotated(annotations);
        return this;
    }

    @Override
    public String toString() {
        return "MethodSearchContext{" +
                "returnType=" + type +
                ", params=" + Arrays.toString(params) +
                ", after=" + after +
                ", before=" + before +
                ", name='" + name + '\'' +
                '}';
    }
}
