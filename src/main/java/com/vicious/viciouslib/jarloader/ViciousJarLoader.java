package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.jarloader.event.EventPhase;
import com.vicious.viciouslib.jarloader.event.InitializationEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ViciousJarLoader {
    private LoaderStage stage = LoaderStage.DORMANT;
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
        while (stage == LoaderStage.LOADING){}
        stage = LoaderStage.INITIALIZING;
        Set<JarInstance<?>> instances = new HashSet<>(getJarInstances());
        while(!instances.isEmpty()) {
            Set<JarInstance<?>> loaded = new HashSet<>();
            l1: for (JarInstance<?> jarInstance : instances) {
                for (String cls : jarInstance.getLoadAfter()) {
                    try {
                        Class<?> mc = Class.forName(cls,false,viciousLoader);
                        if (this.instances.get(mc).getMainInstance() == null) {
                            continue l1;
                        }
                    } catch (ClassNotFoundException e){
                        throw new RuntimeException(jarInstance.getMain() + " is missing required dependency containing " + cls);
                    }
                }
                try {
                    Class<?> main = jarInstance.getMain();
                    LoggerWrapper.logInfo("Initializing " + main.getName());
                    Constructor<?> cons = main.getConstructor(InitializationEvent.class);
                    jarInstance.setMainInstance(cons.newInstance(new InitializationEvent(false)));
                    loaded.add(jarInstance);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize a main class.", e);
                }
            }
            instances.removeAll(loaded);
        }
        stage = LoaderStage.INITIALIZED;
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
        return Class.forName(mainClassName,true,viciousLoader);
    }

    /**
     * Creates a JarInstance if possible using the
     * @param file
     * @return
     */
    public JarInstance<?> loadJar(File file) throws IOException, ClassNotFoundException {
        Class<?> main = getMainClass(file);
        if(main != null) {
            LoggerWrapper.logInfo("Loaded main class " + main.getName());
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

    public static ViciousClassLoader viciousLoader;

    static {
        viciousLoader = new ViciousClassLoader(ViciousClassLoader.class.getClassLoader());
    }

    public void scanDirectoryForJars(String path) throws Exception {
        stage = LoaderStage.LOADING;
        File f = new File(path);
        List<File> vjs = new ArrayList<>();
        if(f.exists() && f.isDirectory()){
            for (File file : f.listFiles()) {
                if(file.getName().endsWith(".jar")){
                    if(isViciousJar(file)){
                        vjs.add(file);
                    }
                }
            }
        }
        for (File vj : vjs) {
            LoggerWrapper.logInfo("Adding " + vj.getName() + " to the classpath.");
            viciousLoader.addURL(vj.toURI().toURL());
        }
        for (File vj : vjs) {
            loadJar(vj);
        }
        stage = LoaderStage.LOADED;
    }

    public LoaderStage getStage() {
        return stage;
    }
}
