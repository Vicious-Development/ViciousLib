package com.vicious.viciouslib.persistence.json;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This deserializer is designed to fix user errors.
 * These are intended to fix user error such as:
 * Typing more than 1 decimal in a decimal number: 123..45 or 1.23.45 or 123.............45
 * Mistyping 'true': treu
 * Typing a decimal when it is unnecessary (assuming the backend requires an int rather than a decimal): 123.0
 */
public class SerializationHandler {
    private static final Map<Class<?>, DSPair<?>> functs = new HashMap<>();
    static {
        registerDeserializer(Integer.class,(str)->Integer.parseInt(intForm(str)));
        registerDeserializer(int.class,(str)->Integer.parseInt(intForm(str)));
        registerDeserializer(Long.class,(str)->Long.parseLong(intForm(str)));
        registerDeserializer(long.class,(str)->Long.parseLong(intForm(str)));
        registerDeserializer(Short.class,(str)->Short.parseShort(intForm(str)));
        registerDeserializer(short.class,(str)->Short.parseShort(intForm(str)));
        registerDeserializer(Byte.class,(str)->Byte.parseByte(intForm(str)));
        registerDeserializer(byte.class,(str)->Byte.parseByte(intForm(str)));
        registerDeserializer(Double.class,(str)->Double.parseDouble(decimalForm(str)));
        registerDeserializer(double.class,(str)->Double.parseDouble(decimalForm(str)));
        registerDeserializer(Float.class,(str)->Float.parseFloat(decimalForm(str)));
        registerDeserializer(float.class,(str)->Float.parseFloat(decimalForm(str)));
        registerDeserializer(Boolean.class, SerializationHandler::booleanForm);
        registerDeserializer(boolean.class, SerializationHandler::booleanForm);
        registerHandler(String.class,(str)->str, s-> "\"" + s + "\"");
    }
    private static boolean booleanForm(String str) {
        return str.contains("t") || str.contains("T");
    }

    public static String intForm(String str){
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(Character.isDigit(c) || c == '-'){
                out.append(c);
            }
            else{
                break;
            }
        }
        return out.toString();
    }
    public static String decimalForm(String str){
        StringBuilder out = new StringBuilder();
        boolean skipPeriod = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(Character.isDigit(c)){
                out.append(c);
            }
            else if(c == '.' && !skipPeriod){
                skipPeriod=true;
                out.append(c);
            }
        }
        return out.toString();
    }

    public static <V> V deserialize(String str, Class<V> cls){
        if(str.equalsIgnoreCase("null")){
            return null;
        }
        if(functs.containsKey(cls)) {
            try {
                return (V) functs.get(cls).deserialize(str);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <V> String serialize(V v){
        if(v == null){
            return "null";
        }
        if(functs.containsKey(v.getClass())){
            DSPair<V> pair = (DSPair<V>) functs.get(v.getClass());
            return pair.serialize(v);
        }
        return v.toString();
    }

    public static <V> void registerDeserializer(Class<V> cls, Function<String,V> func){
        if(!functs.containsKey(cls)) {
            functs.put(cls, new DSPair<>(func,null));
        }
    }

    public static <V> void registerHandler(Class<V> cls, Function<String, V> deserializer, Function<V,String> serializer){
        if(!functs.containsKey(cls)){
            functs.put(cls, new DSPair<>(deserializer,serializer));
        }
    }

    private static class DSPair<V>{
        private final Function<String, V> deserializer;
        private final Function<V, String> serializer;
        private DSPair(Function<String,V> deserializer, Function<V,String> serializer){
            this.deserializer = deserializer;
            this.serializer = serializer;
        }

        public String serialize(V v){
            if(serializer == null){
                return v.toString();
            }
            else {
                return serializer.apply(v);
            }
        }

        public V deserialize(String s){
            return deserializer.apply(s);
        }
    }
}
