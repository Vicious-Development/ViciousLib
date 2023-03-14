package com.vicious.viciouslib.util.reflect;

import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.util.ClassAnalyzer;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class ClassManifest<T> {
    private final Class<T> cls;
    public ClassManifest(Class<T> cls){
        this.cls=cls;
        handleAnnotations(cls);
        process();
    }
    @SuppressWarnings("unchecked")
    private void process(){
        for (Method m : cls.getDeclaredMethods()) {
            processMethod(m);
        }
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            processConstructor((Constructor<T>) c);
        }
        for (Field f : cls.getDeclaredFields()) {
            processField(f);
        }
        DeepReflection.cycleAndExecute(cls,(c)->{
            if(cls != c){
                if(!ClassAnalyzer.manifests.containsKey(c)){
                    ClassAnalyzer.analyzeClass(c);
                }
                superManifests.put(c,ClassAnalyzer.manifests.get(c));
            }
            return null;
        });
    }
    private final Map<Class<?>,ClassManifest<?>> superManifests = new HashMap<>();
    private final Map<Class<? extends Annotation>, List<AnnotatedElement>> annotatedTargets = new HashMap<>();
    private final Map<String, Method> methods = new HashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Set<AnnotatedElement> aunotamatedTargets = new LinkedHashSet<>();
    public Set<AnnotatedElement> getAunotamatedTargets(){
        return aunotamatedTargets;
    }


    private void addAunotamatedTarget(AnnotatedElement element){
        aunotamatedTargets.add(element);
    }
    public Set<ClassManifest<?>> getInterfaceManifests(){
        Set<ClassManifest<?>> set = new HashSet<>();
        for (ClassManifest<?> value : superManifests.values()) {
            if(value.getTargetClass().isInterface()){
                set.add(value);
            }
        }
        return set;
    }
    public Set<ClassManifest<?>> getSuperclassManifests(){
        Set<ClassManifest<?>> set = new HashSet<>();
        for (ClassManifest<?> value : superManifests.values()) {
            if(!value.getTargetClass().isInterface()){
                set.add(value);
            }
        }
        return set;
    }
    public Annotation[] getAnnotations(){
        return cls.getAnnotations();
    }
    public void forSuperManifests(Consumer<ClassManifest<?>> cons){
        for (ClassManifest<?> value : superManifests.values()) {
            cons.accept(value);
        }
    }
    private void processMethod(Method m){
        handleAnnotations(m);
        methods.put(m.getName(),m);
    }
    private void processField(Field f){
        handleAnnotations(f);
        fields.put(f.getName(),f);
    }
    private void processConstructor(Constructor<T> c){
        handleAnnotations(c);
    }
    private void handleAnnotations(AnnotatedElement elem){
        for (Annotation annotation : elem.getAnnotations()) {
            if(Aunotamation.hasProcessor(annotation.annotationType())){
                addAunotamatedTarget(elem);
            }
            List<AnnotatedElement> members = annotatedTargets.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>());
            members.add(elem);
        }
    }
    public Map<Class<? extends Annotation>,List<AnnotatedElement>> getAnnotatedTargets(){
        return annotatedTargets;
    }

    public <T extends Annotation> List<AnnotatedElement> getMembersWithAnnotation(Class<T> annotation){
        return annotatedTargets.getOrDefault(annotation,new ArrayList<>());
    }
    public Method getMethod(String name){
        return methods.get(name);
    }
    public Field getField(String field){
        return fields.get(field);
    }
    public Class<T> getTargetClass(){
        return cls;
    }


    public Collection<Method> getMethods() {
        return methods.values();
    }
}
