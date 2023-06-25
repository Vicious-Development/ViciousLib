package com.vicious.viciouslib.jarloader.event;

import com.vicious.viciouslib.aunotamation.annotation.Extends;
import com.vicious.viciouslib.aunotamation.annotation.ModifiedWith;
import com.vicious.viciouslib.aunotamation.annotation.Parameters;
import com.vicious.viciouslib.aunotamation.annotation.RequiredType;
import org.checkerframework.checker.units.qual.C;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD})

@ModifiedWith(Modifier.PUBLIC)
@Parameters(InitializationEvent.class)
@RequiredType(Object.class)
public @interface MainEntry {
    String[] loadAfter() default {};
}
