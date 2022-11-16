package com.vicious.viciouslib.util.reflect;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.serialization.SerializationUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reflection {

    /**
     * Allows accessing private nonstatic FIELDS in an Object.
     * @param accessed = the object.
     * @param fieldname = the field.
     * @return uncasted field data
     */
    public static Object accessField(Object accessed, String fieldname){
        Field f = getField(accessed,fieldname);
        if(f != null){
            try{
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                return f.get(accessed);
            } catch(IllegalAccessException e){

            }
        }
        return null;
    }

    public static Object accessField(Object obj, Field f){
        if(f != null){
            try{
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                return f.get(obj);
            } catch(IllegalAccessException e){

            }
        }
        return null;
    }
    public static Method getMethod(Object accessed, String methodname, Class<?>[] parameters){
        Class<?> clazz = accessed instanceof Class<?> ? (Class<?>)accessed : accessed.getClass();
        return SerializationUtil.executeOnTargetClass((cls)->{
            try {
                return cls.getDeclaredMethod(methodname,parameters);
            } catch (NoSuchMethodException ignored) {
                try {
                    return cls.getDeclaredMethod(methodname,parameters);
                } catch (NoSuchMethodException ignored1) {
                    return null;
                }
            }
        } ,clazz);
    }
    public static Object invokeMethod(Object accessed, String methodname, Class<?>[] parameters, Object[] args){
        Method m = getMethod(accessed,methodname,parameters);
        try {
            if(!m.isAccessible()) m.setAccessible(true);
            if (m.getReturnType() == void.class) {
                m.invoke(accessed, args);
            } else return m.invoke(accessed, args);
        } catch(IllegalAccessException | InvocationTargetException ignored){}
        return null;
    }
    public static Object invokeMethod(Object accessed, Method m, Object[] args){
        try {
            if(!m.isAccessible()) m.setAccessible(true);
            if (m.getReturnType() == void.class) {
                m.invoke(accessed, args);
            } else return m.invoke(accessed, args);
        } catch(IllegalAccessException ignored){
        } catch (InvocationTargetException e) {
            LoggerWrapper.logError("Failed to Reflectively invoke!");
            e.getCause().printStackTrace();
        }
        return null;
    }
    //Disabled for the time being due to java not supporting this, even via reflection.
    //Future solution: create a super duper object, clone the original object's fields into the superduper. Run the method. Clone the fields from the superduper to the child.
    /*public static Object invokeSuperDuperMethod(Object accessed, Class<?> superClass, Class<?>[] parameterTypes, Object[] args, String methodName) {
        Method m = getMethod(supgierClass,methodName,parameterTypes);
        try {
            if (m.getReturnType() == void.class) {
                m.invoke(accessed, args);
            } else return m.invoke(accessed, args);
        } catch(IllegalAccessException | InvocationTargetException ignored){}
        return null;
    }*/
    public static Field getField(Object accessed, String fieldname) {
        Class<?> clazz = accessed instanceof Class<?> ? (Class<?>) accessed : accessed.getClass();
        return SerializationUtil.executeOnTargetClass((cls)->{
            try {
                return cls.getDeclaredField(fieldname);
            } catch (NoSuchFieldException ignored) {
                try {
                    return cls.getDeclaredField(fieldname);
                } catch (NoSuchFieldException ignored1) {
                    return null;
                }
            }
        } ,clazz);
    }

    /**
     * Pure evil. Time to start fucking with things you shouldn't eh?
     * In a separate method so those who desire to change final fields have to intentionally.
     */
    public static void definalize(Object accessed, String fieldname){
        Field f = getField(accessed,fieldname);
        if(f != null){
            try{
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                Field finfield = Field.class.getDeclaredField("modifiers");
                finfield.setAccessible(true);
                finfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

            } catch(IllegalAccessException | NoSuchFieldException ignored){

            }
        }
    }

    public static void setField(Object accessed, Object value, String fieldname){
        Field f = getField(accessed, fieldname);
        if (f != null) {
            try {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                f.set(accessed, value);
            } catch (IllegalAccessException ignored) {
            }
        }
    }
    public static void setField(Object accessed, Object value, Field f){
        if (f != null) {
            try {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                f.set(accessed, value);
            } catch (IllegalAccessException ignored) {
            }
        }
    }
    public static Constructor<?> getConstructor(Class<?> accessed, Class<?>[] params){
        try {
            return accessed.getConstructor(params);
        } catch(NoSuchMethodException ignored){}
        return null;
    }
    public static Object accessStaticField(Class<?> accessed, String fieldname){
        Field f = null;
        try {
            f = accessed.getDeclaredField(fieldname);
        } catch(NoSuchFieldException ignored){}
        if(f != null){
            try{
                return f.get(accessed);
            } catch(IllegalAccessException ignored){}
        }
        return null;
    }


    public static Class<?> getClassContainingMethod(Class<?> root, String name, Class<?>... params){
        try {
            Method m = root.getDeclaredMethod(name, params);
            return root;
        } catch (NoSuchMethodException ex){
            if(root.getSuperclass() != null) return getClassContainingMethod(root.getSuperclass(),name,params);
            else{
                LoggerWrapper.logError("NO METHOD FOUND FOR CLASS: " + root + " , M: " + name + "->" + Arrays.toString(params));
                ex.printStackTrace();
            }
        }
        return null;
    }
    public static Class<?> getClassContainingField(Class<?> root, String name){
        try {
            Field f = root.getDeclaredField(name);
            return root;
        } catch (NoSuchFieldException ex){
            if(root.getSuperclass() != null) return getClassContainingField(root.getSuperclass(),name);
            else{
                LoggerWrapper.logError("NO FIELD FOUND FOR CLASS: " + root + " , F: " + name);
                ex.printStackTrace();
            }
        }
        return null;
    }
    public static Constructor<?> getConstructor(Object source, Class<?>... params){
        Class<?> cls = source instanceof Class<?> ? (Class<?>) source : source.getClass();
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor(params);
            if(!constructor.isAccessible()) constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Method getMethodReturn(Object source, String name, Class<?> returnType, Class<?>... params){
        Class<?> cls = source instanceof Class<?> ? (Class<?>) source : source.getClass();
        l1: for (Method method : cls.getMethods()) {
            if(method.getName().equals(name)) {
                if (method.getReturnType() == returnType) {
                    Class<?>[] mp = method.getParameterTypes();
                    for (int i = 0; i < mp.length; i++) {
                        if (mp[i] != params[i]) {
                            continue l1;
                        }
                    }
                    return method;
                }
            }
        }
        return cls.getSuperclass() == null ? null : getMethodReturn(cls.getSuperclass(),name,returnType,params);
    }
    public static String classManifest(Class<?> cls, boolean showInherited){
        Class<?>[] interfaces = cls.getInterfaces();
        Class<?> superClass = cls.getSuperclass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        if(showInherited){
            Class<?> scls = cls.getSuperclass();
            while(scls != null){
                Method[] smeth = scls.getDeclaredMethods();
                Method[] combined = new Method[methods.length + smeth.length];
                int i = 0;
                for (Method method : smeth) {
                    combined[i]=method;
                    i++;
                }
                for(Method method : methods){
                    combined[i]=method;
                    i++;
                }
                methods = combined;
                Field[] sfield = scls.getDeclaredFields();
                Field[] combinedF = new Field[fields.length + sfield.length];
                i = 0;
                for (Field field : sfield) {
                    combinedF[i]=field;
                    i++;
                }
                for(Field field : fields){
                    combinedF[i]=field;
                    i++;
                }
                fields = combinedF;
                scls=scls.getSuperclass();
            }
            for (Class<?> itf : interfaces) {
                Method[] smeth = itf.getDeclaredMethods();
                Method[] combined = new Method[methods.length + smeth.length];
                int i = 0;
                for (Method method : smeth) {
                    combined[i]=method;
                    i++;
                }
                for(Method method : methods){
                    combined[i]=method;
                    i++;
                }
                methods = combined;
                Field[] sfield = itf.getDeclaredFields();
                Field[] combinedF = new Field[fields.length + sfield.length];
                i = 0;
                for (Field field : sfield) {
                    combinedF[i]=field;
                    i++;
                }
                for(Field field : fields){
                    combinedF[i]=field;
                    i++;
                }
                fields = combinedF;
            }
        }
        Constructor<?>[] constructors = cls.getConstructors();
        String s = "" + cls + " extends " + superClass + " implements " + Arrays.toString(interfaces);
        s += "\n Fields:";
        if(!showInherited) {
            for (Field field : fields) {
                s += "\n" + field.getType() + " " + field.getName();
            }
            s += "\n Constructors:";
            for (Constructor<?> constructor : constructors) {
                s += "\n(" + Arrays.toString(constructor.getParameterTypes()) + ")";
            }
            s += "\nMethods:";
            for (Method method : methods) {
                s += "\n" + method.getReturnType() + " " + method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ")";
            }
        }
        else{
            for (Field field : fields) {
                s += "\n" + field.getType() + " " + field.getName() + " in: " + field.getDeclaringClass();
            }
            s += "\n Constructors:";
            for (Constructor<?> constructor : constructors) {
                s += "\n(" + Arrays.toString(constructor.getParameterTypes()) + ")";
            }
            s += "\nMethods:";
            for (Method method : methods) {
                s += "\n" + method.getReturnType() + " " + method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ") in: " + method.getDeclaringClass();
            }
        }
        return s;
    }


    /**
     * The following two methods were intended to allow us to access private INNER classes. It has a problem though. Constructing a class from to the desired private class is practically impossible.
     * I'm leaving this here in case I find a solution or this code becomes useful.
     *
     */
    public static <T> Class<?> accessClass(Class<T> accessed, String className) {
        Class<?>[] classes = accessed.getDeclaredClasses();
        for(Class<?> cls : classes){
            if(cls.getName().equals(className)){
                return cls;
            }
        }
        return null;
    }
    public static Object construct(Constructor<?> constructor, Object[] params){
        try {
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Field> getFieldsOfType(Class<?> clazz, Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while(clazz != null) {
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.getType().equals(type)) fields.add(declaredField);
                if(declaredField.getType().getSuperclass() != null && declaredField.getType().getSuperclass().equals(type)) fields.add(declaredField);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    public static Field getFieldContaining(Object object, Class<?> type, Object toFind) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = object.getClass();
        while(clazz != null) {
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.getType().equals(type)) fields.add(declaredField);
                if(declaredField.getType().getSuperclass() != null && declaredField.getType().getSuperclass().equals(type)) fields.add(declaredField);
            }
            clazz = clazz.getSuperclass();
        }
        for (Field field : fields) {
            if(!field.isAccessible()) field.setAccessible(true);
            try {
                if (field.get(object) == toFind) return field;
            } catch(IllegalAccessException ignored){}
        }
        return null;
    }

    public static String fieldsToString(Object object) {
        Class<?> clazz = object.getClass();
        String fields = "";
        while(clazz != null){
            for (Field declaredField : clazz.getDeclaredFields()) {
                fields += "\n" + declaredField;
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
