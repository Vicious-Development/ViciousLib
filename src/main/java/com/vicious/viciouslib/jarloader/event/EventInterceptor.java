package com.vicious.viciouslib.jarloader.event;

import com.vicious.viciouslib.aunotamation.annotation.ModifiedWith;
import com.vicious.viciouslib.aunotamation.annotation.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

/**
 * Indicates a method to run when an event fitting its parameter is sent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD})

@Parameters(Object.class)
@ModifiedWith(Modifier.PUBLIC)
public @interface EventInterceptor {
}
