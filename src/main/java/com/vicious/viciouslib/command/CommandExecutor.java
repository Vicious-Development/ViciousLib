package com.vicious.viciouslib.command;

@FunctionalInterface
public interface CommandExecutor<USERTYPE,CHANNELTYPE> {
    boolean execute(Command.CommandContext<USERTYPE, CHANNELTYPE> ctx, USERTYPE user) throws Exception;
}
