package com.vicious.viciouslib.persistence.vson;

import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.util.quick.ObjectList;
import com.vicious.viciouslib.util.quick.ObjectMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class VSONMap extends LinkedHashMap<String, VSONMapping> {
    public static VSONMap from(ObjectMap map) {
        VSONMap vmap = new VSONMap();
        //for (Object o : map.keySet()) {
        //    System.out.println(o + " : " + map.get(o));
       // }
        for (Map.Entry<Object, Object> objectObjectEntry : map.entrySet()) {
            if(objectObjectEntry.getValue() instanceof ObjectMap){
                vmap.put(objectObjectEntry.getKey().toString(),from((ObjectMap) objectObjectEntry.getValue()));
            }
            else if(objectObjectEntry.getValue() instanceof ObjectList){
                vmap.put(objectObjectEntry.getKey().toString(),VSONArray.from((ObjectList) objectObjectEntry.getValue()));
            }
            else{
                vmap.put(objectObjectEntry.getKey().toString(),objectObjectEntry.getValue());
            }
        }
        return vmap;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, VSONMapping> entry : entrySet()) {
            out.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue().get());
        }
        return out.toString();
    }
    public void put(String key, Object o){
        put(key, new VSONMapping(o));
    }
}
