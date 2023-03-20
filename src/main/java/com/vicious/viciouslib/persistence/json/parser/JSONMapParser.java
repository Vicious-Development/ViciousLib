package com.vicious.viciouslib.persistence.json.parser;

import com.vicious.viciouslib.persistence.json.*;
import com.vicious.viciouslib.persistence.json.value.JSONMapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class JSONMapParser extends JSONParser{
    private final JSONMap map = new JSONMap();
    public JSONMapParser(FileInputStream fis){
        start(fis);
    }

    public JSONMapParser(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        start(fis);
    }

    public void start(FileInputStream fis){
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
                JSONMap inner = new JSONMapParser(fis).getMap();
                map.put(removeEdgeWhiteSpace(name),inner);
                rv = false;
                name = "";
                value = "";
            }
            else if (c == '['){
                JSONArray array = new JSONArrayParser(fis).getArray();
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
        map.put(name,new JSONMapping(SerializationHandler.deserialize(type.string,type.type),type.string));
    }

    public JSONMap getMap(){
        return map;
    }
}
