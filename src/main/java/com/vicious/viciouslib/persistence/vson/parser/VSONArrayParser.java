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
        boolean isString = false;
        while(hasData()) {
            char c = read();
            //Skip Comments.
            if(c == '#'){
                while(c != '\n' && hasData()){
                    c = read();
                }
                continue;
            }
            if(!isString && c == '{'){
                VSONMap inner = new VSONMapParser(fis).getMap();
                arr.addObject(inner);
                value = "";
            }
            else if (!isString && c == '['){
                VSONArray array = new VSONArrayParser(fis).getArray();
                arr.addObject(array);
                value = "";
            }
            else if(!isString && c == ']'){
                if(!value.isEmpty()) {
                    add(value);
                }
                return;
            }
            else if(!isString && c == '\n' || c == ','){
                if(value.isEmpty()){
                    continue;
                }
                add(value);
                value = "";
                isString=false;
            }
            else{
                if(c == '"'){
                    isString=true;
                }
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
        try {
            arr.add(new VSONValue(SerializationHandler.deserialize(type.string, type.type), type.string));
        } catch (Throwable t){
            arr.add(new VSONValue(type.string, type.string));
        }
    }

    public VSONArray getArray(){
        return arr;
    }
}
