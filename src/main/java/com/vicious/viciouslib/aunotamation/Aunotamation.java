package com.vicious.viciouslib.aunotamation;

import com.vicious.viciouslib.aunotamation.annotation.*;
import com.vicious.viciouslib.jarloader.ViciousEventBroadcaster;
import com.vicious.viciouslib.jarloader.event.*;
import com.vicious.viciouslib.persistence.PersistenceHandler;
import com.vicious.viciouslib.persistence.storage.aunotamations.DontAutoLoad;
import com.vicious.viciouslib.persistence.storage.aunotamations.OnChanged;
import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.util.ClassAnalyzer;
import com.vicious.viciouslib.util.reflect.ClassManifest;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Aunotamation {
    private static final Map<Class<?>,AnnotationProcessor<?,?>> processors = new HashMap<>();
    private static final Map<Class<?>,ObjectProcessor> objectProcessors = new HashMap<>();

    private static Map<Class<?>,List<Runnable>> runOnInit = new HashMap<>();

    static {
        init();
    }

    public static void init(){
        if(hasProcessor(ModifiedWith.class)){
            return;
        }
        registerProcessor(new AnnotationProcessor<>(ModifiedWith.class,Class.class){
            @Override
            public void process(Class cls, AnnotatedElement element) {
                ModifiedWith mods = (ModifiedWith) cls.getAnnotation(ModifiedWith.class);
                if(!hasModifiers(getModifiers(element),mods)){
                    err(element,"is missing modifiers: " + modsToString(mods.value()));
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(NotModifiedWith.class,Class.class){
            @Override
            public void process(Class cls, AnnotatedElement element) {
                NotModifiedWith mods = (NotModifiedWith) cls.getAnnotation(NotModifiedWith.class);
                if (!doesNotHaveModifiers(getModifiers(element), mods)) {
                    err(element,"cannot have modifiers: " + modsToString(mods.value()));
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(Conflicts.class,Class.class){
            @Override
            public void process(Class cls, AnnotatedElement element) {
                Conflicts conflicts = (Conflicts) cls.getAnnotation(Conflicts.class);
                if (annotatedWithAtLeastOneOf(element, conflicts.value())) {
                    err(element,"cannot also be annotated with " + Arrays.toString(conflicts.value()));
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(RequiredType.class,Class.class){
            @Override
            public void process(Class cls, AnnotatedElement element) {
                RequiredType requiredType = (RequiredType) cls.getAnnotation(RequiredType.class);
                Class<?> type = getType(element);
                if(!requiredType.value().isAssignableFrom(type)){
                    err(element,"must be of type " + requiredType.value().getCanonicalName());
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(AllowedIn.class,Class.class){
            @Override
            public void process(Class cls, AnnotatedElement element) {
                AllowedIn allowedIn = (AllowedIn) cls.getAnnotation(AllowedIn.class);
                if(!allowedIn.value().isAssignableFrom(getElementLocation(element))){
                    err(element, "must be in a class that is of type " + allowedIn.value().getCanonicalName());
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(Parameters.class,Class.class) {
            @Override
            public void process(Class cls, AnnotatedElement element) {
                Parameters parameters = (Parameters) cls.getAnnotation(Parameters.class);
                if(element instanceof Executable executable){
                    Class<?>[] actual = executable.getParameterTypes();
                    Class<?>[] expected = parameters.value();
                    if(actual.length != expected.length){
                        err(element,"accepts a different amount of parameters than expected! Expected: " + Arrays.toString(expected));
                    }
                    for (int i = 0; i < actual.length; i++) {
                        if(!expected[i].isAssignableFrom(actual[i])){
                            err(element,"expected type assignable from " + expected[i].getCanonicalName());
                        }
                    }
                }
                else{
                    err(element, "must be a method or a constructor");
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(AnnotatedWith.class,Class.class) {
            @Override
            public void process(Class cls, AnnotatedElement element) throws Exception {
                AnnotatedWith annotatedWith = (AnnotatedWith) cls.getAnnotation(AnnotatedWith.class);
                for (Class<? extends Annotation> aClass : annotatedWith.value()) {
                    if(!element.isAnnotationPresent(aClass)){
                        err(element,"must be annotated with @" + aClass.getCanonicalName());
                    }
                }
            }
        });
        registerProcessor(new AnnotationProcessor<>(Save.class, Object.class) {
            @Override
            public void process(Object object, AnnotatedElement element) throws Exception {}
        });
        registerProcessor(new AnnotationProcessor<>(PersistentPath.class, Object.class) {
            @Override
            public void process(Object object, AnnotatedElement element) throws Exception {
                Class<?> cls = object instanceof Class<?> c ? c : object.getClass();
                Object o = DeepReflection.cycleAndExecute(cls,k->{
                    if(k.isAnnotationPresent(DontAutoLoad.class)){
                        return true;
                    }
                    else{
                        return null;
                    }
                });
                if(o == null){
                    PersistenceHandler.init(object);
                }
            }
        });
        Aunotamation.registerProcessor(new AnnotationProcessor<>(GlobalInterceptor.class,Object.class) {
            @Override
            public void process(Object object, AnnotatedElement elem) {
                if (elem instanceof Method m) {
                    if(Modifier.isStatic(m.getModifiers()) && object instanceof Class<?>) {
                        ViciousEventBroadcaster.registerAunotamated(m.getParameterTypes()[0], object, m);
                    }
                    else if(!Modifier.isStatic(m.getModifiers()) && !(object instanceof Class<?>)){
                        ViciousEventBroadcaster.registerAunotamated(m.getParameterTypes()[0], object, m);
                    }
                } else if (elem instanceof Constructor<?> c) {
                    ViciousEventBroadcaster.registerAunotamated(c.getParameterTypes()[0], object, c);
                }
            }
        });
        Aunotamation.registerProcessor(new AnnotationProcessor<>(MainEntry.class,Object.class) {
            @Override
            public void process(Object object, AnnotatedElement elem) {
            }
        });
        Aunotamation.registerProcessor(new AnnotationProcessor<>(LocalInterceptor.class, Object.class) {
            @Override
            public void process(Object object, AnnotatedElement element) throws Exception {
                if(element instanceof Method m) {
                    LocalInterceptor interceptor = element.getAnnotation(LocalInterceptor.class);
                    ClassManifest<?> manif = ClassAnalyzer.getManifest(m.getDeclaringClass());
                    for (String name : interceptor.value()) {
                        Field f = manif.getField(name);
                        InstanceEventBroadcaster ieb = (InstanceEventBroadcaster) f.get(object);
                        ieb.register(m, object);
                    }
                }
            }
        });
        Aunotamation.registerProcessor(new AnnotationProcessor<>(BroadcastTo.class, Object.class) {
            @Override
            public void process(Object object, AnnotatedElement element) throws Exception {
                if(element instanceof Field f){
                    if(f.get(object) instanceof InstanceEventBroadcaster ieb){
                        BroadcastTo bt = f.getAnnotation(BroadcastTo.class);
                        ClassManifest<?> manif = ClassAnalyzer.getManifest(f.getDeclaringClass());
                        for (String s : bt.value()) {
                            Method m = manif.getMethod(s);
                            ieb.register(m,object);
                        }
                    }
                }
            }
        });
        Aunotamation.registerProcessor(new AnnotationProcessor<>(OnChanged.class, Object.class) {
            @Override
            public void process(Object object, AnnotatedElement element) throws Exception {
            }
        });
    }
    private static Class<?> getType(AnnotatedElement element){
        if(element instanceof Field f) return f.getType();
        if(element instanceof Method m) return m.getReturnType();
        throw new InvalidAnnotationException("Your annotation is only applicable to fields and methods!");
    }
    private static int getModifiers(AnnotatedElement element){
        if(element instanceof Class<?> cls) return cls.getModifiers();
        if(element instanceof Member mem) return mem.getModifiers();
        throw new InvalidAnnotationException("Not yet implemented: " + element.getClass());
    }
    public static Class<?> getElementLocation(AnnotatedElement element){
        if(element instanceof Class<?> cls) return cls;
        if(element instanceof Member mem) return mem.getDeclaringClass();
        throw new InvalidAnnotationException("Not yet implemented: " + element.getClass());
    }
    public static String getElementName(AnnotatedElement element){
        if(element instanceof Class<?> cls) return cls.getName();
        if(element instanceof Member mem) return mem.getName();
        throw new InvalidAnnotationException("Not yet implemented: " + element.getClass());
    }
    private static boolean annotatedWithAtLeastOneOf(AnnotatedElement element, Class<? extends Annotation>... annotations){
        for (Class<? extends Annotation> annotation : annotations) {
            if(element.isAnnotationPresent(annotation)) return true;
        }
        return false;
    }
    private static String modsToString(int[] mods){
        StringBuilder modifiers = new StringBuilder();
        for (int i = 0; i < mods.length; i++) {
            modifiers.append(Modifier.toString(mods[i]));
            if(i <= mods.length-1){
                modifiers.append(", ");
            }
        }
        return modifiers.toString();
    }
    public static void registerProcessor(AnnotationProcessor<?,?> processor){
        processors.put(processor.getAnnotationClass(),processor);
        if(runOnInit.containsKey(processor.getAnnotationClass())){
            for (Runnable runnable : runOnInit.remove(processor.getAnnotationClass())) {
                runnable.run();
            }
        }
    }
    public static void registerObjectProcessor(Class<?> cls, ObjectProcessor processor){
        objectProcessors.put(cls, processor);
    }

    private static boolean hasModifiers(int modifiers, ModifiedWith mods){
        for (int i : mods.value()) {
            if((modifiers & i) == 0) return false;
        }
        return true;
    }
    private static boolean doesNotHaveModifiers(int modifiers, NotModifiedWith mods){
        for (int i : mods.value()) {
            if((modifiers & i) != 0) return false;
        }
        return true;
    }
    private static boolean isWithinValidClass(Member element, AllowedIn allowed){
        return allowed.value().isAssignableFrom(element.getDeclaringClass());
    }
    private static boolean isType(AnnotatedElement element, RequiredType instanceOf){
        Class<?> type = element instanceof Method ? ((Method) element).getReturnType() : ((Field)element).getType();
        return instanceOf.value().isAssignableFrom(type);
    }

    public static <T> T processObject(T o) {
        AtomicReference<InvalidAnnotationException> ex = new AtomicReference<>();
        DeepReflection.cycleAndExecute(o instanceof Class k ? k : o.getClass(),(objectType)-> {
            if(ex.get() != null){
                return null;
            }
            try {
                boolean doCompileCheck = false;
                if (!ClassAnalyzer.manifests.containsKey(objectType)) {
                    ClassAnalyzer.analyzeClass(objectType);
                    doCompileCheck = true;
                }
                ClassManifest<?> manifest = ClassAnalyzer.manifests.get(objectType);
                if (objectProcessors.containsKey(objectType)) {
                    objectProcessors.get(objectType).process(o);
                }
                for (AnnotatedElement aunotamatedTarget : manifest.getAunotamatedTargets()) {
                    //Gets the processors for the object type
                    for (Annotation annotation : aunotamatedTarget.getAnnotations()) {
                        if (processors.containsKey(annotation.annotationType())) {
                            AnnotationProcessor<?, ?> processor = processors.get(annotation.annotationType());
                            if (processor.getAnnotationClass().isAnnotationPresent(Extends.class)) {
                                Extends extensions = processor.getAnnotationClass().getAnnotation(Extends.class);
                                for (Class<? extends Annotation> extension : extensions.value()) {
                                    AnnotationProcessor<?,?> internal = processors.get(extension);
                                    //Automatically register annotation processors for those without it.
                                    if(internal == null){
                                        internal = new AnnotationProcessor<>((Class<Annotation>) extension,Object.class) {
                                            @Override
                                            public void process(Object object, AnnotatedElement element){}
                                        };
                                        registerProcessor(internal);
                                    }
                                    process(internal, o, aunotamatedTarget, doCompileCheck);
                                }
                            }
                            process(processor, o, aunotamatedTarget, doCompileCheck);
                        }
                    }
                }
            } catch (InvalidAnnotationException e){
                ex.set(e);
            }
            return null;
        });
        if(ex.get() != null){
            throw ex.get();
        }
        return o;
    }
    private static void process(AnnotationProcessor<?,?> proc, Object o, AnnotatedElement element, boolean compileCheck){
        if(compileCheck){
            Aunotamation.validateAnnotation(proc.getAnnotationClass(), element);
        }
        proc.processObject(o, element);
    }

    public static void validateAnnotation(Class<?> annotationClass, AnnotatedElement element) {
        for (Annotation annotation : annotationClass.getAnnotations()) {
            if(annotation.annotationType().isAnnotationPresent(AnnotationAugmentation.class)){
                AnnotationProcessor<?,?> processor = processors.get(annotation.annotationType());
                processor.processObject(annotationClass,element);
            }
            else if(annotation instanceof Extends extensions){
                for (Class<? extends Annotation> extension : extensions.value()) {
                    validateAnnotation(extension,element);
                }
            }
        }
    }

    public static boolean hasProcessor(Class<? extends Annotation> annotationType) {
        return processors.containsKey(annotationType);
    }
    public static void waitForRegistration(Class<? extends Annotation> annotationClass, Runnable run){
        if(hasProcessor(annotationClass)){
            run.run();
        }
        else runOnInit.computeIfAbsent(annotationClass,(k)->new ArrayList<>()).add(run);
    }
}
