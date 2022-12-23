package com.vicious.viciouslib.persistence.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JSONWriter {
    private File file;
    public JSONWriter(String file){
        this.file=new File(file);
    }
    public void write(JSONMap map) throws IOException {
        FileWriter writer = new FileWriter(file);
        StringBuilder builder = new StringBuilder();
        writeMap(0,map,builder);
        writer.write(builder.toString());
        writer.close();
    }
    public void writeArray(int tabs, JSONArray array, StringBuilder builder){
        for (JSONValue value : array) {
            writeUnmappedValue(tabs, value, builder);
        }
    }

    private void writeUnmappedValue(int tabs, JSONValue value, StringBuilder builder) {
        if(value.get() instanceof JSONMap m) {
            tab(tabs,builder);
            builder.append("{\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else{
            tab(tabs,builder);
            boolean string = value.get() instanceof String;
            if(string){
                builder.append('\"');
            }
            builder.append(value.get());
            if(string) {
                builder.append('\"');
            }
            builder.append('\n');
        }
    }

    protected void writeMap(int tabs, JSONMap map, StringBuilder builder){
        for (Map.Entry<String, JSONMapping> entry : map.entrySet()) {
            JSONMapping value = entry.getValue();
            String name = entry.getKey();
            if(value instanceof JSONMapping.Persistent p) {
                if(!p.hasParent()) {
                    writeValue(tabs, name, value, builder);
                }
            }
            else{
                writeValue(tabs, name, value, builder);
            }
        }
    }

    protected void writeValue(int tabs, String name, JSONMapping value, StringBuilder builder) {
        if(value instanceof JSONMapping.Persistent persistent) {
            String description = persistent.description;
            if (description != null && !description.isEmpty()) {
                //Doing this manually to reduce runtime.
                tab(tabs, builder);
                builder.append("#");
                for (int i = 0; i < description.length(); i++) {
                    char c = description.charAt(i);
                    builder.append(c);
                    if (c == '\n') {
                        tab(tabs, builder);
                        builder.append("#");
                    }
                }
                builder.append('\n');
            }
        }
        if(value.get() instanceof JSONMap m) {
            tab(tabs,builder);
            builder.append(name).append(" = {\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else if(value.get() instanceof JSONArray a){
            tab(tabs,builder);
            builder.append(name).append(" = [\n");
            writeArray(tabs+1,a,builder);
            tab(tabs,builder);
            builder.append("]\n");
        }
        else{
            tab(tabs,builder);
            boolean string = value.get() instanceof String;
            builder.append(name).append(" = ");
            if(string){
                builder.append('\"');
            }
            builder.append(value.get());
            if(string) {
                builder.append('\"');
            }
            builder.append('\n');
        }
        if(value instanceof JSONMapping.Persistent persistent) {
            for (Map.Entry<String, JSONMapping.Persistent> entry : persistent.children) {
                writeValue(tabs + 1, entry.getKey(), entry.getValue(), builder);
            }
        }
    }

    protected void tab(int tabs, StringBuilder builder){
        for (int i = 0; i < tabs; i++) {
            builder.append('\t');
        }
    }
}
