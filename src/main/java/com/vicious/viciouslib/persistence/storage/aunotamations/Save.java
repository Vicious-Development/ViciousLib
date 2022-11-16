package com.vicious.viciouslib.persistence.storage.aunotamations;

import com.vicious.viciouslib.aunotamation.annotation.AllowedIn;
import com.vicious.viciouslib.aunotamation.annotation.ModifiedWith;
import com.vicious.viciouslib.aunotamation.annotation.NotModifiedWith;
import com.vicious.viciouslib.aunotamation.annotation.RequiredType;
import com.vicious.viciouslib.persistence.storage.Persistent;
import com.vicious.viciouslib.persistence.storage.PersistentAttribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@AllowedIn(Persistent.class)
@RequiredType(PersistentAttribute.class)
@ModifiedWith(Modifier.PUBLIC)
@NotModifiedWith({Modifier.STATIC,Modifier.FINAL})
public @interface Save {
    String value() default "";
    String description() default "";
    String parent() default "";
}
