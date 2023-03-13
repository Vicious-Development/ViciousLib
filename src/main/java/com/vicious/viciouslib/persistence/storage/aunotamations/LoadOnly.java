package com.vicious.viciouslib.persistence.storage.aunotamations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates a persistent object that should only load after being created. Saving will only happen if the file does not exist.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadOnly {
}
