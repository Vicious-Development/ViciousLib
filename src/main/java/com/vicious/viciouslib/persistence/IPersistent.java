package com.vicious.viciouslib.persistence;

public interface IPersistent {
    default void load(){
        PersistenceHandler.load(this);
    }
    default void save(){
        PersistenceHandler.save(this);
    }
}
