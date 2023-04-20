package com.vicious.viciouslib.persistence.vson.writer;

import com.vicious.viciouslib.persistence.vson.VSONArray;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.SerializationHandler;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class VSONWriter {
    private File file;
    public VSONWriter(String file){
        this.file=new File(file);
    }
    public void write(VSONMap map) throws IOException {
        FileWriter writer = new FileWriter(file);
        StringBuilder builder = new StringBuilder();
        writeMap(0,map,builder);
        writer.write(builder.toString());
        writer.close();
    }
    public void writeArray(int tabs, VSONArray array, StringBuilder builder){
        for (VSONValue value : array) {
            writeUnmappedValue(tabs, value, builder);
        }
    }

    private void writeUnmappedValue(int tabs, VSONValue value, StringBuilder builder) {
        if(value.get() instanceof VSONMap m) {
            tab(tabs,builder);
            builder.append("{\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else if(value.get() instanceof VSONArray a){
            tab(tabs,builder);
            builder.append("[\n");
            writeArray(tabs+1,a,builder);
            tab(tabs,builder);
            builder.append("]\n");
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

    protected void writeMap(int tabs, VSONMap map, StringBuilder builder){
        for (Map.Entry<String, VSONMapping> entry : map.entrySet()) {
            VSONMapping value = entry.getValue();
            String name = entry.getKey();
            if(value.info != null) {
                if(!value.info.hasParent()) {
                    writeValue(tabs, name, value, builder);
                }
            }
            else{
                writeValue(tabs, name, value, builder);
            }
        }
    }

    protected void writeValue(int tabs, String name, VSONValue value, StringBuilder builder) {
        if(value.info.hasDescription()) {
            String description = value.info.description();
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
        if(value.get() instanceof VSONMap m) {
            tab(tabs,builder);
            builder.append(name).append(" = {\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else if(value.get() instanceof VSONArray a){
            tab(tabs,builder);
            builder.append(name).append(" = [\n");
            writeArray(tabs+1,a,builder);
            tab(tabs,builder);
            builder.append("]\n");
        }
        else{
            tab(tabs,builder);
            builder.append(name).append(" = ");
            builder.append(SerializationHandler.serialize(value.get()));
            builder.append('\n');
        }
        if(value.hasChildren()) {
            for (NamePair entry : value.children) {
                writeValue(tabs + 1, entry.getName(), entry.getValue(), builder);
            }
        }
    }

    protected void tab(int tabs, StringBuilder builder){
        for (int i = 0; i < tabs; i++) {
            builder.append('\t');
        }
    }
}
