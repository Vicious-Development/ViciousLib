package com.vicious.viciouslib.jarloader;

import com.google.common.reflect.TypeResolver;
import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.aunotamation.AnnotationProcessor;
import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.jarloader.event.EventInterceptor;
import com.vicious.viciouslib.jarloader.event.interceptor.EventInterceptorInstance;
import com.vicious.viciouslib.jarloader.event.interceptor.LambdaEventInterceptor;
import com.vicious.viciouslib.jarloader.event.interceptor.MethodEventInterceptor;
import com.vicious.viciouslib.util.ClassAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ViciousJarLoader {
    private static ViciousJarLoader instance;
    public static ViciousJarLoader getInstance(){
        if(instance == null) instance = new ViciousJarLoader();
        return instance;
    }
    private ViciousJarLoader(){}

    private final TypeResolver typeResolver = new TypeResolver();
    private final Map<Class<?>,JarInstance<?>> instances = new HashMap<>();
    private final Map<Class<?>, Set<EventInterceptorInstance>> eventInterceptors = new HashMap<>();
    private final Map<Object, Set<EventInterceptorInstance>> interceptorMap = new HashMap<>();
    public void sendEvent(Object entryEvent) {
        Set<EventInterceptorInstance> interceptors = eventInterceptors.get(entryEvent.getClass());
        if(interceptors != null){
            for (EventInterceptorInstance handler : interceptors) {
                try {
                    Object newInstance = handler.intercept(entryEvent);
                    //Mostly intended for main instance construction.
                    if(newInstance != null){
                        Object interceptor = handler.getInterceptorObject();
                        if(interceptor instanceof Class<?>){
                            Class<?> cls = (Class<?>) interceptor;
                            instances.get(cls).setMainInstance(newInstance);
                        }
                    }
                } catch (InvocationTargetException e) {
                    LoggerWrapper.logError("Failed to intercept an event using interceptor: " + handler + " because of a program logic error.");
                    e.getCause().printStackTrace();
                } catch (IllegalAccessException | InstantiationException e) {
                    LoggerWrapper.logError("Failed to intercept an event using interceptor: " + handler + " because the interceptor is not publicly accessible.");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds all possible event interceptors for the object provided.
     * Providing a class object will create interceptors for all possible static methods in the class.
     * Providing an instance of an object will create interceptors for all possible non-static methods in the class.
     * Providing a Consumer will use the consumer.
     */
    public void addEventInterceptor(Object o){
        ClassAnalyzer.analyzeClass(o.getClass());
    }
    public <T> void addEventInterceptor(Consumer<T> consumer){
        Type type = typeResolver.resolveType(consumer.getClass().getGenericSuperclass());
        if(type instanceof Class<?>){
            Class<?> eventClass = (Class<?>) type;
            EventInterceptorInstance instance = new LambdaEventInterceptor(consumer,consumer,eventClass);
            registerEventInterceptor(instance);
        }
        else{
            LoggerWrapper.logError("Could not get type of event consumer!");
        }
    }

    /**
     * Removes all interceptors for the registered event handler.
     */
    public void removeEventInterceptor(Object o){
        Set<EventInterceptorInstance> instances = interceptorMap.remove(o);
        if(instances != null){
            for (EventInterceptorInstance eventInterceptorInstance : instances) {
                Set<EventInterceptorInstance> clsmapped =  eventInterceptors.get(eventInterceptorInstance.getEventType());
                if(clsmapped != null){
                    clsmapped.remove(eventInterceptorInstance);
                }
            }
        }
    }

    public static void init(){
        Aunotamation.registerProcessor(new AnnotationProcessor<>(EventInterceptor.class,Object.class) {
            @Override
            public void process(Object object, AnnotatedElement elem) throws Exception {
                if (elem instanceof Method m) {
                    ViciousJarLoader.getInstance().registerEventInterceptor(m.getParameterTypes()[0], object, m);
                } else if (elem instanceof Constructor<?> c) {
                    ViciousJarLoader.getInstance().registerEventInterceptor(c.getParameterTypes()[0], object, c);
                }
            }
        });
    }

    private void registerEventInterceptor(Class<?> eventType, Object target, Executable exec){
        registerEventInterceptor(new MethodEventInterceptor(target,exec,eventType));
    }
    private void registerEventInterceptor(EventInterceptorInstance inst){
        Set<EventInterceptorInstance> interceptors = eventInterceptors.computeIfAbsent(inst.getEventType(), k -> new HashSet<>());
        interceptors.add(inst);
        interceptors = interceptorMap.computeIfAbsent(inst.getInterceptorObject(), k -> new HashSet<>());
        interceptors.add(inst);
    }

    /**
     * Gets the main class for the jar file by locating 'mainentry.info'
     * Mainentry only contains a single line with the canonical class name for the main class.
     * This main class should have at least one @EventInterceptor annotated method otherwise the class is basically useless.
     * This EventInterceptor method of course should take in an initialization event as a parameter, which depends on the runtime environment.
     * An example of this would be ForgeModLoader's FMLInitialization event, although FML does not use VJL to handle mod loading.
     */
    public static Class<?> getMainClass(File file) {
        try {
            ZipFile zip = new ZipFile(file);
            ZipEntry jarInfo = zip.getEntry("mainentry.info");
            if(jarInfo == null) return null;
            InputStream infoStream = zip.getInputStream(jarInfo);
            Scanner scan = new Scanner(infoStream);
            String mainClassName = scan.nextLine();
            scan.close();
            Class<?> main = Class.forName(mainClassName);
            return main;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {}
        return null;
    }

    /**
     * Creates a JarInstance if possible using the
     * @param file
     * @return
     */
    public JarInstance<?> loadJar(File file){
        Class<?> main = getMainClass(file);
        if(main != null) {
            if(!instances.containsKey(main)) {
                JarInstance<?> ji = new JarInstance<>(file, main);
                instances.put(main, ji);
                ClassAnalyzer.analyzeClass(main);
                return ji;
            }
            else{
                System.err.println("Attempted to load the same jar main class " + main.getCanonicalName() + " multiple times! ");
            }
        }
        else{
            System.err.println("Could not find main class for file: " + file + " make sure your jar has a valid 'mainentry.info' file");
        }
        return null;
    }
}
