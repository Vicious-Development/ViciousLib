package com.vicious.viciouslib.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class CommandHandler<USERTYPE, CHANNELTYPE> {
    public abstract void messageUser(USERTYPE user, String restrictedDescription);
    public abstract boolean isAuthorized(Command<USERTYPE,CHANNELTYPE> cmd, USERTYPE user);
    public abstract boolean isUserACommandSender(USERTYPE user);
    public abstract boolean canBeCommand(String line);

    public static final String INVALIDCOMMANDMSG = "Invalid command!";
    protected Map<String,Command<USERTYPE,CHANNELTYPE>> commands = new HashMap<>();
    public final InputQueue<USERTYPE,CHANNELTYPE> queuedInputs = new InputQueue<>();
    public void register(Command<USERTYPE,CHANNELTYPE> cmd){
        for (String identifier : cmd.getIdentifiers()) {
            commands.putIfAbsent(identifier,cmd);
            cmd.handler=this;
        }
    }
    private Command<USERTYPE,CHANNELTYPE> getCommand(String commandLine){
        Command<USERTYPE,CHANNELTYPE> cmd = commands.get(parseIdentifier(commandLine));
        if(cmd == null) return null;
        //Get child
        cmd = getCommand(cmd.eraseIdentifier(commandLine),cmd);
        return cmd;
    }
    private Command<USERTYPE,CHANNELTYPE> getCommand(String commandLine, Command<USERTYPE,CHANNELTYPE> parent){
        for(Command<USERTYPE,CHANNELTYPE> c : parent.collectChildren()){
            if(c.isTheCommand(commandLine)){
                return getCommand(c.eraseIdentifier(commandLine), c);
            }
        }
        return parent;
    }
    private void execute(Command<USERTYPE,CHANNELTYPE> cmd, String line, USERTYPE user, CHANNELTYPE channel) throws Exception{
        cmd.getExecutor().execute(cmd.execute(line, channel), user);
    }
    public String getRestrictedDescription(Command<USERTYPE,CHANNELTYPE> cmd, USERTYPE user) {
        if(cmd.permission == null || isAuthorized(cmd,user)) {
            AtomicReference<String> childdescs = new AtomicReference<>("");
            int i = 0;
            cmd.children.forEach((str,child)->{
                if(isAuthorized(cmd,user)) {
                    childdescs.set(childdescs.get().concat(getRestrictedDescription(child,user)));
                    if (i < cmd.children.size() - 1) {
                        childdescs.set(childdescs.get().concat("\n"));
                    }
                }
            });
            return toBold(cmd.getFullIdentifier() + " " + cmd.argumentList()) + " " + cmd.description + (childdescs.get().equals("") ? "" : "\n" + childdescs.get());
        }
        return "";
    }
    public static String toBold(String str){
        return "**" + str + "**";
    }
    public String parseIdentifier(String cmd){
        String identifier="";
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if(c == ' '){
                return identifier;
            }
            else if(i == cmd.length()-1){
                return identifier+c;
            }
            else identifier+=c;
        }
        return identifier;
    }
    public void processCommand(String line, USERTYPE user, CHANNELTYPE channel) throws Exception{
        if(queuedInputs.attemptExecution(channel, user, line)) return;
        if(!canBeCommand(line)) return;
        if (!isUserACommandSender(user)) return;
        Command<USERTYPE,CHANNELTYPE> cmd = getCommand(line);
        if(cmd == null) throw new CommandException("The command you entered does not exist.");
        //Execute if authorized
        if(isAuthorized(cmd,user)){
            executeCommand(cmd, line, user, channel);
        }
        else throw new CommandException("You are not authorized to run this command.");
    }
    public void executeCommand(Command<USERTYPE,CHANNELTYPE> cmd, String line, USERTYPE user, CHANNELTYPE channel) throws Exception{
        execute(cmd,line,user,channel);
    }

    public void queueInputExecutor(CHANNELTYPE channel, USERTYPE u, int i, Consumer<String> executor) {
        queuedInputs.queue(new QueuedInput(channel,u,i,executor));
    }
    public Map<String,Command<USERTYPE,CHANNELTYPE>> getCommands(){
        return commands;
    }
}
