package com.vicious.viciouslib.jarloader.event;

import com.google.common.reflect.TypeResolver;
import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.jarloader.InterceptorMap;
import com.vicious.viciouslib.jarloader.event.interceptor.EventInterceptorInstance;
import com.vicious.viciouslib.jarloader.event.interceptor.ExecutableEventInterceptor;
import com.vicious.viciouslib.jarloader.event.interceptor.LambdaEventInterceptor;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public interface InstanceEventBroadcaster {
    InterceptorMap getMap();
    TypeResolver typeResolver = new TypeResolver();

    default boolean send(Object object){
        return getMap().send(object);
    }

    default void register(Method m, Object target){
        register(m.getParameterTypes()[0],target,m);
    }

    default void register(Class<?> eventType, Object target, Executable exec){
        register(new ExecutableEventInterceptor(target,exec,eventType));
    }

    default void register(Object o){
        Aunotamation.processObject(o);
    }

    default void register(Consumer<Object> consumer){
        Type type = typeResolver.resolveType(consumer.getClass().getGenericSuperclass());
        if(type instanceof Class<?>){
            EventInterceptorInstance instance = new LambdaEventInterceptor(consumer,consumer,(Class<?>)type);
            register(instance);
        }
        else{
            LoggerWrapper.logError("Could not get type of event consumer!");
        }
    }
    default void register(EventInterceptorInstance instance){
        getMap().add(instance);
    }
    default void unregister(Object o){
        getMap().remove(o);
    }
}
