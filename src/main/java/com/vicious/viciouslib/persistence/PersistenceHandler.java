package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.aunotamation.InvalidAnnotationException;
import com.vicious.viciouslib.persistence.storage.AnnotationAttrInfo;
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
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslib.util.reflect.ClassManifest;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PersistenceHandler {
    public static final String SPECIAL_ENUM_KEY = "E-NAME";
    public static final String SPECIAL_TYPE_KEY = "VSONTYPE";

    public static void init(Object o){
        if(!classOf(o).isAnnotationPresent(DontAutoLoad.class)) {
            load(o);
        }
        save(o);
    }

    public static void load(Object o){
        load(o,Context.of(o));
    }

    public static void save(Object o){
        try {
            boolean loadOnly = classOf(o).isAnnotationPresent(LoadOnly.class);
            String path = getPath(o);
            if(Files.exists(Paths.get(path))){
                if(!loadOnly){
                    saveToFile(path,save(o,Context.of(o)));
                }
            }
            else{
                path = FileUtil.resolve(path);
                saveToFile(path,save(o,Context.of(o)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void load(Object o, Context context){
        String path = getPath(o);
        VSONMap map = loadFromFile(path);
        if(map != null) {
            loadObject(o, map, context);
        }
    }

    private static void loadObject(Object o, VSONMap map, Context context){
        if(context.isNonStatic()) {
            Class<?> cls = classOf(o);
            while (cls != null && cls != Class.class) {
                loadObjectOnClass(o, map, context, cls);
                cls = cls.getSuperclass();
            }
        }
        else{
            loadObjectOnClass(o, map, context, classOf(o));
        }
    }

    private static void loadObjectOnClass(Object o, VSONMap map, Context context, Class<?> cls){
        List<AnnotatedElement> listeners = ClassAnalyzer.analyzeClass(classOf(o)).getMembersWithAnnotation(OnChanged.class);
        onSavableFields(cls,context,field->{
            try {
                VSONMapping mapping = map.get(getName(field));
                if (mapping != null) {
                    Class<?> type = field.getType();
                    Context fieldContext = Context.of(type);
                    if (fieldContext.isStatic()) {
                        Object v = field.get(o);
                        if(v == null){
                            throw new InvalidAnnotationException("Cannot have @Save on Field of type Class with null value.");
                        }
                        loadObjectOnClass(v, mapping.softAs(VSONMap.class), Context.STATIC, (Class<?>) v);
                    } else {
                        onChange(listeners, o, false, field, context);
                        field.set(o, unmap(type, field.getAnnotation(Typing.class), mapping, field.get(o), field.isAnnotationPresent(Unmapped.class), field.isAnnotationPresent(Mapped.class), 0));
                        onChange(listeners, o, true, field, context);

                    }
                }
            } catch (IllegalAccessException e) {
                throw new InvalidAnnotationException(e);
            }
        });
    }

    private static void onChange(List<AnnotatedElement> listeners, Object o, boolean done, Field field, Context context){
        for (AnnotatedElement listener : listeners) {
            if (listener instanceof Method) {
                Method m = (Method) listener;
                if (context.isNonStatic() && Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                if (context.isStatic() && !Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                try {
                    m.invoke(o, new AttributeModificationEvent(done, field.get(o)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static Object unmap(Class<?> type, Typing typing, VSONValue mapping, Object defaultValue, boolean forceUnmapped, boolean forceMapped, int ord){
        if(typing != null && typing.value().length <= ord){
            typing=null;
        }
        if(typing != null){
            if(mapping.get() instanceof VSONMap){
                if(type == VSONMap.class){
                    return mapping.get();
                }
                Map map = (Map<?, ?>) defaultValue;
                if(map == null){
                    map = (Map<?, ?>) SerializationHandler.initialize(type);
                }
                map.clear();
                VSONMap map1 = mapping.softAs(VSONMap.class);
                Class<?> keyType = typing.value()[ord];
                Class<?> valType = typing.value()[ord+1];
                for (String key : map1.keySet()) {
                    map.put(SerializationHandler.deserialize(key,keyType), unmap(valType,typing,map1.get(key),null,forceUnmapped,forceMapped,ord+2));
                }
                return map;
            }
            else if(mapping.get() instanceof VSONArray){
                if(type == VSONArray.class){
                    return mapping.get();
                }
                Collection collection = (Collection) defaultValue;
                if(collection == null){
                    collection = (Collection) SerializationHandler.initialize(type);
                }
                collection.clear();
                Class<?> valType = typing.value()[ord];
                for (VSONValue value : mapping.softAs(VSONArray.class)) {
                    collection.add(unmap(valType,typing,value,null,forceUnmapped,forceUnmapped,ord+1));
                }
                return collection;
            }
        }
        else{
            if((forceUnmapped || !ClassAnalyzer.analyzeClass(type).hasMembersWithAnnotation(Save.class) && Enum.class.isAssignableFrom(type))){
                String name = mapping.softAs(String.class);
                return Enum.valueOf((Class)type,name);
            }
            else if(forceMapped || mapping.get() instanceof VSONMap){
                VSONMap internal = (VSONMap) mapping.get();
                if(Enum.class.isAssignableFrom(type)){
                    String name = internal.get(SPECIAL_ENUM_KEY).softAs(String.class);
                    Enum val = Enum.valueOf((Class)type,name);
                    loadObject(val,internal,Context.NONSTATIC);
                    return val;
                }
                else if(internal.containsKey(SPECIAL_TYPE_KEY)){
                    String key = internal.get(SPECIAL_TYPE_KEY).softAs(String.class);
                    try {
                        type = KeyToClass.get(key);
                        Object source = SerializationHandler.initialize(type);
                        loadObject(source,internal,Context.NONSTATIC);
                        return source;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("No key to class converter registered for " + key + " use KeyToClass.register to provide.");
                    }
                }
                else{
                    Object source = requireNonNull(defaultValue,type);
                    loadObject(source,internal,Context.of(source));
                    return source;
                }
            }
        }
        return mapping.softAs(type);
    }

    private static Object requireNonNull(Object defaultValue, Class<?> type) {
        if(defaultValue == null){
            return SerializationHandler.initialize(type);
        }
        return defaultValue;
    }

    private static String getName(Field field){
        Save anno = field.getAnnotation(Save.class);
        return !anno.value().isEmpty() ? anno.value() : field.getName();
    }

    private static void onSavableFields(Class<?> type, Context context, Consumer<Field> cons){
        ClassManifest<?> manifest = ClassAnalyzer.analyzeClass(type);
        for (AnnotatedElement element : manifest.getMembersWithAnnotation(Save.class)) {
            if(element instanceof Field){
                if(context.matches((Member) element)){
                    if(!((Field) element).isAccessible()){
                        ((Field) element).setAccessible(true);
                    }
                    cons.accept((Field) element);
                }
            }
            else{
                throw new InvalidAnnotationException("@Save can only be applied to Fields.");
            }
        }
    }

    private static VSONMap save(Object o, Context context){
        return saveObject(o,context);
    }

    private static VSONMap saveObject(Object o, Context context){
        VSONMap map = new VSONMap();
        if(context.isNonStatic()) {
            Class<?> cls = classOf(o);
            while (cls != null && cls != Class.class) {
                saveObjectOnClass(o, map, context, cls);
                cls = cls.getSuperclass();
            }
        }
        else{
            saveObjectOnClass(o, map, context, classOf(o));
        }
        return map;
    }

    private static void saveObjectOnClass(Object o, VSONMap map, Context context, Class<?> cls){
        onSavableFields(cls,context,field->{
            try {
                String name = getName(field);
                Class<?> type = field.getType();
                Context fieldContext = Context.of(type);
                if (fieldContext.isStatic()) {
                    VSONMap m = new VSONMap();
                    Object v = field.get(o);
                    if (v == null) {
                        throw new InvalidAnnotationException("Cannot have @Save on Field of type Class with null value.");
                    }
                    saveObjectOnClass(v, m, Context.STATIC, (Class<?>) v);
                    map.put(name, m);
                } else {
                    try {
                        Object output = map(type, field.getAnnotation(Typing.class), field.get(o), field.isAnnotationPresent(Unmapped.class), field.isAnnotationPresent(Mapped.class), 0);
                        VSONMapping mapping = new VSONMapping(output);
                        mapping.info = new AnnotationAttrInfo(field.getAnnotation(Save.class));
                        map.put(name,mapping);
                    } catch (IllegalAccessException e) {
                        throw new InvalidAnnotationException(e);
                    }
                }
            } catch (IllegalAccessException e){
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static Object map(Class<?> type, Typing typing, Object val, boolean forceUnmapped, boolean forceMapped, int ord){
        if(typing != null && typing.value().length <= ord){
            typing=null;
        }
        if(val instanceof VSONMap || val instanceof VSONArray){
            return val;
        }
        if(val instanceof Map){
            VSONMap out = new VSONMap();
            Map m = (Map) val;
            for (Object key : m.keySet()) {
                Object value = m.get(key);
                out.put(key instanceof String ? (String) key : SerializationHandler.serialize(key),map(typing.value()[ord+1],typing,value,forceUnmapped,forceMapped,ord+2));
            }
            return out;
        }
        else if(val instanceof Collection){
            VSONArray out = new VSONArray();
            Collection c = (Collection) val;
            for (Object value : c) {
                out.addObject(map(typing.value()[ord],typing,value,forceUnmapped,forceMapped,ord+1));
            }
            return out;
        }
        else{
            ClassManifest manif = ClassAnalyzer.analyzeClass(type);
            if(val instanceof Enum){
                if(forceUnmapped || !manif.hasMembersWithAnnotation(Save.class)){
                    return ((Enum<?>) val).name();
                }
                else{
                    VSONMap out = save(val,Context.NONSTATIC);
                    out.put(SPECIAL_ENUM_KEY,((Enum<?>) val).name());
                    return out;
                }
            }
            else if(forceMapped || manif.hasMembersWithAnnotation(Save.class)){
                VSONMap map = save(val,Context.of(val));
                if(val.getClass() != type){
                    try {
                        map.put(SPECIAL_TYPE_KEY,KeyToClass.get(val.getClass()));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("No key to class converter registered for " + val.getClass() + " use KeyToClass.register to provide.");
                    }
                }
                return map;
            }
            else{
                return val;
            }
        }
    }

    public static Class<?> classOf(Object o){
        return o instanceof Class<?> ? (Class<?>) o : o.getClass();
    }



    @Nonnull
    public static String getPath(Object o){
        Context ctx = Context.of(o);
        Class<?> cls = classOf(o);
        AtomicReference<RuntimeException> thrown = new AtomicReference<>();
        String path = DeepReflection.cycleAndExecute(cls, c->{
            ClassManifest<?> manifest = ClassAnalyzer.analyzeClass(c);
            try {
                return getPath(manifest, o, ctx);
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

    private static String getPath(ClassManifest<?> manifest, Object o, Context ctx){
        List<AnnotatedElement> persistentPath = manifest.getMembersWithAnnotation(PersistentPath.class);
        List<Field> valid = new ArrayList<>();
        for (AnnotatedElement annotatedElement : persistentPath) {
            if(annotatedElement instanceof Field){
                Field f = (Field) annotatedElement;
                if(ctx.isStatic() && Modifier.isStatic(f.getModifiers())){
                    valid.add(f);
                }
                if(ctx.isNonStatic() && !Modifier.isStatic(f.getModifiers())){
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

    public static void saveToFile(String path, VSONMap out) throws IOException {
        new VSONWriter(path).write(out);
    }

    public static VSONMap loadFromFile(String path){
        try {
            VSONMapParser parser = new VSONMapParser(path);
            return parser.getMap();
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }

    private enum Context {
        STATIC,
        NONSTATIC;

        public boolean isStatic(){
            return this == STATIC;
        }

        public static Context of(Object o){
            return o instanceof Class<?> ? STATIC : NONSTATIC;
        }

        public static Context of(Class<?> cls){
            return cls == Class.class ? STATIC : NONSTATIC;
        }

        public boolean isNonStatic() {
            return this == NONSTATIC;
        }

        public boolean matches(Member element) {
            if(isStatic() && Modifier.isStatic(element.getModifiers())){
                return true;
            }
            return isNonStatic() && !Modifier.isStatic(element.getModifiers());
        }
    }
}
