package com.vicious.viciouslib.util.reflect.wrapper;

/**
 * Stores Reflectively obtained objects.
 */
public abstract class ReflectiveObject<T> {
    protected T o;
    public abstract T getReflectiveTarget(Object in);
}
