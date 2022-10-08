package com.vicious.viciouslib.aunotamation.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Extends {
    Class<? extends Annotation>[] value();
}
