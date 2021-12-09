package com.vicious.viciouslib.database.tracking.interfaces;

public interface TrackableValueStringParser<T> {
    T parse(String s) throws Exception;
}
