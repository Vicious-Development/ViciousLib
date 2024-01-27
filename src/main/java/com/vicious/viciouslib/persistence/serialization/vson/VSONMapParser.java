package com.vicious.viciouslib.persistence.serialization.vson;

import com.vicious.viciouslib.persistence.serialization.generic.MapParser;
import com.vicious.viciouslib.persistence.vson.value.VSONException;
import com.vicious.viciouslib.util.quick.ObjectMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VSONMapParser implements MapParser {
    private final InputStream inputStream;
    private final ObjectMap map = new ObjectMap();

    public VSONMapParser(InputStream inputStream) {
        this.inputStream = inputStream;
        parse();
    }
    public VSONMapParser(String filePath) throws IOException {
        inputStream = Files.newInputStream(Paths.get(filePath));
    }

    public void parse(){
        StringBuilder name = new StringBuilder();
        while(hasData()){
            char c = (char) read();
            if(c == '='){
                char l = skipWhiteSpace();
                VSONObjectParser value = new VSONObjectParser(getInputStream(),l);
                String n = trimWhiteSpace(name.toString());
                name = new StringBuilder();
                try {
                    getMap().put(n, value.getObject());
                } catch (Exception e){
                    throw new VSONException("could not parse map entry with key: " + n,e);
                }
            }
            else if(c == ',' || c == '\n'){
                name = new StringBuilder();
            }
            else if(c == '}'){
                return;
            }
            else{
                name.append(c);
            }
        }
    }



    @Override
    public ObjectMap getMap() {
        return map;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    private enum Stage{
        NAME,
        VALUE
    }
}
