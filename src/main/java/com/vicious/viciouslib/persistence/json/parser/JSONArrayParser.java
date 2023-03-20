package com.vicious.viciouslib.persistence.json.parser;

import com.vicious.viciouslib.persistence.json.*;
import com.vicious.viciouslib.persistence.json.value.JSONValue;

import java.io.FileInputStream;

public class JSONArrayParser extends JSONParser {
    private final JSONArray arr = new JSONArray();
    public JSONArrayParser(FileInputStream fis){
        start(fis);
    }

    public void start(FileInputStream fis){
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
                JSONMap inner = new JSONMapParser(fis).getMap();
                arr.addObject(inner);
                value = "";
            }
            else if (c == '['){
                JSONArray array = new JSONArrayParser(fis).getArray();
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
        arr.add(new JSONValue(SerializationHandler.deserialize(type.string,type.type),type.string));
    }

    public JSONArray getArray(){
        return arr;
    }
}
