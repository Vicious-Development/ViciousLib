package com.vicious.viciouslib.persistence.vson.parser;

import java.io.IOException;
import java.io.InputStream;

public abstract class VSONParser {
    protected InputStream fis;
    public boolean hasTraversed=false;
    public boolean completed = false;


    public boolean hasData(){
        try {
            return fis.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }
    protected abstract void start(InputStream fis);

    protected char read() {
        try {
            return (char) fis.read();
        } catch (IOException e) {
            throw new RuntimeException("ERROR READING!", e);
        }
    }

    public String removeEdgeWhiteSpace(String data){
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
}
