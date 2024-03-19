package com.vicious.viciouslib.persistence.vson;

import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;
import com.vicious.viciouslib.util.quick.ObjectList;
import com.vicious.viciouslib.util.quick.ObjectMap;

import java.util.ArrayList;
import java.util.Map;

public class VSONArray extends ArrayList<VSONValue>{
    public static VSONArray from(ObjectList list) {
        VSONArray array = new VSONArray();
        for (Object o : list) {
            if(o instanceof ObjectMap){
                array.addObject(VSONMap.from((ObjectMap)o));
            }
            else if(o instanceof ObjectList){
                array.addObject(from((ObjectList)o));
            }
            else{
                array.addObject(o);
            }
        }
        return array;
    }

    public void addObject(Object o){
        add(new VSONValue(o));
    }
}
