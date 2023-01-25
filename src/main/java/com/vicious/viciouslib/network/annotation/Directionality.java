package com.vicious.viciouslib.network.annotation;

import com.vicious.viciouslib.network.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Directionality {
    Side[] value();
}
