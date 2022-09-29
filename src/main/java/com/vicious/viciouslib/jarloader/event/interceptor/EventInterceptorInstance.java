package com.vicious.viciouslib.jarloader.event.interceptor;

import java.lang.reflect.InvocationTargetException;

public abstract class EventInterceptorInstance {
    protected final Object interceptor;
    protected final Class<?> eventType;
    public EventInterceptorInstance(Object interceptor, Class<?> eventType){
        this.interceptor=interceptor;
        this.eventType=eventType;
    }
    public abstract Object intercept(Object event) throws InvocationTargetException, IllegalAccessException, InstantiationException;
    public Class<?> getEventType(){
        return eventType;
    }

    public Object getInterceptorObject() {
        return interceptor;
    }
}
