package com.vicious.viciouslib.aunotamation.all.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@AnnotationAugmentation
public @interface Conflicts {
    Class<? extends Annotation>[] value();
}
