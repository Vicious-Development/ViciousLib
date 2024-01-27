package com.vicious.viciouslib.persistence.serialization.vson;

import com.vicious.viciouslib.persistence.serialization.generic.ObjectParser;
import com.vicious.viciouslib.persistence.vson.SerializationHandler;

import java.io.InputStream;

public class VSONObjectParser implements ObjectParser, VSONParser {

    private final InputStream inputStream;
    private final AssumedType type;
    private final Object object;

    public VSONObjectParser(InputStream inputStream, char lastChar) {
        this.inputStream = inputStream;
        this.type = AssumedType.of(lastChar);
        if(type == AssumedType.MAP){
            object=new VSONMapParser(inputStream).getMap();
        }
        else if(type == AssumedType.COLLECTION){
            object=new VSONCollectionParser(inputStream).getCollection();
        }
        else if(type == AssumedType.DELIMITER){
            object = AssumedType.DELIMITER;
        }
        else{
            this.object = parse(lastChar);
        }
    }

    private Object parse(char first) {
        StringBuilder value = new StringBuilder();
        if(type != AssumedType.STRING || first != '"') {
            value.append(first);
        }
        boolean hasEnded = !forceString();
        boolean hasDecimal=false;
        boolean isEscaped = false;
        while(hasData()){
            char c = (char) read();
            if(isDeliminator(c) && hasEnded){
                break;
            }
            else {
                if(!isEscaped) {
                    if (type == AssumedType.STRING && c == '"') {
                        hasEnded = true;
                    }
                    if (type == AssumedType.STRING && c == '\\') {
                        isEscaped = true;
                        continue;
                    }
                    if (type == AssumedType.NUMBER && c == '.') {
                        hasDecimal = true;
                    }
                }
                else{
                    isEscaped=false;
                }
                value.append(c);
            }
        }
        String result = value.toString();
        result = trimWhiteSpace(result);
        if(forceString()){
            if(result.endsWith("\"")){
                result = result.substring(0,result.length()-1);
            }
        }
        if(type == AssumedType.STRING){
            return result;
        }
        else if(type == AssumedType.BOOLEAN){
            return SerializationHandler.deserialize(result,boolean.class);
        }
        else if(type == AssumedType.NUMBER) {
            char last = result.charAt(result.length()-1);
            if(last == 'f' || last == 'F'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),float.class);
            }
            if(last == 'd' || last == 'D'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),double.class);
            }
            if(last == 'b' || last == 'B'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),byte.class);
            }
            if(last == 'i' || last == 'I'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),int.class);
            }
            if(last == 's' || last == 'S'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),short.class);
            }
            if(last == 'l' || last == 'L'){
                return SerializationHandler.deserialize(result.substring(0,result.length()-1),long.class);
            }
            return hasDecimal ? SerializationHandler.deserialize(result,double.class) : SerializationHandler.deserialize(result,long.class);
        }
        else if(type == AssumedType.CHAR){
            return value.charAt(1);
        }
        else{
            return result;
        }
    }

    private boolean forceString(){
        return type == AssumedType.STRING;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    public enum AssumedType{
        STRING,
        CHAR,
        NUMBER,
        COLLECTION,
        BOOLEAN,
        DELIMITER,
        MAP;

        public static AssumedType of(char c) {
            c = Character.toLowerCase(c);
            if(defaultDeliminators.contains(c)){
                return DELIMITER;
            }
            if(c == '"'){
                return STRING;
            }
            if(Character.isDigit(c) || c == '-'){
                return NUMBER;
            }
            if(c == '{'){
                return MAP;
            }
            if(c == '['){
                return COLLECTION;
            }
            if(c == '\''){
                return CHAR;
            }
            if(c == 't'){
                return BOOLEAN;
            }
            if (c == 'f') {
                return BOOLEAN;
            }
            return STRING;
        }
    }
}
