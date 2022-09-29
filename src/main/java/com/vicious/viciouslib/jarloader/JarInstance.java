package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.LoggerWrapper;

import java.io.File;

public class JarInstance<T> {
    private final Class<T> mainClass;
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
        this.file = file;
    }
    public Class<T> getMain(){
        return mainClass;
    }
    public File getJarFile(){
        return file;
    }
}
