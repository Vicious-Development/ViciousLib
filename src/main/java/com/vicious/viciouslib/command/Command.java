package com.vicious.viciouslib.command;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class Command<USERTYPE,CHANNELTYPE> {
    public final String description;
    public boolean isPrivateMessage;
    public boolean isPublicMessage;
    public String minimumRole;
    private final List<String> identifiers;
    public final Map<String,Command<USERTYPE,CHANNELTYPE>> children = new HashMap<>();
    private CommandExecutor<USERTYPE,CHANNELTYPE> executor;
    private final Argument[] args;
    private Command<USERTYPE,CHANNELTYPE> parent;
    public CommandHandler<USERTYPE,CHANNELTYPE> handler;
    private Command(List<String> identifiers, String role, boolean pub, boolean priv, List<Command<USERTYPE,CHANNELTYPE>> children, CommandExecutor<USERTYPE,CHANNELTYPE> executor, Argument[] args, String description){
        minimumRole = role;
        isPrivateMessage=priv;
        isPublicMessage=pub;
        for (int i = 0; i < identifiers.size(); i++) {
            identifiers.set(i, identifiers.get(i).toLowerCase(Locale.ROOT));
        }
        this.identifiers = identifiers;
        for (Command<USERTYPE, CHANNELTYPE> child : children) {
            for (String identifier : child.getIdentifiers()) {
                this.children.putIfAbsent(identifier,child);
            }
        }
        this.executor = executor == null ? (ctx, user)->{
            handler.messageUser(user,handler.getRestrictedDescription(this,user));
            return true;
        } : executor;
        this.args=args;
        this.description=description;
        for(Command<USERTYPE,CHANNELTYPE> c : children){
            c.parent = this;
        }
    }
    public String getFullIdentifier(){
        if(this.parent == null) return identifiers.get(0);
        return this.parent.getFullIdentifier() + " " + this.identifiers.get(0);
    }
    public void setExecutor(CommandExecutor<USERTYPE,CHANNELTYPE> executor){
        this.executor=executor;
    }

    public String argumentList() {
        String ret = "";
        for(int i = 0; i < args.length; i++){
            Argument a = args[i];
            ret+=a.toString();
            if(i < args.length-1){
                ret+=",";
            }
        }
        return ret;
    }

    public boolean isTheCommand(String str){
        str = eraseSpace(str);
        for(String s : identifiers) {
            if(str.toLowerCase(Locale.ROOT).startsWith(s)) return true;
        }
        return false;
    }
    private CommandContext<USERTYPE,CHANNELTYPE> toCommandContext(String input, CHANNELTYPE channel) throws CommandException,ParseException {
        String fixed = eraseIdentifier(input);
        return new CommandContext<USERTYPE,CHANNELTYPE>(parse(deliminate(fixed)), channel);
    }
    public static <USERTYPE,CHANNELTYPE>  CommandBuilder<USERTYPE,CHANNELTYPE> builder(){
        return new CommandBuilder<USERTYPE,CHANNELTYPE>();
    }
    public String eraseIdentifier(String input){
        input=eraseSpace(input);
        String fixed = "" + input;
        if(parent == null) {
            for (String s : identifiers) {
                if (fixed.toLowerCase(Locale.ROOT).startsWith(s)) {
                    fixed = fixed.replaceFirst(Pattern.compile(s,Pattern.CASE_INSENSITIVE).pattern(), "");
                    eraseSpace(fixed);
                    break;
                }
            }
        } else{
            input = parent.eraseIdentifier(input);
            for (String s : identifiers) {
                if (input.toLowerCase(Locale.ROOT).startsWith(s)) {
                    fixed = input.replaceFirst(Pattern.compile(s,Pattern.CASE_INSENSITIVE).pattern(), "");
                    eraseSpace(fixed);
                    break;
                }
            }
        }
        return fixed;
    }
    public String eraseSpace(String input){
        String ret = input;
        while(ret.startsWith(" ")){
            ret = ret.replaceFirst(" ","");
        }
        return ret;
    }
    private List<String> deliminate(String commandLine){
        List<String> deliminatedArgs = new ArrayList<>();
        String val = "";
        String fullID = getFullIdentifier();
        try {
            commandLine = commandLine.substring(0, fullID.length()).toLowerCase(Locale.ROOT) + commandLine.substring(fullID.length());
        } catch(Exception ignored){

        }
        commandLine = commandLine.replaceFirst(fullID,"");
        commandLine = eraseSpace(commandLine);
        for(int i = 0; i < commandLine.length();i++) {
            char c = commandLine.charAt(i);
            if (c == ' ' || i == commandLine.length() - 1) {
                if (i == commandLine.length() - 1) val += c;
                deliminatedArgs.add(val);
                val = "";
            } else {
                val += c;
            }
        }
        return deliminatedArgs;
    }
    private List<ParsedArgument> parse(List<String> deliminatedArgs) throws CommandException,ParseException {
        int j = 0;
        List<ParsedArgument> parsedArgs = new ArrayList<>();
        for(int i = 0; i < args.length; i++){
            Argument arg = args[i];
            if(arg instanceof RemainingJoinedArgument){
                String str = "";

                for(int k = j; k < deliminatedArgs.size(); k++){
                    str += deliminatedArgs.get(k);
                    str =  k < deliminatedArgs.size()-1 ? str+" " : str;
                }
                parsedArgs.add(arg.parse(str));
                break;
            }
            else{
                try {
                    parsedArgs.add(arg.parse(deliminatedArgs.get(j)));
                } catch(Exception e){
                    throw new CommandException("You did not supply the correct number and types of arguments.");
                }
            }
            j++;
        }
        return parsedArgs;
    }

    public Map<String,Command<USERTYPE,CHANNELTYPE>> getChildren() {
        return children;
    }
    public Collection<Command<USERTYPE,CHANNELTYPE>> collectChildren() {
        return children.values();
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
    public String[] getIdentifierArray(){
        return identifiers.toArray(new String[0]);
    }

    public CommandContext<USERTYPE,CHANNELTYPE> execute(String input, CHANNELTYPE channel) throws CommandException,ParseException {
        return toCommandContext(input, channel);
    }

    public CommandExecutor<USERTYPE,CHANNELTYPE> getExecutor() {
        return executor;
    }


    public String getDescription(){
        String childdescs = "";
        int i = 0;
        for (Command<USERTYPE, CHANNELTYPE> child : collectChildren()) {
            childdescs +=  child.getDescription();
            if(i < children.size()-1){
                childdescs += "\n";
            }
            i++;
        }
        return CommandHandler.toBold(getFullIdentifier() + " " + argumentList()) + " " + description + (childdescs.equals("") ? "" : "\n" + childdescs);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static class CommandContext<USERTYPE,CHANNELTYPE>{
        private final List<ParsedArgument> parsedArgs;
        private final CHANNELTYPE channel;
        public CommandContext(List<ParsedArgument> parsedArgs, CHANNELTYPE channel){
            this.parsedArgs=parsedArgs;
            this.channel=channel;
        }
        public <T> T getOne(String key) throws Exception{
            for(ParsedArgument pa : parsedArgs){
                if(pa.name.equals(key)){
                    return (T)pa.parsedObject;
                }
            }
            throw new Exception("The provided argument key: " + key + " was not valid.");
        }
        public CHANNELTYPE getChannel(){
            return channel;
        }
    }
    public static class CommandBuilder<USERTYPE, CHANNELTYPE>{
        private boolean isPrivate=false;
        private boolean isPublic=false;
        private String minimumRequiredRole = null;
        private final List<String> identifiers = new ArrayList<>();
        private final List<Command<USERTYPE,CHANNELTYPE>> children = new ArrayList<>();
        private Argument[] args = new Argument[0];
        private CommandExecutor<USERTYPE,CHANNELTYPE> executor;
        private String description;

        public CommandBuilder(){

        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> role(String minimumrole){
            minimumRequiredRole = minimumrole;
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> isPrivateMessageAccessible(boolean bool){
            isPrivate=bool;
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> isPublicAccessible(boolean bool){
            isPublic=bool;
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> identifiers(String... prefixes){
            identifiers.clear();
            for(String prefix : prefixes) identifiers.add(prefix);
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> child(Command<USERTYPE,CHANNELTYPE> cmd){
            children.add(cmd);
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> executor(CommandExecutor<USERTYPE,CHANNELTYPE> executor){
            this.executor = executor;
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> arguments(Argument... args){
            this.args=args;
            return this;
        }
        public CommandBuilder<USERTYPE,CHANNELTYPE> description(String desc){
            this.description = desc;
            return this;
        }
        public Command<USERTYPE,CHANNELTYPE> build(){
            return new Command<USERTYPE,CHANNELTYPE>(identifiers,minimumRequiredRole,isPublic,isPrivate,children,executor,args,description);
        }
    }
}
