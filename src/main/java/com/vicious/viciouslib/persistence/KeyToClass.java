package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.persistence.vson.VSONArray;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;
import com.vicious.viciouslib.util.BiMap;

import java.lang.reflect.InvocationTargetException;


/**
 * Used to account for class names changing, assigning keys to classes.
 */
public class KeyToClass {
    private static final BiMap<String,Class<?>> classes = new BiMap<>();

    public static String get(Class<?> cls) throws ClassNotFoundException {
        if(!classes.containsValue(cls)){
            throw new ClassNotFoundException();
        }
        return classes.getByValue(cls);
    }

    public static Class<?> get(String str) throws ClassNotFoundException {
        if(!classes.containsKey(str)){
            throw new ClassNotFoundException();
        }
        return classes.getByKey(str);
    }

    public static void register(Class<?> cls, String key){
        classes.put(key,cls);
    }

    public static Object newInstance(String key) throws ClassNotFoundException {
        Class<?> cls = get(key);
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Missing publicly accessible default constructor for serializable class.");
        }
    }

    static {
        classes.put("vicious.vsonmap", VSONMap.class);
        classes.put("vicious.vsonarray", VSONArray.class);
        classes.put("vicious.vsonvalue", VSONValue.class);
        classes.put("vicious.vsonmapping", VSONMapping.class);
    }
}
