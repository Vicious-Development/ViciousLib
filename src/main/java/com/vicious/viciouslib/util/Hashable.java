package com.vicious.viciouslib.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface Hashable {
    default int hash(){
        Class<?> cls = this.getClass();
        List<Object> objects = new ArrayList<>();
        while(cls != null){
            for (Field declaredField : cls.getDeclaredFields()) {
                declaredField.setAccessible(true);
                try {
                    objects.add(declaredField.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            cls=cls.getSuperclass();
        }
        return Objects.hash(objects);
    }
}
