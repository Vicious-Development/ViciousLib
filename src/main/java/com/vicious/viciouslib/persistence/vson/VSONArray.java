package com.vicious.viciouslib.persistence.vson;

import com.vicious.viciouslib.persistence.vson.value.VSONValue;

import java.util.ArrayList;

public class VSONArray extends ArrayList<VSONValue>{
    public void addObject(Object o){
        add(new VSONValue(o));
    }
}
