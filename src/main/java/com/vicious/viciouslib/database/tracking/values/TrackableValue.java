package com.vicious.viciouslib.database.tracking.values;

import com.vicious.viciouslib.database.objectTypes.LongText;
import com.vicious.viciouslib.database.objectTypes.MediumText;
import com.vicious.viciouslib.database.tracking.Trackable;
import com.vicious.viciouslib.database.tracking.TrackingHandler;
import com.vicious.viciouslib.database.tracking.interfaces.SQLConverter;
import com.vicious.viciouslib.database.tracking.interfaces.TrackableValueConverter;
import com.vicious.viciouslib.util.VLUtil;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TrackableValue<T> {
    public static TrackingHandler globalHandler;
    public static final Map<Class<?>, SQLConverter> sqlconverters = new HashMap<>();
    public static final Map<Class<?>, Function<Object,String>> universalConverters = new HashMap<>();

    public final T defaultSetting;
    protected T setting;
    public final Class<T> type;
    public final String name;
    public Trackable<?> tracker;
    protected TrackableValueConverter converter;

    protected TrackableValue(String name, Supplier<T> defaultSetting, Trackable<?> tracker){
        this.name=name;
        this.defaultSetting=defaultSetting.get();
        this.type= (Class<T>) this.defaultSetting.getClass();
        this.setting=defaultSetting.get();
        this.tracker=tracker;
    }
    public TrackableValue(String name, Supplier<T> defaultSetting, Trackable<?> tracker, Class<T> type) {
        this.name=name;
        this.defaultSetting=defaultSetting.get();
        this.type= type;
        this.setting=defaultSetting.get();
        this.tracker=tracker;
    }

    static {
        //For when .toString isn't enough (SQL syntax does not support certain date formats, and doesn't autoconvert booleans).
        sqlconverters.put(Date.class, VLUtil.DATEFORMAT::format);
        sqlconverters.put(MediumText.class, (t)-> t.toString().replaceAll("'","''"));
        sqlconverters.put(LongText.class, (t)-> t.toString().replaceAll("'","''"));
        sqlconverters.put(String.class, (t)-> t.toString().replaceAll("'","''"));
        sqlconverters.put(Boolean.class, (t)-> {
            if((Boolean)t) return "1";
            return "0";
        });
        universalConverters.put(Class.class, (t)-> ((Class<?>)t).getCanonicalName());
    }


    public static <V> TrackableValue<V> fromString(String substring, Class<V> type) {
        TrackableObject<V> obj = new TrackableObject<>("",()->null,null,type);
        obj.setFromStringWithoutUpdate(substring);
        return obj.value() == null ? null : obj;
    }

    public T value() {
        return setting;
    }

    public String SQLString(){
        if(setting == null) return null;
        if(sqlconverters.containsKey(setting.getClass())) return sqlconverters.get(setting.getClass()).convert(this.value());
        if(universalConverters.containsKey(setting.getClass())) return universalConverters.get(setting.getClass()).apply(this.value());
        return setting.toString();
    }
    public void convert(){
        if(converter != null) converter.convert(this);
    }
    //Used to convert a trackable's value into something else.
    public TrackableValue<T> converter(TrackableValueConverter converter){
        this.converter=converter;
        return this;
    }
    public TrackableValue<T> set(T setting){
        //Failure is expected only if the dev screwed up.
        this.setting=setting;
        //Mark dirty.
        tracker.markDirty(name,SQLString());
        return this;
    }

    public TrackableValue<T> setWithoutUpdate(T setting){
        //Failure is expected only if the dev screwed up.
        this.setting=setting;
        return this;
    }
    public abstract TrackableValue<T> setFromSQL(ResultSet rs) throws Exception;
    public abstract TrackableValue<T> setFromJSON(JSONObject jo) throws Exception;
    public abstract TrackableValue<T> setFromStringWithUpdate(String s) throws Exception;
    public abstract TrackableValue<T> setFromStringWithoutUpdate(String s) throws Exception;
    public String toString(){
        return setting.toString();
    }

    public TrackableValue<T> setUnchecked(Object setting) {
        //Failure is expected only if the dev screwed up.
        this.setting=(T)setting;
        //Mark dirty.
        tracker.markDirty(name,SQLString());
        return this;
    }

    public Object getJSONValue(){
        if(setting == null) return null;
        if(universalConverters.containsKey(type)) return universalConverters.get(type).apply(this.value());
        return setting.toString();
    }
}
