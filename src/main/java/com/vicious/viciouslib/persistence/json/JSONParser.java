package com.vicious.viciouslib.persistence.json;

public class JSONParser {
    protected int i = 0;
    protected AssumedType parseValue(String line){
        AssumedType type = new AssumedType();
        if(i >= line.length()) return type;
        if(line.charAt(i) == '{'){
            return new AssumedType.Map();
        }
        if(line.charAt(i) == '['){
            return new AssumedType.Array();
        }
        for (;i < line.length(); i++) {
            char c = line.charAt(i);
            if(c != '\n'){
                type.append(c);
            }
        }
        return type;
    }
    protected void skipSyntax(String line) {
        while (i < line.length() && (Character.isWhitespace(line.charAt(i)) || line.charAt(i) == '=')){
            i++;
        }
    }
}
