package com.vicious.viciouslib.persistence.storage;

public class AttrInfoImpl implements AttrInfo{
    private String name = "";
    private String description = "";
    private String parent = "";

    public AttrInfoImpl(String name, String description, String parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String parent() {
        return parent;
    }
}
