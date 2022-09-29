package com.vicious.viciouslib.util.reflect.wrapper;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.util.reflect.Reflection;

import java.lang.reflect.Constructor;

public class ReflectiveConstructor extends ReflectiveObject<Constructor<?>>{
    private Class<?>[] params;
    public ReflectiveConstructor(Class<?>[] params){
        this.params=params;
    }
    public ReflectiveConstructor(Constructor<?> constructor){
        super(constructor);
        this.params=constructor.getParameterTypes();
    }
    public Object construct(Class<?> constructorClass, Object... params){
        Constructor<?> cnsrt = getReflectiveTarget(constructorClass);
        try {
            return cnsrt.newInstance(params);
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Constructor<?> getReflectiveTarget(Object target) {
        if(o == null){
            o = Reflection.getConstructor(target,params);
            setAccessible();
        }
        return o;
    }
}
