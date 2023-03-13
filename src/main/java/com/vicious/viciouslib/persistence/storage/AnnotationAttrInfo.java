package com.vicious.viciouslib.persistence.storage;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class AnnotationAttrInfo implements AttrInfo{
    private final Save impl;

    public AnnotationAttrInfo(Save impl){
        this.impl = impl;
    }
    @Override
    public String name() {
        return impl.value();
    }

    @Override
    public String description() {
        return impl.description();
    }

    @Override
    public String parent() {
        return impl.parent();
    }
}
