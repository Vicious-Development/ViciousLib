package com.vicious.viciouslib.staticinheritance;


import java.lang.reflect.Field;

/**
 * This class seems useless. It is not, this system allows creating static fields in interfaces and classes that can be overriden in implementers.
 * To do so, the interface must have a method or field that returns the StaticField.get().
 * And so, to get the field value, one must call an interface method or field returning the StaticField.get() and have in the inheriting class a static field with the field name.
 */
public class StaticField {
    /**
     *
     * @param clazz = The inheriting class. I.E. Class A inherits from Class B, clazz = A.class
     * @param fieldResult = The class of the field's result, it is possible to get the field's type but this ensures that we get an E rather than an Object
     * @param field = The field name.
     * @param <T>
     * @param <E>
     * @return The field's value
     */
    public static <T,E> E get(Class<T> clazz, Class<E> fieldResult, String field) throws Exception{
        Field partypfld = null;
        try {
            partypfld = clazz.getDeclaredField(field);
            partypfld.setAccessible(true);
        } catch (NoSuchFieldException e){
            throw new NoSuchFieldException("Your class is lacking a STATIC " + field + " field! Invoked on Class:" + clazz.getCanonicalName());
        }
        return (E) partypfld.get(clazz);
    }
}
