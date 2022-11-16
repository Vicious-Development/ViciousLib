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
    private static final Map<Class<?>, Function<String,?>> reserializers = new HashMap<>();
    static {
        registerReserializer(Integer.class,(str)->Integer.parseInt(intForm(str)));
        registerReserializer(int.class,(str)->(int)Integer.parseInt(intForm(str)));
        registerReserializer(Long.class,(str)->Long.parseLong(intForm(str)));
        registerReserializer(long.class,(str)->Long.parseLong(intForm(str)));
        registerReserializer(Short.class,(str)->Short.parseShort(intForm(str)));
        registerReserializer(short.class,(str)->Short.parseShort(intForm(str)));
        registerReserializer(Byte.class,(str)->Byte.parseByte(intForm(str)));
        registerReserializer(byte.class,(str)->Byte.parseByte(intForm(str)));
        registerReserializer(Double.class,(str)->Double.parseDouble(decimalForm(str)));
        registerReserializer(double.class,(str)->Double.parseDouble(decimalForm(str)));
        registerReserializer(Float.class,(str)->Float.parseFloat(decimalForm(str)));
        registerReserializer(float.class,(str)->Float.parseFloat(decimalForm(str)));
        registerReserializer(Boolean.class, Deserializer::booleanForm);
        registerReserializer(boolean.class, Deserializer::booleanForm);
        registerReserializer(String.class,(str)->str);
    }
    private static boolean booleanForm(String str) {
        return str.contains("t") || str.contains("T");
    }

    public static String intForm(String str){
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(Character.isDigit(c)){
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
        if(reserializers.containsKey(cls)) {
            try {
                return (V)reserializers.get(cls).apply(str);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <V> void registerReserializer(Class<V> cls, Function<String,V> func){
        reserializers.put(cls,func);
    }
}
