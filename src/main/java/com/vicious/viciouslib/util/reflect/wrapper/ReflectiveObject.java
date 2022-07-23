package com.vicious.viciouslib.util.reflect.wrapper;

import com.vicious.viciouslib.LoggerWrapper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Stores Reflectively obtained objects.
 */
public abstract class ReflectiveObject<T extends AccessibleObject & Member> {
    private static Field MODIFIERSFIELD;
    private static boolean canDefinalize = true;
    protected T o;
    public abstract T getReflectiveTarget(Object in);
    public ReflectiveObject(){}
    public ReflectiveObject(T o){
        this.o=o;
        setAccessible();
        definalize();
    }
    public ReflectiveObject<T> definalize(){
        if(canDefinalize) {
            if (MODIFIERSFIELD == null) {
                try {
                    MODIFIERSFIELD = Field.class.getDeclaredField("modifiers");
                    MODIFIERSFIELD.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    LoggerWrapper.logError("VL has no ability to definalize using reflection in this java implementation.");
                    canDefinalize = false;
                    return this;
                }
            }
            try {
                MODIFIERSFIELD.setInt(o,o.getModifiers() & ~Modifier.FINAL);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    public ReflectiveObject<T> setAccessible(){
        o.setAccessible(true);
        return this;
    }
}
