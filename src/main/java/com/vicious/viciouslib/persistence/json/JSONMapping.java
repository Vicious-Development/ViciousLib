package com.vicious.viciouslib.persistence.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONMapping extends JSONValue{
    public JSONMapping(Object mapping, String valueString){
        super(mapping);
        this.valueString=valueString;
    }
    public JSONMapping(Object mapping){
        super(mapping);
        this.valueString= mapping == null ? "null" : mapping.toString();
    }

    @Override
    public String toString() {
        if(value == null){
            return "EmptyJSONMapping";
        }
        else{
            return "JSONMapping{class = " + value.getClass().getName() + ", value = " + value + ", serialized form = " + valueString + "}";
        }
    }

    public static class Persistent extends JSONMapping {
        public final String description;
        public boolean hasParent = false;
        public List<Map.Entry<String,Persistent>> children = new ArrayList<>();
        public Persistent(Object o, String description) {
            super(o);
            this.description=description;
        }
        public void addChild(Map.Entry<String,Persistent> p){
            this.children.add(p);
        }

        public boolean hasParent() {
            return hasParent;
        }
    }
}
