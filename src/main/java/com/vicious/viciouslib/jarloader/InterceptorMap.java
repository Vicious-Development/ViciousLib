package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.jarloader.event.VEvent;
import com.vicious.viciouslib.jarloader.event.VEventReturns;
import com.vicious.viciouslib.jarloader.event.interceptor.EventInterceptorInstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InterceptorMap {
    private final Map<Class<?>, Set<EventInterceptorInstance>> eventTypeMapping = new HashMap<>();
    private final Map<Object, Set<EventInterceptorInstance>> interceptorMapping = new HashMap<>();

    public void add(EventInterceptorInstance instance){
        this.eventTypeMapping.computeIfAbsent(instance.getEventType(), t->new HashSet<>()).add(instance);
        this.interceptorMapping.computeIfAbsent(instance.getInterceptorObject(), t->new HashSet<>()).add(instance);
    }

    public boolean send(Object object) {
        Class<?> cls = object.getClass();
        Set<EventInterceptorInstance> interceptors = eventTypeMapping.get(cls);
        if(interceptors != null) {
            for (EventInterceptorInstance interceptor : interceptors) {
                try {
                    Object o = interceptor.intercept(object);
                    if(o != null && object instanceof VEventReturns returns){
                        returns.returnObject(o);
                    }
                } catch (Exception e) {
                    LoggerWrapper.logError("An event interceptor threw an exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        if(object instanceof VEvent v){
            return !v.isCanceled();
        }
        else return true;
    }

    public void remove(Object o) {
        Set<EventInterceptorInstance> instances = interceptorMapping.remove(o);
        if(instances != null){
            for (EventInterceptorInstance eventInterceptorInstance : instances) {
                Set<EventInterceptorInstance> clsmapped =  eventTypeMapping.get(eventInterceptorInstance.getEventType());
                if(clsmapped != null){
                    clsmapped.remove(eventInterceptorInstance);
                }
            }
        }
    }
}
