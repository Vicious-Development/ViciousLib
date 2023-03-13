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
public class Deserializer {
    private static final Map<Class<?>, Function<String,?>> deserializers = new HashMap<>();
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
        registerDeserializer(Boolean.class, Deserializer::booleanForm);
        registerDeserializer(boolean.class, Deserializer::booleanForm);
        registerDeserializer(String.class,(str)->str);
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

    public static <V> V fix(String str, Class<V> cls){
        if(deserializers.containsKey(cls)) {
            try {
                return (V) deserializers.get(cls).apply(str);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <V> void registerDeserializer(Class<V> cls, Function<String,V> func){
        deserializers.put(cls,func);
    }
}
