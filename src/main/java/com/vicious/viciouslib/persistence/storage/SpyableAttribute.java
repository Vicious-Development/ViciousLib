package com.vicious.viciouslib.persistence.storage;

import com.vicious.viciouslib.jarloader.InterceptorMap;
import com.vicious.viciouslib.jarloader.event.InstanceEventBroadcaster;
import com.vicious.viciouslib.jarloader.event.VEvent;

public class SpyableAttribute<T> extends PersistentAttribute<T> implements InstanceEventBroadcaster {
    public SpyableAttribute(String name, Class<T> expectedType) {
        super(name, expectedType);
    }

    public SpyableAttribute(String name, Class<T> expectedType, T value) {
        super(name, expectedType, value);
    }

    private final InterceptorMap map = new InterceptorMap();

    public InterceptorMap getMap(){
        return map;
    }

    @Override
    public T set(T t) {
        T x = super.set(t);
        send(new Update<T>(this));
        return x;
    }

    public static class Update<T> extends VEvent {
        private final SpyableAttribute<T> attri;
        public Update(SpyableAttribute<T> t){
            super(true);
            this.attri=t;
        }
        public SpyableAttribute<T> getUpdated(){
            return attri;
        }
    }
}
