package com.vicious.viciouslib.util;

import com.vicious.viciouslib.util.reflect.ClassManifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ClassAnalyzer {
    Map<Class<?>,ClassManifest<?>> manifests = new HashMap<>();
    List<ClassAnalyzer> analyzers = new ArrayList<>();
    static void addAnalyzer(ClassAnalyzer al){
        analyzers.add(al);
    }
    static <T> ClassManifest<T> analyzeClass(Class<T> cls){
        ClassManifest<T> manifest;
        if(!manifests.containsKey(cls)) {
            manifest = new ClassManifest<>(cls);
            manifests.put(cls,manifest);
        }
        else{
            manifest = (ClassManifest<T>) manifests.get(cls);
        }
        for (ClassAnalyzer analyzer : analyzers) {
            analyzer.receiveManifest(manifest);
        }
        return manifest;
    }
    static <T> void analyzeObject(Object o){
        ClassManifest<T> manifest;
        Class<T> cls = (Class<T>) o.getClass();
        if(!manifests.containsKey(cls)) {
            manifest = new ClassManifest<>(cls);
        }
        else{
            manifest = (ClassManifest<T>) manifests.get(cls);
        }
        for (ClassAnalyzer analyzer : analyzers) {
            analyzer.receiveObjectManifest(manifest,0);
        }
    }

    static boolean hasAnalyzed(Class<?> cls) {
        return manifests.containsKey(cls);
    }

    static ClassManifest<?> getManifest(Class<?> cls) {
        return manifests.get(cls);
    }

    default <T> void receiveManifest(ClassManifest<T> manifest){}
    <T> void receiveObjectManifest(ClassManifest<T> manifest, Object o);
}
