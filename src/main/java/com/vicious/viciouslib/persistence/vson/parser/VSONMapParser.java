package com.vicious.viciouslib.persistence.vson.parser;

import com.vicious.viciouslib.persistence.vson.*;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class VSONMapParser extends VSONParser {
    private final VSONMap map = new VSONMap();
    public VSONMapParser(InputStream fis){
        start(fis);
    }

    public VSONMapParser(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        start(fis);
    }

    public void start(InputStream fis){
        this.fis = fis;
        String name = "";
        boolean rv = false;
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
                map.put(removeEdgeWhiteSpace(name),inner);
                rv = false;
                name = "";
                value = "";
            }
            else if (!isString && c == '['){
                VSONArray array = new VSONArrayParser(fis).getArray();
                map.put(removeEdgeWhiteSpace(name),array);
                rv = false;
                name = "";
                value = "";
            }
            else if(!isString && c == '}'){
                if(!name.isEmpty() && !value.isEmpty()) {
                    add(name,value);
                }
                return;
            }
            else if(c == '=' && !rv){
                rv = true;
            }
            else if(c == '\n' || c == ','){
                if(name.isEmpty() || value.isEmpty()){
                    continue;
                }
                add(name,value);
                name = "";
                value = "";
                rv = false;
                isString=false;
            }
            else{
                if(!rv){
                    name += c;
                }
                else{
                    if(c == '"'){
                        isString=true;
                    }
                    value+=c;
                }
            }
        }
    }

    public void add(String name, String value){
        name = removeEdgeWhiteSpace(name);
        value = removeEdgeWhiteSpace(value);
        AssumedType type = new AssumedType();
        for (int i = 0; i < value.length(); i++) {
            type.append(value.charAt(i));
        }
        try {
            map.put(name, new VSONMapping(SerializationHandler.deserialize(type.string, type.type), type.string));
        } catch (Throwable t){
            map.put(name, new VSONMapping(type.string, type.string));
        }
    }

    public VSONMap getMap(){
        return map;
    }
}
