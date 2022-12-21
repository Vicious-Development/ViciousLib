package com.vicious.viciouslib.persistence.json;

import com.vicious.viciouslib.persistence.storage.PersistentObject;
import com.vicious.viciouslib.persistence.storage.PersistentAttribute;

import java.io.FileNotFoundException;
import java.util.Map;

public class JSONFile extends PersistentObject {
    private String path;
    public JSONFile(String path){
        this.path=path;
    }

    @Override
    public void save() {
        JSONWriter writer = new JSONWriter(path);
        JSONMap map = new JSONMap();
        for (Map.Entry<String, PersistentAttribute<?>> entry : getMap().entrySet()) {
            String name = entry.getKey();
            PersistentAttribute<?> value = entry.getValue();
            JSONMapping.Persistent mapping = new JSONMapping.Persistent(value.get(),value.getDescription().isEmpty() ? null : value.getDescription());
            map.put(name, mapping);
        }
        for (Map.Entry<String, PersistentAttribute<?>> entry : getMap().entrySet()) {
            String name = entry.getKey();
            PersistentAttribute<?> value = entry.getValue();
            JSONMapping.Persistent mapping = (JSONMapping.Persistent) map.get(name);
            if(value.hasParent()) {
                if(map.get(value.getParent().name()) instanceof JSONMapping.Persistent p){
                    p.addChild(Map.entry(name,mapping));
                    mapping.hasParent=true;
                }
            }
        }
        try {
            writer.write(map);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(){
        try {
            JSONMapParser parser = new JSONMapParser(path);
            parser.getMap().forEach((k,v)->{
                if(getMap().containsKey(k)) {
                    get(k).fromJSON(v);
                }
            });
        } catch (FileNotFoundException ignored){}
    }
}
