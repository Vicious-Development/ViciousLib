package com.vicious.viciouslib.persistence.serialization.generic;

import java.io.IOException;
import java.io.InputStream;

public interface Parser {
    InputStream getInputStream();
    default boolean hasData(){
        try {
            return getInputStream().available() > 0;
        } catch (IOException e) {
            return false;
        }
    }
    default int read(){
        try {
            return getInputStream().read();
        } catch (IOException e) {
            throw new RuntimeException("Could not parse due to an IO exception!", e);
        }
    }

    default String trimWhiteSpace(String data){
        int beg = 0;
        while(beg < data.length() && Character.isWhitespace(data.charAt(beg))){
            beg++;
        }
        int end = data.length()-1;
        while(end >= 0 && Character.isWhitespace(data.charAt(end))){
            end--;
        }
        if(end == -1){
            return "";
        }
        return data.substring(beg,end+1);
    }
    default char skipWhiteSpace() {
        while(hasData()){
            char c = (char) read();
            if(!Character.isWhitespace(c)){
                return c;
            }
        }
        return '\n';
    }
}
