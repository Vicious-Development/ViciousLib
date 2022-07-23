package com.vicious.viciouslib.util.reflect.wrapper;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.util.reflect.Reflection;

import java.lang.reflect.Field;

public class ReflectiveField extends ReflectiveObject<Field>{
    private String name;
    public ReflectiveField(String name){
        this.name=name;
    }
    public ReflectiveField(Field f){
        super(f);
        this.name=f.getName();
    }
    public Object get(Object target){
        try {
            return Reflection.accessField(target,getReflectiveTarget(target));
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public void set(Object target, Object result){
        try {
            getReflectiveTarget(target).set(target,result);
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public Field getReflectiveTarget(Object target) {
        if(o == null){
            o = Reflection.getField(target,name);
            setAccessible();
            definalize();
        }
        return o;
    }
}
