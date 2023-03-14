package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.aunotamation.InvalidAnnotationException;
import com.vicious.viciouslib.persistence.json.JSONMap;
import com.vicious.viciouslib.persistence.json.parser.JSONMapParser;
import com.vicious.viciouslib.persistence.json.value.JSONMapping;
import com.vicious.viciouslib.persistence.json.writer.JSONWriter;
import com.vicious.viciouslib.persistence.storage.AnnotationAttrInfo;
import com.vicious.viciouslib.persistence.storage.AttributeModificationEvent;
import com.vicious.viciouslib.persistence.storage.aunotamations.LoadOnly;
import com.vicious.viciouslib.persistence.storage.aunotamations.OnChanged;
import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.util.ClassAnalyzer;
import com.vicious.viciouslib.util.reflect.ClassManifest;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PersistenceHandler {
    public static void load(Object o){
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = isStatic ? (Class<?>) o : o.getClass();
        JSONMap map = loadJSON(getPath(o));
        if(map == null || map.isEmpty()){
            return;
        }
        Throwable failure = DeepReflection.cycleAndExecute(cls,c->{
            ClassManifest<?> manifest = ClassAnalyzer.analyzeClass(c);
            try {
                load(manifest, map, o, isStatic);
            } catch (Throwable e){
                return e;
            }
            return null;
        });
        if(failure != null){
            throw new RuntimeException("Failed to load a JSON object.",failure);
        }
    }

    public static boolean isValid(Member m, boolean isStatic){
        if(!isStatic && Modifier.isStatic(m.getModifiers())){
            return false;
        }
        if(isStatic && !Modifier.isStatic(m.getModifiers())){
            return false;
        }
        return true;
    }

    private static void load(ClassManifest<?> manifest, JSONMap map, Object o, boolean isStatic) {
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        List<AnnotatedElement> listeners = manifest.getMembersWithAnnotation(OnChanged.class);
        try {
            for (AnnotatedElement member : members) {
                if(member instanceof Field f) {
                    if(!isValid(f,isStatic)){
                        continue;
                    }
                    Save save = member.getAnnotation(Save.class);
                    String name = save.value();
                    if(name.isEmpty()){
                        name = f.getName();
                    }
                    if(map.containsKey(name)) {
                        Object value = map.get(name).softAs(f.getType());
                        for (AnnotatedElement listener : listeners) {
                            if(listener instanceof Method m) {
                                if(!isStatic && Modifier.isStatic(m.getModifiers())){
                                    continue;
                                }
                                if(isStatic && !Modifier.isStatic(m.getModifiers())){
                                    continue;
                                }
                                m.invoke(o,new AttributeModificationEvent(false,f.get(o)));
                            }
                        }
                        f.set(o, value);
                        for (AnnotatedElement listener : listeners) {
                            if(listener instanceof Method m) {
                                if(!isStatic && Modifier.isStatic(m.getModifiers())){
                                    continue;
                                }
                                if(isStatic && !Modifier.isStatic(m.getModifiers())){
                                    continue;
                                }
                                m.invoke(o,new AttributeModificationEvent(true,f.get(o)));
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    private static String getPath(Object o){
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = isStatic ? (Class<?>) o : o.getClass();
        AtomicReference<RuntimeException> thrown = new AtomicReference<>();
        String path = DeepReflection.cycleAndExecute(cls,c->{
            ClassManifest<?> manifest = ClassAnalyzer.analyzeClass(c);
            try {
                return getPath(manifest, o, isStatic);
            } catch (RuntimeException e){
                if(thrown.get() == null){
                    thrown.set(e);
                }
                return null;
            }
        });
        if(path == null){
            throw thrown.get();
        }
        else{
            return path;
        }
    }



    public static String getPath(ClassManifest<?> manifest, Object o, boolean isStatic){
        List<AnnotatedElement> persistentPath = manifest.getMembersWithAnnotation(PersistentPath.class);
        List<Field> valid = new ArrayList<>();
        for (AnnotatedElement annotatedElement : persistentPath) {
            if(annotatedElement instanceof Field f){
                if(isStatic && Modifier.isStatic(f.getModifiers())){
                    valid.add(f);
                }
                if(!isStatic && !Modifier.isStatic(f.getModifiers())){
                    valid.add(f);
                }
            }
        }
        if(valid.isEmpty()){
            throw new InvalidAnnotationException("The object provided lacks an annotated @PersistentPath Field. This tells the handler where to load and save the file.\nClass: " + o.getClass().getCanonicalName());
        }
        else if(valid.size() > 2){
            throw new InvalidAnnotationException("The object provided has " + persistentPath.size() + " @PersistentPath Fields. Can only have 1");
        }
        else {
            try {
                String path = (String) valid.get(0).get(o);
                if (path == null) {
                    throw new NullPointerException("@PersistentPath field is not initialized yet. Field: " + valid.get(0).getClass().getCanonicalName() + "." + valid.get(0).getName());
                }
                return path;
            } catch (IllegalAccessException ignored) {}
        }
        return "";
    }

    public static boolean isLoadOnly(Class<?> cls){
        Boolean out = DeepReflection.cycleAndExecute(cls,c->{
            if(c.isAnnotationPresent(LoadOnly.class)){
                return true;
            }
            else{
                return null;
            }
        });
        if(out == null){
            return false;
        }
        return out;
    }

    public static void save(Object o){
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = isStatic ? (Class<?>) o : o.getClass();
        String path = getPath(o);
        //Don't load already present objects if the file is load only.
        if(isLoadOnly(cls) && new File(path).exists()){
            return;
        }
        JSONMap out = new JSONMap();
        Throwable failure = DeepReflection.cycleAndExecute(cls,c->{
            ClassManifest<?> manifest = ClassAnalyzer.analyzeClass(c);
            try {
                save(manifest, out, o, isStatic);
            } catch (Throwable e){
                return e;
            }
            return null;
        });
        if(failure != null){
            throw new RuntimeException("Failed to save a JSON object.",failure);
        }
        try {
            saveJSON(path, out);
        } catch (FileNotFoundException ignored){}
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static void save(ClassManifest<?> manifest, JSONMap out, Object o, boolean isStatic) {
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        for (AnnotatedElement member : members) {
            if(member instanceof Field f){
                if(!isValid(f,isStatic)){
                    continue;
                }
                Save save = member.getAnnotation(Save.class);
                String name = save.value();
                if(name.isEmpty()){
                    name = f.getName();
                }
                try {
                    out.put(name,new JSONMapping(f.get(o),new AnnotationAttrInfo(save)));
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    public static void saveJSON(String path, JSONMap out) throws IOException {
        new JSONWriter(path).write(out);
    }

    public static JSONMap loadJSON(String path){
        try {
            JSONMapParser parser = new JSONMapParser(path);
            return parser.getMap();
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    public static void init(Object object) {
        load(object);
        save(object);
    }
}
