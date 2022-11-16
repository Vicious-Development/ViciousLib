package com.vicious.viciouslib.persistence.storage;

import java.util.Collection;
import java.util.Map;

public interface Persistent {
    default Collection<String> getKeys(){
        return getMap().keySet();
    }
    default PersistentAttribute<?> get(String key){
        return getMap().get(key);
    }
    default void put(String key, PersistentAttribute<?> value){
        getMap().putIfAbsent(key,value);
    }
    void save();
    void load();
    void setDirty();
    Map<String, PersistentAttribute<?>> getMap();
}
