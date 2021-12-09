package com.vicious.viciouslib.command;

import java.util.function.Consumer;

public class QueuedInput<USERTYPE,CHANNELTYPE> {
    private final Consumer<String> executor;
    public String input;
    public final InputTarget TARGET;
    public QueuedInput(CHANNELTYPE channel, USERTYPE user, int priority, Consumer<String> executor){
        TARGET=new InputTarget(channel,user,priority);
        this.executor=executor;
    }
    public QueuedInput insertString(String stringin){
        input=stringin;
        return this;
    }
    public boolean hasInput(){
        return input != null;
    }
    public boolean doesEventMatchRequisites(CHANNELTYPE channel, USERTYPE author){
        return TARGET.CHANNEL.equals(channel) && TARGET.USER.equals(author);
    }
    public Consumer<String> getExecutor(){
        return executor;
    }

    public static class InputTarget<USERTYPE,CHANNELTYPE> {
        public final USERTYPE USER;
        public final CHANNELTYPE CHANNEL;
        public final int PRIORITY;
        public InputTarget(CHANNELTYPE channel, USERTYPE user, int priority){
            USER = user;
            CHANNEL = channel;
            PRIORITY = priority;
        }
    }

}
