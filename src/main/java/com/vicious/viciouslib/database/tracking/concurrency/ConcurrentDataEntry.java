package com.vicious.viciouslib.database.tracking.concurrency;

public class ConcurrentDataEntry<V> {
    public boolean isDirty;
    public V value;
    public ConcurrentDataEntry(V value){
        this.value=value;
    }
}
