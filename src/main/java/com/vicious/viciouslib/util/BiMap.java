package com.vicious.viciouslib.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class BiMap<V1,V2> {
    private final Map<V1,V2> keyValue = new HashMap<>();
    private final Map<V2,V1> valueKey = new HashMap<>();


    public int size() {
        return keyValue.size();
    }

    public boolean isEmpty() {
        return keyValue.isEmpty();
    }
    public boolean containsKey(V1 v){
        return keyValue.containsKey(v);
    }
    public boolean containsValue(V2 v){
        return valueKey.containsKey(v);
    }
    public void put(V1 v1, V2 v2){
       keyValue.put(v1,v2);
       valueKey.put(v2,v1);
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
    public Set<V2> valueSet(){
        return valueKey.keySet();
    }
    public void forEach(BiConsumer<V1,V2> consumer){
        keyValue.forEach(consumer);
    }

    public V2 getByKey(V1 name) {
        return keyValue.get(name);
    }

    public V1 getByValue(V2 name) {
        return valueKey.get(name);
    }
}
