package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.aunotamation.InvalidAnnotationException;
import com.vicious.viciouslib.persistence.storage.AnnotationAttrInfo;
import com.vicious.viciouslib.persistence.storage.AttrInfo;
import com.vicious.viciouslib.persistence.storage.AttributeModificationEvent;
import com.vicious.viciouslib.persistence.storage.aunotamations.*;
import com.vicious.viciouslib.persistence.vson.SerializationHandler;
import com.vicious.viciouslib.persistence.vson.VSONArray;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.parser.VSONMapParser;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;
import com.vicious.viciouslib.persistence.vson.writer.VSONWriter;
import com.vicious.viciouslib.util.ClassAnalyzer;
import com.vicious.viciouslib.util.reflect.ClassManifest;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PersistenceHandler {
    public static void load(Object o){
        VSONMap map = loadVSON(getPath(o));
        load(o,map);
    }

    private static void load(Object o, VSONMap map){
        if(o == null){
            return;
        }
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = getClassOf(o);
        if(map == null || map.isEmpty()){
            return;
        }
        if(map.containsKey("META-MAP")){
            if(o instanceof IPersistent.Metaful) {
                ((IPersistent.Metaful) o).loadMeta(map.get("META-MAP").softAs(VSONMap.class));
            }
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

    public static boolean isInvalid(Member m, boolean isStatic){
        if(!isStatic && Modifier.isStatic(m.getModifiers())){
            return true;
        }
        return isStatic && !Modifier.isStatic(m.getModifiers());
    }

    private static void load(ClassManifest<?> manifest, VSONMap map, Object o, boolean isStatic) {
        if(o == null) {
            o = SerializationHandler.initialize(manifest.getTargetClass());
        }
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        List<AnnotatedElement> listeners = manifest.getMembersWithAnnotation(OnChanged.class);
        try {
            for (AnnotatedElement member : members) {
                if(member instanceof Field) {
                    Field f = (Field) member;
                    if(isInvalid(f, isStatic)){
                        continue;
                    }
                    Save save = member.getAnnotation(Save.class);
                    String name = save.value();
                    if(name.isEmpty()){
                        name = f.getName();
                    }
                    if(map.containsKey(name)) {
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
                        load(name,o, f, map);
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

    /**
     * Loads a value from a VSONMap.
     *
     * @param name Name of the object in the parent map.
     * @param o The parent object.
     * @param map The parent map.
     * @param f The Field containing the object being loaded.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static void load(String name, Object o, Field f, VSONMap map){
        Class<?> type = f.getType();
        try {
            //The object being loaded.
            Object internal = f.get(o);
            //Contexts.
            boolean isStatic = type == Class.class;
            ClassManifest<?> manif = ClassAnalyzer.analyzeClass(isStatic ? (Class)internal : type);
            boolean isSavable = !manif.getMembersWithAnnotation(Save.class).isEmpty();
            //If the object is a persistent object load from the map.
            if(isSavable){
                VSONMap mapRepr = map.get(name).softAs(VSONMap.class);
                //Set field to initialized value if the map representation is not empty.
                if(internal == null && !mapRepr.isEmpty()){
                    internal = initField(f,o);
                }
                load(internal,mapRepr);
            }
            else{
                Typing typing = f.getAnnotation(Typing.class);
                //If the object has typing handle here.
                if(typing != null){
                    Class<?>[] types = typing.value();
                    //Attempt initialization. This may throw an exception in some cases, but in those it will not be my fault.
                    //Example fail case: Map<?,?> m = null. Interface map can't be instantiated. Simple fix: HashMap<?,?> m = null. Will instantiate the map.
                    if(internal == null){
                        initField(f,o);
                    }
                    if(Map.class.isAssignableFrom(type)){
                        unmapMap(types,map,name,internal);
                    }
                    else if(Collection.class.isAssignableFrom(type)){
                        unmapCollection(types,map,name,internal);
                    }
                    else{
                        throw new InvalidAnnotationException("Typing on " + f + " is not allowed. Typing is only used for Maps and Collections.");
                    }
                }
                else{
                    f.set(o, map.get(name).softAs(f.getType()));
                }
            }
        } catch (IllegalAccessException e) {
            throw new InvalidAnnotationException("Must be public.",e);
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void unmapMap(Class<?>[] types, VSONMap map, String name, Object internal){
        VSONMap mapping = map.get(name).softAs(VSONMap.class);
        Class<?> keyType = types[0];
        Class<?> valueType = types[1];
        Map val = (Map) internal;
        val.clear();
        boolean isImplicit = !ClassAnalyzer.analyzeClass(valueType).getMembersWithAnnotation(Save.class).isEmpty();
        for (String key : mapping.keySet()) {
            Object k = SerializationHandler.deserialize(key,keyType);
            VSONValue vson = mapping.get(key);
            val.put(k,unmap(valueType,vson,isImplicit));
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void unmapCollection(Class<?>[] types, VSONMap map, String name, Object internal){
        VSONArray mapping = map.get(name).softAs(VSONArray.class);
        Class<?> valueType = types[0];
        boolean isImplicit = !ClassAnalyzer.analyzeClass(valueType).getMembersWithAnnotation(Save.class).isEmpty();
        Collection val = (Collection) internal;
        val.clear();
        for (VSONValue vson : mapping) {
            val.add(unmap(valueType, vson, isImplicit));
        }
    }

    private static Object initField(Field f, Object obj){
        Object out = SerializationHandler.initialize(f.getType());
        try {
            f.set(obj,out);
        } catch (IllegalAccessException e) {
            throw new InvalidAnnotationException(e);
        }
        return out;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static <V> V unmap(Class<V> type, VSONValue vson, boolean savable){
        if(vson.isNull()){
            return null;
        }
        if(savable){
            V out = null;
            if(vson.isType(VSONMap.class)){
                VSONMap m = vson.softAs(VSONMap.class);
                boolean isEnum = m.containsKey("E-NAME");
                if(isEnum){
                    out = (V) Enum.valueOf((Class<? extends Enum>)type,m.remove("E-NAME").softAs(String.class));
                }
                else{
                    out = SerializationHandler.initialize(type);
                }
                load(ClassAnalyzer.getManifest(type), vson.softAs(VSONMap.class), out, out instanceof Class<?>);
            }
            if(out == null){
                out = SerializationHandler.initialize(type);
            }
            return out;
        }
        else{
            return vson.softAs(type);
        }
    }

    @Nonnull
    public static String getPath(Object o){
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = getClassOf(o);
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

    private static String getPath(ClassManifest<?> manifest, Object o, boolean isStatic){
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
        Class<?> cls = getClassOf(o);
        String path = getPath(o);
        //Don't load already present objects if the file is load only.
        if(isLoadOnly(cls) && new File(path).exists()){
            try(FileInputStream fis = new FileInputStream(path)){
                if(fis.available() > 0){
                    return;
                }
            } catch (IOException ignored) {}
        }
        VSONMap out = toMap(o);
        try {
            saveVSON(path, out);
        } catch (FileNotFoundException ignored){}
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static VSONMap toMap(Object o){
        boolean isStatic = o instanceof Class<?>;
        Class<?> cls = getClassOf(o);
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
        if(o instanceof IPersistent.Metaful){
            out.put("META-MAP", ((IPersistent.Metaful) o).getMetaMap());
        }
        if(failure != null){
            throw new RuntimeException("Failed to save a VSON object.",failure);
        }
        return out;
    }

    private static void save(ClassManifest<?> manifest, VSONMap out, Object o, boolean isStatic) {
        List<AnnotatedElement> members = manifest.getMembersWithAnnotation(Save.class);
        for (AnnotatedElement member : members) {
            if(member instanceof Field){
                Field f = (Field) member;
                if(isInvalid(f, isStatic)){
                    continue;
                }
                Save save = member.getAnnotation(Save.class);
                String name = save.value();
                if(name.isEmpty()){
                    name = f.getName();
                }
                saveField(f,o,out,name,save);
            }
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void saveField(Field f, Object o, VSONMap out, String name, Save save){
        try {
            Object internal = f.get(o);
            if (internal != null && !ClassAnalyzer.analyzeClass(internal instanceof Class ? (Class)internal : internal.getClass()).getMembersWithAnnotation(Save.class).isEmpty()) {
                out.put(name,map(internal,true).asMapping(new AnnotationAttrInfo(save)));
            }
            else if (f.isAnnotationPresent(Typing.class)){
                saveHardTyped(f,internal,out,save,name);
            }
            else {
                out.put(name, new VSONMapping(internal, new AnnotationAttrInfo(save)));
            }
        } catch (IllegalAccessException e) {
            throw new InvalidAnnotationException(e);
        }
    }

    private static void saveHardTyped(Field f, Object internal, VSONMap out, Save save, String name){
        Typing typing = f.getAnnotation(Typing.class);
        Class<?>[] types = typing.value();
        if(internal == null){
            out.put(name, new VSONMapping(null,new AnnotationAttrInfo(save)));
        }
        else {
            Class<?> type = internal.getClass();
            if (Map.class.isAssignableFrom(type)) {
                out.put(name,mapMap(types,internal).asMapping(new AnnotationAttrInfo(save)));
            } else if (Collection.class.isAssignableFrom(type)) {
                out.put(name,mapCollection(types,internal).asMapping(new AnnotationAttrInfo(save)));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static VSONValue mapMap(Class<?>[] types, Object internal){
        Class<?> valType = types[1];
        boolean isImplicit = !ClassAnalyzer.analyzeClass(valType).getMembersWithAnnotation(Save.class).isEmpty();
        VSONMap otherobj = new VSONMap();
        Map val = (Map) internal;
        for (Object key : val.keySet()) {
            String k = key instanceof String ? (String) key : SerializationHandler.serialize(key);
            otherobj.put(k,map(val.get(key),isImplicit).asMapping(AttrInfo.EMPTY));
        }
        return new VSONValue(otherobj);
    }

    @SuppressWarnings("rawtypes")
    private static VSONValue mapCollection(Class<?>[] types, Object internal){
        Class<?> valType = types[0];
        boolean isImplicit = !ClassAnalyzer.analyzeClass(valType).getMembersWithAnnotation(Save.class).isEmpty();
        VSONArray otherobj = new VSONArray();
        Collection val = (Collection) internal;
        for (Object value : val) {
            otherobj.add(map(value,isImplicit));
        }
        return new VSONValue(otherobj);
    }

    private static VSONValue map(Object value, boolean savable){
        boolean isStatic = value instanceof Class;
        if(savable){
            VSONMap m = new VSONMap();
            save(ClassAnalyzer.getManifest(getClassOf(value)), m, value, isStatic);
            if(value instanceof Enum){
                if(m.containsKey("E-NAME")){
                    throw new InvalidAnnotationException("Savable Enum class is using a reserved data name 'E-NAME' this is reserved for the persistence handler, rename your data field.");
                }
                m.put("E-NAME",((Enum<?>) value).name());
            }
            return new VSONValue(m);
        }
        else{
            return new VSONValue(value);
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

    public static Class<?> getClassOf(Object value){
        if(value instanceof Class<?>){
            return (Class<?>) value;
        }
        else if(value instanceof Enum<?>){
            return ((Enum<?>) value).getDeclaringClass();
        }
        else{
            return value.getClass();
        }
    }

    public static void init(Object object) {
        load(object);
        save(object);
    }
}
