package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.jarloader.event.EventPhase;
import com.vicious.viciouslib.jarloader.event.InitializationEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ViciousJarLoader {
    private static ViciousJarLoader instance;
    public static ViciousJarLoader getInstance(){
        if(instance == null) instance = new ViciousJarLoader();
        return instance;
    }
    private ViciousJarLoader(){}

    private final Map<Class<?>,JarInstance<?>> instances = new HashMap<>();

    public Set<JarInstance<?>> getJarInstances(){
        return new HashSet<>(instances.values());
    }

    /**
     * Do this to initialize all main classes of instances scanned.
     */
    public void sendInitialization(){
        InitializationEvent IE = new InitializationEvent(EventPhase.BEFORE);
        IE.post();
        for (Object o : IE.getReturned()) {
            Class<?> main = o.getClass();
            if(instances.containsKey(main)){
                instances.get(main).setMainInstance(o);
            }
        }
    }

    public boolean isViciousJar(File file) throws IOException {
        ZipFile zip = new ZipFile(file);
        ZipEntry jarInfo = zip.getEntry("mainentry.info");
        zip.close();
        return jarInfo != null;
    }
    /**
     * Gets the main class for the jar file by locating 'mainentry.info'
     * Mainentry only contains a single line with the canonical class name for the main class.
     * This main class should have at least one @EventInterceptor annotated method otherwise the class is basically useless.
     * This EventInterceptor method of course should take in an initialization event as a parameter, which depends on the runtime environment.
     * An example of this would be ForgeModLoader's FMLInitialization event, although FML does not use VJL to handle mod loading.
     */
    public static Class<?> getMainClass(File file) throws ClassNotFoundException, IOException {
        ZipFile zip = new ZipFile(file);
        ZipEntry jarInfo = zip.getEntry("mainentry.info");
        if(jarInfo == null) return null;
        InputStream infoStream = zip.getInputStream(jarInfo);
        Scanner scan = new Scanner(infoStream);
        String mainClassName = scan.nextLine();
        scan.close();
        zip.close();
        URLClassLoader child = new URLClassLoader(
                new URL[] {file.toURI().toURL()},
                ViciousJarLoader.class.getClassLoader()
        );
        return Class.forName(mainClassName,true,child);
    }

    /**
     * Creates a JarInstance if possible using the
     * @param file
     * @return
     */
    public JarInstance<?> loadJar(File file) throws IOException, ClassNotFoundException {
        Class<?> main = getMainClass(file);
        if(main != null) {
            if(!instances.containsKey(main)) {
                JarInstance<?> ji = new JarInstance<>(file, main);
                instances.put(main, ji);
                Aunotamation.processObject(main);
                return ji;
            }
            else{
                LoggerWrapper.logError("Attempted to load the same jar main class " + main.getCanonicalName() + " multiple times! ");
            }
        }
        else{
            LoggerWrapper.logError("Could not find main class for file: " + file + " make sure your jar has a valid 'mainentry.info' file");
        }
        return null;
    }

    public void scanDirectoryForJars(String path) throws Exception {
        File f = new File(path);
        if(f.exists() && f.isDirectory()){
            for (File file : f.listFiles()) {
                if(file.getName().endsWith(".jar")){
                    if(isViciousJar(file)){
                        loadJar(file);
                    }
                }
            }
        }
    }
}
