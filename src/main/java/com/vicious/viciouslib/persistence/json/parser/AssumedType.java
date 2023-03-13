package com.vicious.viciouslib.persistence.json.parser;

public class AssumedType {
    private boolean isLocked = false;
    public String string = "";
    public Class<?> type = Object.class;
    public AssumedType append(char c){

        updateType(c);
        if(c != '"') {
            string += c;
        }
        return this;
    }
    private void updateType(char c) {
        if(c == '"'){
            type = String.class;
            isLocked = true;
        }
        else if(!isLocked) {
            if(isNumber()){
                if(c == '.') {
                    type = Double.class;
                }
            }
            else{
                if(Character.isDigit(c) || c == '-'){
                    type=Integer.class;
                }
                if (Character.isLetter(c)) {
                    type = String.class;
                }
            }
        }
        else if(string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false")){
            type = Boolean.class;
        }
    }

    public boolean isNumber(){
        return Number.class.isAssignableFrom(type);
    }

    public boolean isEmpty() {
        return string.isEmpty();
    }

    public static class Map extends AssumedType { }
    public static class Array extends AssumedType { }
}
