package com.vicious.viciouslib.database.tracking.interfaces;


import com.vicious.viciouslib.database.tracking.values.TrackableValue;

public interface TrackableValueConverter {
    void convert(TrackableValue<?> trackableValue);
}
