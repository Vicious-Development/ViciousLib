package com.vicious.viciouslib.util.reflect.wrapper;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.util.reflect.Reflection;

import java.lang.reflect.Method;

public class ReflectiveMethod extends ReflectiveObject<Method>{
    protected String name;
    protected Class<?>[] params;
    public ReflectiveMethod(Method m){
        super(m);
        this.name=m.getName();
        this.params=m.getParameterTypes();
    }
    public ReflectiveMethod(String name, Class<?>... params){
        this.name=name;
        this.params=params;
    }
    public Object invoke(Object target, Object... params){
        try {
            return Reflection.invokeMethod(target,getReflectiveTarget(target),params);
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Method getReflectiveTarget(Object in) {
        if(o == null){
            o = Reflection.getMethod(in,name,params);
            setAccessible();
        }
        return o;
    }
}
