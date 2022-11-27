package com.vicious.viciouslib.jarloader.event;

public class InitializationEvent extends VEventReturns<Object>{
    public InitializationEvent(boolean hasCompleted) {
        super(hasCompleted);
    }

    public InitializationEvent(EventPhase phase) {
        super(phase);
    }
}
