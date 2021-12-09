package com.vicious.viciouslib.database.tracking.interfaces;

import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import org.json.JSONObject;

public interface TrackableValueJSONParser<T> {
    T parse(JSONObject jo, TrackableObject<?> track) throws Exception;
}
