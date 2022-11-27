package com.vicious.viciouslib.jarloader.event;

import java.util.ArrayList;
import java.util.List;

public class VEventReturns<T> extends VEvent {
    private List<T> returned = new ArrayList<>();
    public VEventReturns(boolean hasCompleted) {
        super(hasCompleted);
    }
    public VEventReturns(EventPhase phase) {
        super(phase);
    }
    public void returnObject(T t){
        returned.add(t);
    }
    public List<T> getReturned(){
        return returned;
    }
}
