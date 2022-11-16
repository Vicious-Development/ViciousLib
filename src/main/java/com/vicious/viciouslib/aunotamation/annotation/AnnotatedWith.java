package com.vicious.viciouslib.aunotamation.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@AnnotationAugmentation
public @interface AnnotatedWith {
    Class<? extends Annotation>[] value();
}
