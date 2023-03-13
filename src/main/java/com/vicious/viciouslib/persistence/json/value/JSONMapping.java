package com.vicious.viciouslib.persistence.json.value;

import com.vicious.viciouslib.persistence.storage.AttrInfo;

public class JSONMapping extends JSONValue{
    public JSONMapping(Object mapping, AttrInfo info){
        this(mapping);
        this.info=info;
    }
    public JSONMapping(Object mapping, String valueString){
        super(mapping);
        this.valueString=valueString;
    }
    public JSONMapping(Object mapping){
        super(mapping);
        this.valueString = mapping == null ? "null" : mapping.toString();
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
}
