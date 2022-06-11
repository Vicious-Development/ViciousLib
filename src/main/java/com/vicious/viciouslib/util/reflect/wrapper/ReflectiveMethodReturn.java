package com.vicious.viciouslib.util.reflect.wrapper;


import com.vicious.viciouslib.util.reflect.Reflection;

import java.lang.reflect.Method;

public class ReflectiveMethodReturn extends ReflectiveMethod{
    private final Class<?> returnType;
    public ReflectiveMethodReturn(String name, Class<?> returnType, Class<?>... params) {
        super(name, params);
        this.returnType=returnType;
    }
    public ReflectiveMethodReturn(Method m){
        super(m.getName(),m.getParameterTypes());
        returnType=m.getReturnType();
        o=m;
    }
    @Override
    public Method getReflectiveTarget(Object in) {
        if(o == null){
            o = Reflection.getMethodReturn(in,name,returnType,params);
        }
        return o;
    }
}
