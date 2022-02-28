package com.vicious.viciouslib.util;

import com.vicious.viciouslib.database.tracking.JSONTrackable;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VLUtil {
    public static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean isEmptyOrNull(Object s){
        if(s == null) return true;
        if(s instanceof String) return ((String) s).isEmpty();
        else return false;
    }
    public static boolean isSameUUIDs(UUID u1, UUID u2){
        if(u1 == null || u2 == null) return false;
        else return u1.equals(u2);
    }

    public static String none(Object value) {
        if(isEmptyOrNull(value)) return "none";
        else return value.toString();
    }

    public static String dateString(long l) {
        if(Instant.now().toEpochMilli() > l) return "FOREVER";
        return DATEFORMAT.format(new Date(l));
    }

    public static void runIfNotNull(Runnable... runners) {
        for (Runnable runner : runners) {
            if(runner == null) continue;
            runner.run();
        }
    }

    public static List<String> getStringList(String in){
        String val = "";
        List<String> strs = new ArrayList<>();
        if(in == null) return strs;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if(c == ',' || c == ']'){
                strs.add(val);
                val="";
            }
            else if(c != '['){
                val+=c;
            }
        }
        return strs;
    }

    public static int subtractOrZero(int value, int subtractor) {
        return Math.max(value - subtractor, 0);
    }
    public static <T> ScheduledFuture<?> executeWhen(Predicate<T> predicator, Consumer<T> runnable, T t){
        return Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            if(predicator.test(t)) {
                runnable.accept(t);
            } },50,50, TimeUnit.MILLISECONDS);
    }

    public static <T extends JSONTrackable<T>> boolean mightBeInitialized(Class<?> targetFieldClass, Object target) {
        for (Field declaredField : target.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            if(isSubclassOfOrEqualTo(targetFieldClass,declaredField.getType())){
                try {
                    if (declaredField.get(target) == null) return false;
                } catch(IllegalAccessException ignored){}
            }
        }
        return true;
    }
    public static boolean isSubclassOfOrEqualTo(Class<?> expected, Class<?> actual){
        while(actual != null){
            if(actual.equals(expected)) return true;
            actual = actual.getSuperclass();
        }
        return false;
    }
}
