package com.vicious.viciouslib.persistence.json;

import java.util.ArrayList;

public class JSONArray extends ArrayList<JSONValue>{
    public void addObject(Object o){
        add(new JSONValue(o));
    }
}
