package com.vicious.viciouslib.persistence.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONMapping{
    private Object mapping;
    private final String valueString;
    public JSONMapping(Object mapping, String valueString){
        this.mapping=mapping;
        this.valueString=valueString;
    }
    public JSONMapping(Object mapping){
        this.mapping=mapping;
        this.valueString= mapping == null ? "null" : mapping.toString();
    }
    public Object get(){
        return mapping;
    }

    /**
     * Requires the class to be of the type provided. This will cause problems if the mapping is of type Double and you try to cast it to Float
     */
    @SuppressWarnings("unchecked")
    public <V> V as(Class<V> cls){
        if(mapping == null){
            return null;
        }
        return cls.cast(mapping);
    }
    @SuppressWarnings("unchecked")
    public <V> V softAs(Class<V> cls){
        try{
            return as(cls);
        } catch (ClassCastException e){
            if(mapping instanceof Number && Number.class.isAssignableFrom(cls)){
                return (V) asN((Class<Number>)cls);
            }
            V v = Deserializer.fix(valueString,cls);
            if(v == null){
                throw new JSONException("Completely failed to parse a JSON value.");
            }
            mapping=v;
            return v;
        }
    }
    @SuppressWarnings("unchecked")
    private <V extends Number> V asN(Class<V> cls){
        Number n = (Number) mapping;
        if(int.class == cls){
            return (V)(Integer)n.intValue();
        }
        if(double.class == cls){
            return (V)(Double)n.doubleValue();
        }
        if(float.class == cls){
            return (V)(Float)n.floatValue();
        }
        if(long.class == cls){
            return (V)(Long)n.longValue();
        }
        if(short.class == cls) {
            return (V) (Short) n.shortValue();
        }
        if(byte.class == cls){
            return (V)(Byte)n.byteValue();
        }
        return (V)n;
    }

    @Override
    public String toString() {
        if(mapping == null){
            return "EmptyJSONMapping";
        }
        else{
            return "JSONMapping{class = " + mapping.getClass().getName() + ", value = " + mapping + ", serialized form = " + valueString + "}";
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
