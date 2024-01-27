package com.vicious.viciouslib.util.quick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ObjectMap extends HashMap<Object,Object> {

    public ObjectMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObjectMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ObjectMap() {
    }

    public ObjectMap(Map<?, ?> m) {
        super(m);
    }

    public static ObjectMap empty() {
        return new ObjectMap();
    }

    public ObjectMap modify(Consumer<ObjectMap> consumer) {
        consumer.accept(this);
        return this;
    }
}
