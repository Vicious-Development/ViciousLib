package com.vicious.viciouslib.persistence.storage.aunotamations;

import com.vicious.viciouslib.aunotamation.annotation.Parameters;
import com.vicious.viciouslib.persistence.storage.AttributeModificationEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Parameters(AttributeModificationEvent.class)
public @interface OnChanged {
}
