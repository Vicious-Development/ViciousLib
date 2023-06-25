package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.jarloader.event.MainEntry;
import com.vicious.viciouslib.util.ClassAnalyzer;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.List;

public class JarInstance<T> {
    private final MainEntry anno;
    private final Class<T> mainClass;
    private final Constructor<?> constructor;
    private final File file;
    private T mainInstance;
    public void setMainInstance(Object inst){
        if(mainInstance == null ){
            mainInstance= (T) inst;
        }
        else{
            LoggerWrapper.logError("Attempted to set main instance a second time! This is unnecessary.");
        }
    }

    public JarInstance(File file, Class<T> mainClass) {
        this.mainClass = mainClass;
        List<AnnotatedElement> elements = ClassAnalyzer.analyzeClass(mainClass).getMembersWithAnnotation(MainEntry.class);
        if(elements.size() > 1){
            throw new RuntimeException(mainClass.getName() + " has more than one element annotated with @MainEntry.");
        }
        if(elements.size() == 0){
            throw new RuntimeException(mainClass.getName() + " is missing an @MainEntry constructor");
        }
        else{
            if(elements.get(0) instanceof Constructor<?> c){
                this.constructor=c;
                this.anno = c.getAnnotation(MainEntry.class);
            }
            else{
                throw new RuntimeException(mainClass.getName() + " has an element marked with @MainEntry, but it isn't a constructor");
            }
        }
        this.file = file;
    }
    public String[] getLoadAfter(){
        return getAnno().loadAfter();
    }

    public MainEntry getAnno() {
        return anno;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public T getMainInstance(){
        return mainInstance;
    }
    public Class<T> getMain(){
        return mainClass;
    }
    public File getJarFile(){
        return file;
    }
}
