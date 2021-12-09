package com.vicious.viciouslib.command;

import java.text.ParseException;

public class Argument<T> {
    public String name = "";
    private Class<T> type;
    public Argument(String name, Class<T> type){
        this.name=name;
        this.type=type;
    }
    public <T> ParsedArgument<T> parse(String argument) throws ParseException {
        if(type.equals(String.class)){
            return new ParsedArgument<>(name,(T)argument);
        }
        else if(type.equals(Integer.class)){
            return new ParsedArgument<>(name,(T)((Integer)Integer.parseInt(argument)));
        }
        else if(type.equals(Double.class)){
            return new ParsedArgument<>(name,(T)((Double)Double.parseDouble(argument)));
        }
        else if(type.equals(Float.class)){
            return new ParsedArgument<>(name,(T)((Float)Float.parseFloat(argument)));
        }
        else if(type.equals(Long.class)){
            return new ParsedArgument<>(name,(T)((Long)Long.parseLong(argument)));
        }
        else if(type.equals(Short.class)){
            return new ParsedArgument<>(name,(T)((Short)Short.parseShort(argument)));
        }
        else if(type.equals(Character.class)){
            return new ParsedArgument<>(name,(T)((Character)argument.charAt(0)));
        }
        else if(type.equals(Boolean.class)){
            return new ParsedArgument<>(name,(T)((Boolean)Boolean.parseBoolean(argument)));
        }

        throw new ParseException("Failed to parse the argument. Usually this means that the parser does not support the arg type.",0);
    }
    public String toString(){
        String t = type.getSimpleName();
        if(type.equals(Boolean.class)) return "<true/false>";
        if(type.equals(String.class)) return "<text:" + name + ">";
        return "<" + t + ":" + name + ">";
    }
}
