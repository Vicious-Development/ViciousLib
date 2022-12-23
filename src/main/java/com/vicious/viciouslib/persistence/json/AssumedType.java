package com.vicious.viciouslib.persistence.json;

public class AssumedType {
    public String string = "";
    public Class<?> type = Object.class;
    public AssumedType append(char c){
        string+=c;
        updateType(c);
        return this;
    }
    private void updateType(char c) {
        if(type != String.class) {
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
