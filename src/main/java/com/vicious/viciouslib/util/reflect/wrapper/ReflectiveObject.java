package com.vicious.viciouslib.util.reflect.wrapper;

import com.vicious.viciouslib.LoggerWrapper;

import java.lang.reflect.*;

/**
 * Stores Reflectively obtained objects.
 */
public abstract class ReflectiveObject<T extends AccessibleObject & Member> {
    private static Field MODIFIERSFIELD;
    private static boolean canDefinalize = true;
    protected T o;

    public static ReflectiveObject<?> of(AnnotatedElement elem) {
        if(elem instanceof Method){
            return new ReflectiveMethod((Method)elem);
        }
        if(elem instanceof Constructor){
            return new ReflectiveConstructor((Constructor<?>)elem);
        }
        if(elem instanceof Field){
            return new ReflectiveField((Field) elem);
        }
        return null;
    }

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
        if(!o.isAccessible()) o.setAccessible(true);
        return this;
    }
    public T target(){
        return o;
    }

    public boolean isStatic() {
        return Modifier.isStatic(o.getModifiers());
    }
}
