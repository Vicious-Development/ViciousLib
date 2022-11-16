package com.vicious.viciouslib.persistence.storage;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PersistentObject implements Persistent {
    private boolean isDirty = false;
    private Map<String, PersistentAttribute<?>> values = new LinkedHashMap<>();

    @Override
    public void setDirty() {
        isDirty=true;
    }

    @Override
    public Map<String, PersistentAttribute<?>> getMap() {
        return values;
    }
}
