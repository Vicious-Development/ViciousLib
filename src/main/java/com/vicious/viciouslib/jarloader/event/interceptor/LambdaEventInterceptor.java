package com.vicious.viciouslib.jarloader.event.interceptor;

import java.util.function.Consumer;

@SuppressWarnings({"rawtypes","unchecked"})
public class LambdaEventInterceptor extends EventInterceptorInstance{
    protected final Consumer cons;
    public LambdaEventInterceptor(Object interceptor, Consumer cons, Class<?> eventType) {
        super(interceptor, eventType);
        this.cons=cons;
    }

    @Override
    public Object intercept(Object event) {
        cons.accept(event);
        return null;
    }
}
