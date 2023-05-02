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
                map.put(removeEdgeWhiteSpace(name),inner);
                rv = false;
                name = "";
                value = "";
            }
            else if (c == '['){
                VSONArray array = new VSONArrayParser(fis).getArray();
                map.put(removeEdgeWhiteSpace(name),array);
                rv = false;
                name = "";
                value = "";
            }
            else if(c == '}'){
                if(!name.isEmpty() && !value.isEmpty()) {
                    add(name,value);
                }
                return;
            }
            else if(c == '='){
                rv = true;
            }
            else if(c == '\n' || c == ','){
                if(name.isEmpty() || value.isEmpty()){
                    continue;
                }
                add(name,value);
                AssumedType type = new AssumedType();
                name = "";
                value = "";
                rv = false;
            }
            else{
                if(!rv){
                    name += c;
                }
                else{
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
        map.put(name,new VSONMapping(SerializationHandler.deserialize(type.string,type.type),type.string));
    }

    public VSONMap getMap(){
        return map;
    }
}
