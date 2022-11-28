package com.vicious.viciouslib.jarloader.event;


import com.vicious.viciouslib.aunotamation.annotation.ModifiedWith;
import com.vicious.viciouslib.aunotamation.annotation.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

@Parameters(Object.class)
@ModifiedWith(Modifier.PUBLIC)
public @interface BroadcastTo {
    String[] value();
}
