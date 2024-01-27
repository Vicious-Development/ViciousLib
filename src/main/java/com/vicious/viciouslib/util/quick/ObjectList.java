package com.vicious.viciouslib.util.quick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ObjectList extends ArrayList<Object> {
    public ObjectList(int initialCapacity) {
        super(initialCapacity);
    }

    public ObjectList() {
    }

    public ObjectList(Collection<?> c) {
        super(c);
    }

    public static ObjectList empty() {
        return new ObjectList();
    }
    public static ObjectList of(Object... values){
        return new ObjectList(Arrays.asList(values));
    }

    public ObjectList modify(Consumer<ObjectList> consumer) {
        consumer.accept(this);
        return this;
    }

    public void add(Object... values) {
        this.addAll(Arrays.asList(values));
    }
}
