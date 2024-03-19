package com.vicious.viciouslib.jarloader;

import com.vicious.viciouslib.jarloader.event.InstanceEventBroadcaster;

import java.lang.reflect.Executable;

public class ViciousEventBroadcaster implements InstanceEventBroadcaster {
    private final InterceptorMap globalInterceptors = new InterceptorMap();

    public static volatile ViciousEventBroadcaster INSTANCE = new ViciousEventBroadcaster();

    public static boolean post(Object object) {
        return get().send(object);
    }

    public static ViciousEventBroadcaster get(){
        return INSTANCE;
    }

    private ViciousEventBroadcaster(){}
    public static void registerAunotamated(Class<?> eventType, Object target, Executable exec){
        get().register(eventType,target,exec);
    }

    @Override
    public InterceptorMap getMap() {
        return globalInterceptors;
    }
}
