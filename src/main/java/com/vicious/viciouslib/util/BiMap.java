package com.vicious.viciouslib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class BiMap<V1,V2> implements Map<V1,V2>{
    private final Map<V1,V2> keyValue = new HashMap<>();
    private final Map<V2,V1> valueKey = new HashMap<>();


    public int size() {
        return keyValue.size();
    }

    public boolean isEmpty() {
        return keyValue.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyValue.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return valueKey.containsKey(value);
    }

    @Override
    public V2 get(Object key) {
        return keyValue.get(key);
    }

    public V2 put(V1 v1, V2 v2){
       V2 out = keyValue.put(v1,v2);
       valueKey.put(v2,v1);
       return out;
    }

    @Override
    public V2 remove(Object key) {
        V2 out = keyValue.remove(key);
        if(out != null){
            valueKey.remove(out);
        }
        return out;
    }

    @Override
    public void putAll(Map<? extends V1, ? extends V2> m) {
        m.forEach(this::put);
    }

    public boolean removeByKey(V1 v1) {
        V2 v2 = keyValue.remove(v1);
        if(v2 == null){
            return false;
        }
        return valueKey.remove(v2) != null;
    }
    public boolean removeByValue(V2 v2) {
        V1 v1 = valueKey.remove(v2);
        if(v1 == null){
            return false;
        }
        return keyValue.remove(v1) != null;
    }

    public void clear() {
        valueKey.clear();
        keyValue.clear();
    }

    public Set<V1> keySet(){
        return keyValue.keySet();
    }

    @Override
    public Collection<V2> values() {
        return keyValue.values();
    }

    @Override
    public Set<Entry<V1, V2>> entrySet() {
        return keyValue.entrySet();
    }

    public Set<V2> valueSet(){
        return valueKey.keySet();
    }
    public void forEach(BiConsumer<? super V1,? super V2> consumer){
        keyValue.forEach(consumer);
    }

    public V2 getByKey(V1 name) {
        return keyValue.get(name);
    }

    public V1 getByValue(V2 name) {
        return valueKey.get(name);
    }
}
