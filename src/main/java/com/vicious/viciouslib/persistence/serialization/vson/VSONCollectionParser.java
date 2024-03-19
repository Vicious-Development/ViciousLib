package com.vicious.viciouslib.persistence.serialization.vson;

import com.vicious.viciouslib.persistence.serialization.generic.CollectionParser;
import com.vicious.viciouslib.persistence.vson.value.VSONException;
import com.vicious.viciouslib.util.quick.ObjectList;

import java.io.InputStream;

public class VSONCollectionParser implements CollectionParser {
    private final ObjectList arr = new ObjectList();
    private final InputStream stream;
    public VSONCollectionParser(InputStream stream){
        this.stream=stream;
        parse();
    }

    private void parse() {
        while(hasData()){
            char c = (char) read();
            if(c == ',' || c == '\n'){
                char l = skipWhiteSpace();
                if(add(l)){
                    return;
                }
            }
            else if(c == ']'){
                return;
            }
            else if(!Character.isWhitespace(c)){
                add(c);
            }
        }
    }

    private boolean add(char c){
        try {
            VSONObjectParser value = new VSONObjectParser(getInputStream(),c);
            Object obj = value.getObject();
            if(obj != VSONObjectParser.AssumedType.DELIMITER) {
                getCollection().add(obj);
                return false;
            }
            return true;
        } catch (Exception e){
            throw new VSONException("could not parse array value", e);
        }
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }

    @Override
    public ObjectList getCollection() {
        return arr;
    }
}
