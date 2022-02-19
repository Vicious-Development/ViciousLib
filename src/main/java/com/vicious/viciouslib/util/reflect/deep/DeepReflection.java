package com.vicious.viciouslib.util.reflect.deep;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.util.JarReader;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveMethodReturn;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Alright boys, I hope you reflect deeply on your life choices if you use this class.
 * Methods here are much more computationally expensive and should be used only once if ever.
 *
 */
public class DeepReflection {
    private static Map<ClassLocation,Class<?>> classMap = new HashMap<>();
    public static Class<?> get(String name, String owner){
        return classMap.get(new ClassLocation(name,owner));
    }
    public static void mapClasses(Enumeration<URL> roots, ClassLoader cloader){
        while(roots.hasMoreElements()){
            JarReader.cycleJarEntries(roots.nextElement(), (e)->{
                String name = e.getName();
                name = name.replaceAll("/",".");
                if(!name.endsWith(".class")) return;
                name = name.replace(".class","");
                String owner = "";
                boolean end = false;
                for (int i = 0; i < name.length(); i++) {
                    char c = name.charAt(i);
                    if(c == '.'){
                        if(end){
                           break;
                        }
                        else{
                            end = true;
                        }
                    }
                    owner+=c;
                }
                try {
                    Class<?> cls = Class.forName(name,false,cloader);
                    if(cls == null) return;
                    ClassLocation cl = new ClassLocation(reverseSubstring(reverseIndexOf('.',name),name),owner);
                    classMap.put(cl,cls);
                } catch (ClassNotFoundException classNotFoundException) {
                    LoggerWrapper.logError("Failed to load a class that exists.");
                    classNotFoundException.printStackTrace();
                }
            });
        }
    }

    private static String reverseSubstring(int idx, String s) {
        String ret = "";
        for (int i = s.length()-1; i > idx; i--) {
            ret = s.charAt(i) + ret;
        }
        return ret;
    }

    private static int reverseIndexOf(char target, String s) {
        for (int i = s.length()-1; i > 0; i--) {
            char c = s.charAt(i);
            if(target == c){
                return i;
            }
        }
        return -1;
    }

    /**
     * U
     */
    public static void purge(){
        classMap.clear();
    }

    /**
     * Uses a MethodSearchContext to locate methods more effectively. This is the most likely to succeed method search.
     */
    public static ReflectiveMethodReturn getMethod(Object target, MethodSearchContext ctx) throws TotalFailureException {
        Class<?> cls = target instanceof Class<?> ? (Class<?>) target : target.getClass();
        List<Method> obtained = cycleAndExecute(cls,(c)->{
            List<Method> result = ctx.getAllMatchingWithin(c.getDeclaredMethods());
            return result.size() > 0 ? result : null;
        });
        if(obtained == null){
            throw new TotalFailureException("Failed to locate method using all forms of searching. Are you sure it exists?");
        }
        else if(obtained.size() > 1){
            throw new BadSearchException("Found " + obtained.size() + " results for the method search executed: " + ctx);
        }
        return new ReflectiveMethodReturn(obtained.get(0));
    }

    /**
     * Does same as above but does not fail if multiple methods are found.
     */
    public static List<Method> queryMethods(Object target, MethodSearchContext ctx) throws TotalFailureException{
        Class<?> cls = target instanceof Class<?> ? (Class<?>) target : target.getClass();
        List<Method> obtained = cycleAndExecute(cls,(c)->{
            List<Method> result = ctx.getAllMatchingWithin(c.getDeclaredMethods());
            return result.size() > 0 ? result : null;
        });
        if(obtained == null){
            throw new TotalFailureException("Failed to locate method using all forms of searching. Are you sure it exists?");
        }
        return obtained;
    }

    /**
     * Please use this only if you are confident the method will be locatable. Use the system above for more success.
     */
    public static ReflectiveMethodReturn getMethod(Object target, String nameOption, Class<?> returnType, Class<?>... params) throws TotalFailureException {
        Class<?> cls = target instanceof Class<?> ? (Class<?>) target : target.getClass();
        ReflectiveMethodReturn ret;
        //Prioritize utilization of the name option as its faster.
        if(nameOption != null){
            ret = getNamedMethod(cls,nameOption,params);
            if(ret != null) return ret;
        }
        //Else use facts and logic (no feelings)
        Method m = findMethod(cls,(method)->{
            if(method.getReturnType()==returnType){
                Class<?>[] mp = method.getParameterTypes();
                for (int i = 0; i < mp.length; i++) {
                    if(mp[i] != params[i]){
                        return false;
                    }
                }
                return true;
            } else return false;
        });
        if(m == null){
            throw new TotalFailureException("Failed to locate method using all forms of searching. Are you sure it exists?");
        }
        return new ReflectiveMethodReturn(m);
    }

    /**
     * Searches for a method in the class provided (and any of its superiors) using the filter.
     */
    public static Method findMethod(Class<?> cls, Predicate<Method> filter){
        return cycleAndExecute(cls, (c)->{
            for (Method m : cls.getDeclaredMethods()) {
                if(filter.test(m)){
                    return m;
                }
            }
            return null;
        });
    }

    /**
     * Gets a named method and wraps it for ease of use (mostly for caching, for the love of all things holy please cache this!).
     */
    public static ReflectiveMethodReturn getNamedMethod(Class<?> cls, String name, Class<?>... params){
        return cycleAndExecute(cls,(c)->{
            try {
                return new ReflectiveMethodReturn(cls.getDeclaredMethod(name,params));
            } catch (NoSuchMethodException ignored) {
                return null;
            }
        });
    }

    /**
     * Cycles through a class, its parents, its interfaces and their parents recursively.
     */
    public static <T> T cycleAndExecute(Class<?> cls, Function<Class<?>,T> func){
        if(cls == null) return null;
        //Try current class.
        T t = func.apply(cls);
        if(t != null) return t;
        //Try Interfaces
        for (Class<?> itf : cls.getInterfaces()) {
            t = cycleAndExecute(itf,func);
            if(t != null) return t;
        }
        //Try superclass
        return cycleAndExecute(cls.getSuperclass(),func);
    }
}
