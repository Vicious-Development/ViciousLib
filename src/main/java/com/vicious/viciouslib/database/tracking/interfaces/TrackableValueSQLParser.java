package com.vicious.viciouslib.database.tracking.interfaces;

import com.vicious.viciouslib.database.tracking.values.TrackableObject;

import java.sql.ResultSet;

public interface TrackableValueSQLParser<T> {
    T parse(ResultSet rs, TrackableObject<T> track) throws Exception;
}
