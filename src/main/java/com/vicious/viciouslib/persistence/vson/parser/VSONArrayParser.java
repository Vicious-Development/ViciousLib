package com.vicious.viciouslib.persistence.vson.parser;

import com.vicious.viciouslib.persistence.vson.SerializationHandler;
import com.vicious.viciouslib.persistence.vson.VSONArray;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;

import java.io.InputStream;

public class VSONArrayParser extends VSONParser {
    private final VSONArray arr = new VSONArray();
    public VSONArrayParser(InputStream fis){
        start(fis);
    }

    public void start(InputStream fis){
        this.fis = fis;
        String value = "";
        while(hasData()) {
            char c = read();
            //Skip Comments.
            if(c == '#'){
                while(c != '\n' && hasData()){
                    c = read();
                }
                continue;
            }
            if(c == '{'){
                VSONMap inner = new VSONMapParser(fis).getMap();
                arr.addObject(inner);
                value = "";
            }
            else if (c == '['){
                VSONArray array = new VSONArrayParser(fis).getArray();
                arr.addObject(array);
                value = "";
            }
            else if(c == ']'){
                if(!value.isEmpty()) {
                    add(value);
                }
                return;
            }
            else if(c == '\n' || c == ','){
                if(value.isEmpty()){
                    continue;
                }
                add(value);
                value = "";
            }
            else{
                value+=c;
            }
        }
    }

    public void add(String value){
        value = removeEdgeWhiteSpace(value);
        if(value.isEmpty()){
            return;
        }
        AssumedType type = new AssumedType();
        for (int i = 0; i < value.length(); i++) {
            type.append(value.charAt(i));
        }
        arr.add(new VSONValue(SerializationHandler.deserialize(type.string,type.type),type.string));
    }

    public VSONArray getArray(){
        return arr;
    }
}
