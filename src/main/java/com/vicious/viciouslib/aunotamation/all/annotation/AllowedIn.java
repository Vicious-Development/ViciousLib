package com.vicious.viciouslib.aunotamation.all.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates what the class the annotated element is in must inherit
 */
@Retention(RetentionPolicy.RUNTIME)
@AnnotationAugmentation
public @interface AllowedIn {
    Class<?> value();
}
