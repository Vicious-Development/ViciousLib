package com.vicious.viciouslib.staticinheritance;

import java.lang.reflect.Method;

/**
 * This class seems useless. It is not, this system allows creating static method in interfaces that can be overriden in implementers.
 * To do so, the interface must have a method or field that returns the StaticMethod.get().
 * And so, to get the field value, one must call an interface method or field returning the StaticMethod.get() and have in the inheriting class a static method with the field name.
 */
public class StaticMethod {
    public static <T,E> E get(Class<T> clazz, Class<E> mtdResult, Class<?>[] paramTypes, Object[] parameters, String method) throws Exception {
        Method partypmtd = null;
        try{
            partypmtd = clazz.getDeclaredMethod(method, paramTypes);
            partypmtd.setAccessible(true);
        } catch (NoSuchMethodException e){
            throw new NoSuchMethodException("Your class is lacking a STATIC " + method + " method! Invoked on Class:" + clazz.getCanonicalName());
        }
        //Gets the data from static field parameterTypes
        return (E) partypmtd.invoke(clazz, parameters);
    }
}
