package com.vicious.viciouslib.command;

import java.util.ArrayList;
import java.util.List;

public class InputQueue<USERTYPE,CHANNELTYPE> {
    private final List<QueuedInput<USERTYPE,CHANNELTYPE>> queued = new ArrayList<>();
    public void queue(QueuedInput<USERTYPE,CHANNELTYPE> input){
        queued.add(input);
    }
    public boolean attemptExecution(CHANNELTYPE channel, USERTYPE author, String message){
        List<QueuedInput<USERTYPE,CHANNELTYPE>> nextList = new ArrayList<>();
        QueuedInput<USERTYPE,CHANNELTYPE> toexecute;
        for(QueuedInput<USERTYPE,CHANNELTYPE> qi : queued){
            if(qi.doesEventMatchRequisites(channel,author)){
                nextList.add(qi);
            }
        }
        if(nextList.size() > 0) {
            if (nextList.size() > 1) {
                toexecute = next(nextList);
            } else toexecute = nextList.get(0);
            toexecute.getExecutor().accept(message);
            queued.remove(toexecute);
            return true;
        }
        return false;
    }
    public void clearForUSERTYPE(USERTYPE u){
        queued.removeIf(qi -> qi.TARGET.USER.equals(u));
    }
    public void clearForChannel(CHANNELTYPE mc){
        queued.removeIf(qi -> qi.TARGET.CHANNEL.equals(mc));
    }
    private QueuedInput<USERTYPE,CHANNELTYPE> next(List<QueuedInput<USERTYPE,CHANNELTYPE>> nextList){
        QueuedInput<USERTYPE,CHANNELTYPE> priorityInput = nextList.get(0);
        for(QueuedInput<USERTYPE,CHANNELTYPE> qi : nextList){
            if(qi.TARGET.PRIORITY < priorityInput.TARGET.PRIORITY){
                priorityInput = qi;
            }
        }
        return priorityInput;
    }
}
