package com.vicious.viciouslib.persistence.vson.value;

import com.vicious.viciouslib.persistence.storage.AttrInfo;

public class VSONMapping extends VSONValue {
    public VSONMapping(Object mapping, AttrInfo info){
        this(mapping);
        this.info=info;
    }
    public VSONMapping(Object mapping, String valueString){
        super(mapping);
        this.valueString=valueString;
    }
    public VSONMapping(Object mapping){
        super(mapping);
        this.valueString = mapping == null ? "null" : mapping.toString();
    }

    @Override
    public String toString() {
        if(value == null){
            return "EmptyVSONMapping";
        }
        else{
            return "VSONMapping{class = " + value.getClass().getName() + ", value = " + value + ", serialized form = " + valueString + "}";
        }
    }
}
