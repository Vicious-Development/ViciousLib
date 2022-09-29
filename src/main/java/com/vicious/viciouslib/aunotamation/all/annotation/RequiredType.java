package com.vicious.viciouslib.aunotamation.all.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates what type the annotated field or method must return.
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AnnotationAugmentation
public @interface RequiredType {
    Class<?> value();
}
