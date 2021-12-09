package com.vicious.viciouslib.command;

public class ParsedArgument<T> {
    public T parsedObject;
    public String name;
    public ParsedArgument(String name, T object){
        parsedObject=object;
        this.name=name;
    }
}
