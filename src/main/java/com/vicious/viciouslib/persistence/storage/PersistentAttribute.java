package com.vicious.viciouslib.persistence.storage;

import com.vicious.viciouslib.persistence.json.JSONMapping;

import java.util.function.Consumer;

public class PersistentAttribute<T> {
    private final String name;
    private final Class<T> expectedType;
    private T value;
    private boolean isDirty = false;
    private Persistent container;
    private String description = "";
    private PersistentAttribute<?> parent;

    public PersistentAttribute(String name, Class<T> expectedType) {
        this.name = name;
        this.expectedType = expectedType;
    }
    public PersistentAttribute(String name, Class<T> expectedType, T value) {
        this.name = name;
        this.expectedType = expectedType;
        this.value=value;
    }

    public T get(){
        return value;
    }

    public void describe(String description){
        this.description=description;
    }

    public void setContainer(Persistent persistent){
        this.container=container;
    }

    public Persistent container(){
        return container;
    }

    public T set(T t){
        T pre = value;
        value=t;
        if(value != null && !value.equals(pre)) {
            setDirty(true);
        }
        return pre;
    }

    public boolean isDirty(){
        return isDirty;
    }

    public void setDirty(boolean b){
        this.isDirty=b;
        if(isDirty) {
            onContainer(Persistent::setDirty);
        }
    }

    private void onContainer(Consumer<Persistent> cons) {
        if(container != null){
            cons.accept(container);
        }
    }

    public Class<T> type(){
        return expectedType;
    }

    public String name(){
        return name;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public String getDescription() {
        return description;
    }

    public void fromJSON(JSONMapping mapping) {
        set(mapping.softAs(type()));
    }

    public void setParent(PersistentAttribute<?> parent) {
        this.parent = parent;
    }

    public boolean hasParent(){
        return parent != null;
    }

    public PersistentAttribute<?> getParent(){
        return parent;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    public T value(){
        return value;
    }
}
