package com.vicious.viciouslib.persistence.vson.writer;

import com.vicious.viciouslib.persistence.vson.SerializationHandler;
import com.vicious.viciouslib.persistence.vson.VSONArray;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.value.IHasChildren;
import com.vicious.viciouslib.persistence.vson.value.IHasDescription;
import com.vicious.viciouslib.persistence.vson.value.VSONValue;
import com.vicious.viciouslib.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class VSONWriter {
    private File file;
    public VSONWriter(String file){
        this.file=new File(FileUtil.resolve(file));
    }
    public void write(VSONMap map) throws IOException {
        FileWriter writer = new FileWriter(file);
        StringBuilder builder = new StringBuilder();
        writeMap(0,map,builder);
        writer.write(builder.toString());
        writer.close();
    }
    public void writeArray(int tabs, Collection<?> array, StringBuilder builder){
        for (Object value : array) {
            if(value instanceof VSONValue) {
                writeUnmappedValue(tabs, value, builder);
            }
            else{
                writeUnmappedValue(tabs,value,builder);
            }
        }
    }

    private void writeUnmappedValue(int tabs, Object value, StringBuilder builder) {
        if(value instanceof VSONValue v){
            value = v.get();
        }
        if(value instanceof VSONMap m) {
            tab(tabs,builder);
            builder.append("{\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else if(value instanceof VSONArray a){
            tab(tabs,builder);
            builder.append("[\n");
            writeArray(tabs+1,a,builder);
            tab(tabs,builder);
            builder.append("]\n");
        }
        else{
            tab(tabs,builder);
            boolean string = value instanceof String;
            if(string){
                builder.append('\"');
            }
            builder.append(value);
            if(string) {
                builder.append('\"');
            }
            builder.append('\n');
        }
    }

    protected void writeMap(int tabs, Map<?,?> map, StringBuilder builder){
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            Object key = entry.getKey();
            if(value instanceof VSONValue v) {
                if (v.info != null) {
                    if (!v.info.hasParent()) {
                        writeValue(tabs, key instanceof String ? (String) key : SerializationHandler.serialize(key), v, builder);
                    }
                } else {
                    writeValue(tabs, key instanceof String ? (String) key : SerializationHandler.serialize(key), v, builder);
                }
            }
            else{
                writeValue(tabs,key instanceof String ? (String) key : SerializationHandler.serialize(key),value,builder);
            }
        }
    }

    protected void writeValue(int tabs, String name, Object value, StringBuilder builder) {
        if(value instanceof IHasDescription descr) {
            if (descr.hasDescription()) {
                String description = descr.getDescription();
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
        }
        Object v = value;
        if(v instanceof VSONValue){
            v = ((VSONValue)v).get();
        }
        if(v instanceof Map m) {
            tab(tabs,builder);
            builder.append(name).append(" = {\n");
            writeMap(tabs+1,m,builder);
            tab(tabs,builder);
            builder.append("}\n");
        }
        else if(v instanceof Collection a){
            tab(tabs,builder);
            builder.append(name).append(" = [\n");
            writeArray(tabs+1,a,builder);
            tab(tabs,builder);
            builder.append("]\n");
        }
        else{
            tab(tabs,builder);
            builder.append(name).append(" = ");
            builder.append(SerializationHandler.serialize(v));
            builder.append('\n');
        }
        if(value instanceof IHasChildren chld) {
            if (chld.hasChildren()) {
                for (NamePair entry : chld.getChildren()) {
                    writeValue(tabs + 1, entry.getName(), entry.getValue(), builder);
                }
            }
        }
    }

    protected void tab(int tabs, StringBuilder builder){
        for (int i = 0; i < tabs; i++) {
            builder.append('\t');
        }
    }
}
