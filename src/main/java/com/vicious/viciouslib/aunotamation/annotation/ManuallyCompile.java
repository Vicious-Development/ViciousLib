package com.vicious.viciouslib.aunotamation.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stops the aunotamation compiler from checking validity and instead allows the processor to perform compilation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@AnnotationAugmentation
public @interface ManuallyCompile {
}
