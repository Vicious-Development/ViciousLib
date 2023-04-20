package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.aunotamation.InvalidAnnotationException;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.parser.VSONMapParser;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.persistence.vson.writer.VSONWriter;
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
        VSONMap map = loadVSON(getPath(o));
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
            throw new RuntimeException("Failed to load a VSON object.",failure);
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

    private static void load(ClassManifest<?> manifest, VSONMap map, Object o, boolean isStatic) {
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        List<AnnotatedElement> listeners = manifest.getMembersWithAnnotation(OnChanged.class);
        try {
            for (AnnotatedElement member : members) {
                if(member instanceof Field) {
                    Field f = (Field) member;
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
                            if(listener instanceof Method) {
                                Method m = (Method) listener;
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
                            if(listener instanceof Method) {
                                Method m = (Method) listener;
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
            if(annotatedElement instanceof Field){
                Field f = (Field) annotatedElement;
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
        VSONMap out = new VSONMap();
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
            throw new RuntimeException("Failed to save a VSON object.",failure);
        }
        try {
            saveVSON(path, out);
        } catch (FileNotFoundException ignored){}
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static void save(ClassManifest<?> manifest, VSONMap out, Object o, boolean isStatic) {
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        for (AnnotatedElement member : members) {
            if(member instanceof Field){
                Field f = (Field) member;
                if(!isValid(f,isStatic)){
                    continue;
                }
                Save save = member.getAnnotation(Save.class);
                String name = save.value();
                if(name.isEmpty()){
                    name = f.getName();
                }
                try {
                    out.put(name,new VSONMapping(f.get(o),new AnnotationAttrInfo(save)));
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    public static void saveVSON(String path, VSONMap out) throws IOException {
        new VSONWriter(path).write(out);
    }

    public static VSONMap loadVSON(String path){
        try {
            VSONMapParser parser = new VSONMapParser(path);
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
