package com.vicious.viciouslib.jarloader.event.interceptor;

public abstract class EventInterceptorInstance {
    protected final Object interceptor;
    protected final Class<?> eventType;
    public EventInterceptorInstance(Object interceptor, Class<?> eventType){
        this.interceptor=interceptor;
        this.eventType=eventType;
    }
    public abstract Object intercept(Object event) throws Exception;
    public Class<?> getEventType(){
        return eventType;
    }

    public Object getInterceptorObject() {
        return interceptor;
    }
}
