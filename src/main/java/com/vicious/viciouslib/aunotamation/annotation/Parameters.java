package com.vicious.viciouslib.aunotamation.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)

@AnnotationAugmentation
public @interface Parameters {
    Class<?>[] value();
}
