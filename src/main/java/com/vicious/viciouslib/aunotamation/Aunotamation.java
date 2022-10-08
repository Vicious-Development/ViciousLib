package com.vicious.viciouslib.aunotamation;

import com.vicious.viciouslib.aunotamation.annotation.*;
import com.vicious.viciouslib.util.ClassAnalyzer;
import com.vicious.viciouslib.util.reflect.ClassManifest;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Aunotamation {
    private static final Map<Class<?>,AnnotationProcessor<?,?>> processors = new HashMap<>();
    private static final Map<Class<?>,ObjectProcessor> objectProcessors = new HashMap<>();

    static {
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
                    err(element, "must be in a class that is of type" + allowedIn.value().getCanonicalName());
                }
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
        DeepReflection.cycleAndExecute(o instanceof Class k ? k : o.getClass(),(objectType)-> {
            boolean doCompileCheck = false;
            if (!ClassAnalyzer.manifests.containsKey(objectType)) {
                ClassAnalyzer.analyzeClass(objectType);
                doCompileCheck = true;
            }
            ClassManifest<?> manifest = ClassAnalyzer.manifests.get(objectType);
            if(objectProcessors.containsKey(objectType)){
                objectProcessors.get(objectType).process(o);
            }
            for (AnnotatedElement aunotamatedTarget : manifest.getAunotamatedTargets()) {
                //Gets the processors for the object type
                for (Annotation annotation : aunotamatedTarget.getAnnotations()) {
                    if(processors.containsKey(annotation.annotationType())){
                        AnnotationProcessor<?,?> processor = processors.get(annotation.annotationType());
                        if(processor.getAnnotationClass().isAnnotationPresent(Extends.class)){
                            Extends extensions = processor.getAnnotationClass().getAnnotation(Extends.class);
                            for (Class<? extends Annotation> extension : extensions.value()) {
                                process(Aunotamation.processors.get(extension),o,aunotamatedTarget,doCompileCheck);
                            }
                        }
                        process(processor,o,aunotamatedTarget,doCompileCheck);
                    }
                }
            }
            return null;
        });
        return o;
    }
    private static void process(AnnotationProcessor<?,?> proc, Object o, AnnotatedElement element, boolean compileCheck){
        if(compileCheck) Aunotamation.validateAnnotation(proc.getAnnotationClass(),element);
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
}
