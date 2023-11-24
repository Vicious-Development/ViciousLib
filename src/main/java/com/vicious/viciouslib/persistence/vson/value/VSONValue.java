package com.vicious.viciouslib.persistence.vson.value;

import com.vicious.viciouslib.persistence.storage.AnnotationAttrInfo;
import com.vicious.viciouslib.persistence.storage.AttrInfo;
import com.vicious.viciouslib.persistence.vson.SerializationHandler;
import com.vicious.viciouslib.persistence.vson.writer.NamePair;

import java.util.List;
import java.util.function.Supplier;

public class VSONValue implements IHasDescription,IHasChildren, Supplier<Object> {
    protected Object value;
    protected String valueString;

    public AttrInfo info = AttrInfo.EMPTY;
    protected List<NamePair> children;

    public VSONValue(Object o) {
        this.value=o;
    }
    public VSONValue(Object o, String valueString) {
        this.value=o;
        this.valueString=valueString;
    }

    public Object get(){
        return value;
    }

    public boolean hasChildren(){
        return children != null && !children.isEmpty();
    }

    @Override
    public List<NamePair> getChildren() {
        return children;
    }

    /**
     * Requires the class to be of the type provided. This will cause problems if the mapping is of type Double and you try to cast it to Float
     */
    @SuppressWarnings("unchecked")
    public <V> V as(Class<V> cls){
        if(value == null){
            return null;
        }
        return cls.cast(value);
    }
    @SuppressWarnings("unchecked")
    public <V> V softAs(Class<V> cls){
        try{
            return as(cls);
        } catch (ClassCastException e){
            if(cls.isEnum()){
                return (V)Enum.valueOf((Class)cls,valueString);
            }
            if(value instanceof Number && Number.class.isAssignableFrom(cls)){
                return (V) asN((Class<Number>)cls);
            }
            V v = SerializationHandler.deserialize(valueString,cls);
            if(v == null){
                throw new VSONException("Completely failed to parse a VSON value. String: " + valueString);
            }
            value = v;
            return v;
        }
    }
    @SuppressWarnings("unchecked")
    private <V extends Number> V asN(Class<V> cls){
        Number n = (Number) value;
        if(int.class == cls || Integer.class == cls){
            return (V)(Integer)n.intValue();
        }
        if(double.class == cls || Double.class == cls){
            return (V)(Double)n.doubleValue();
        }
        if(float.class == cls || Float.class == cls){
            return (V)(Float)n.floatValue();
        }
        if(long.class == cls || Long.class == cls){
            return (V)(Long)n.longValue();
        }
        if(short.class == cls || Short.class == cls) {
            return (V) (Short) n.shortValue();
        }
        if(byte.class == cls || Byte.class == cls){
            return (V)(Byte)n.byteValue();
        }
        return (V)n;
    }

    @Override
    public String toString() {
        //This shouldn't ever happen as storing nulls doesn't really make much sense but this is supported regardless.
        if(value == null){
            return "EmptyVSONValue";
        }
        else{
            return "VSONValue{class = " + value.getClass().getName() + ", value = " + value + ", serialized form = " + valueString + "}";
        }
    }

    public boolean isType(Class<?> cls) {
        if(value == null){
            return false;
        }
        else{
            return cls == value.getClass();
        }
    }

    @Override
    public boolean hasDescription() {
        return info.hasDescription();
    }

    @Override
    public String getDescription() {
        return info.description();
    }

    public boolean isNull() {
        return value == null;
    }

    public VSONMapping asMapping(AttrInfo info) {
        return new VSONMapping(get(),info);
    }
}
