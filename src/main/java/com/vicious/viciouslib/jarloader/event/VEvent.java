package com.vicious.viciouslib.jarloader.event;

import com.vicious.viciouslib.jarloader.ViciousEventBroadcaster;

public class VEvent {
    private final EventPhase phase;
    private boolean canceled = false;
    public VEvent(boolean hasCompleted){
        if(hasCompleted) phase = EventPhase.AFTER;
        else phase = EventPhase.BEFORE;
    }
    public VEvent(EventPhase phase){
        this.phase=phase;
    }
    public EventPhase getPhase(){
        return phase;
    }
    public boolean isCanceled(){
        return canceled && phase != EventPhase.AFTER;
    }
    public void cancel(){
        canceled=true;
    }
    public boolean post(){
        return ViciousEventBroadcaster.post(this);
    }
}
