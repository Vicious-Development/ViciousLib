package com.vicious.viciouslib.persistence.storage.aunotamations;

import com.vicious.viciouslib.aunotamation.annotation.ModifiedWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ModifiedWith(Modifier.PUBLIC)
public @interface Typing {
    Class<?>[] value();
}
