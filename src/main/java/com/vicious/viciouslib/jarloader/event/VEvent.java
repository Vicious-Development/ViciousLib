package com.vicious.viciouslib.jarloader.event;

import com.vicious.viciouslib.jarloader.ViciousJarLoader;

public class VEvent {
    private EventPhase phase;
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
    public void send(){
        ViciousJarLoader.getInstance().sendEvent(this);
    }

    /**
     * Called after the event pre phase.
     * If not cancelled, Runs all post code then sends out the post event notification.
     */
    public void post(Runnable run){
        if(!isCanceled()){
            run.run();
            phase=EventPhase.AFTER;
            ViciousJarLoader.getInstance().sendEvent(this);
        }
    }
}
