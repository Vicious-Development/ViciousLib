package com.vicious.viciouslib.jarloader.event.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExecutableEventInterceptor extends EventInterceptorInstance {
    private final Executable exec;
    public ExecutableEventInterceptor(Object interceptor, Executable exec, Class<?> eventType){
        super(interceptor,eventType);
        this.exec=exec;
    }
    public Object intercept(Object event) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if(exec instanceof Method m){
            m.invoke(interceptor,event);
        }
        else if(exec instanceof Constructor c){
            return c.newInstance(event);
        }
        return null;
    }
    public Class<?> getEventType(){
        return eventType;
    }

    public Object getInterceptorObject() {
        return interceptor;
    }

    @Override
    public String toString() {
        return exec.toString();
    }
}
