package com.vicious.viciouslib.database.tracking.interfaces;

public interface SQLConverter<V> {
    String convert(V trackableValue);
}
