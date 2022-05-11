package com.vicious.viciouslib.database.tracking.values;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.tracking.Trackable;
import com.vicious.viciouslib.database.tracking.interfaces.TrackableValueConverter;
import com.vicious.viciouslib.serialization.SerializationUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.function.Supplier;

public class TrackableObject<T> extends TrackableValue<T> {
    public TrackableObject(String name, Supplier<T> defaultSetting, Trackable<?> tracker){
        super(name, defaultSetting, tracker);

    }

    public TrackableObject(String name, Supplier<T> defaultSetting, Trackable<?> tracker, Class<T> type) {
        super(name, defaultSetting, tracker,type);
    }

    public TrackableObject<T> set(T setting){
        return (TrackableObject<T>) super.set(setting);
    }

    public TrackableObject<T> setWithoutUpdate(T setting){
        return (TrackableObject<T>) super.setWithoutUpdate(setting);
    }

    public TrackableObject<T> setFromSQL(ResultSet rs) throws Exception{
        if(type == Integer.class) setWithoutUpdate((T)(Integer)rs.getInt(name));
        else if(type == Short.class) setWithoutUpdate((T)(Short)rs.getShort(name));
        else if(type == Long.class) setWithoutUpdate((T)(Long)rs.getLong(name));
        else if(type == Byte.class) setWithoutUpdate((T)(Byte)rs.getByte(name));
        else if(type == Double.class) setWithoutUpdate((T)(Double)rs.getDouble(name));
        else if(type == Float.class) setWithoutUpdate((T)(Float)rs.getFloat(name));
        else if(type == Boolean.class) setWithoutUpdate((T)(Boolean)rs.getBoolean(name));
        else{
            try {
                this.setWithoutUpdate((T) SerializationUtil.parse(type, rs.getString(name)));
            } catch (Exception e) {
                LoggerWrapper.logError(e.getMessage());
                e.printStackTrace();
            }
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromJSON(JSONObject jo) throws JSONException{
        try {
            Object o = jo.get(this.name);
            //Prevent really weird class cast exceptions outside of the trackable.
            if(o instanceof Number){
                //I hate this but its necessary. Java why.
                Number n = (Number) o;
                if(type == Long.class) this.setWithoutUpdate((T)((Long)n.longValue()));
                if(type == Byte.class) this.setWithoutUpdate((T)((Byte)n.byteValue()));
                if(type == Short.class) this.setWithoutUpdate((T)((Short)n.shortValue()));
                if(type == Integer.class) this.setWithoutUpdate((T)((Integer)n.intValue()));
                if(type == Double.class) this.setWithoutUpdate((T)((Double)n.doubleValue()));
                if(type == Float.class) this.setWithoutUpdate((T)((Float)n.floatValue()));
            }
            else this.setWithoutUpdate((T) SerializationUtil.parse(type,o));
        }
        catch(Exception e){
            if(e instanceof JSONException){
                throw (JSONException) e;
            }
            else{
                LoggerWrapper.logError(e.getMessage());
                e.printStackTrace();
            }
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromStringWithUpdate(String s) throws Exception{
        try {
            this.set((T) SerializationUtil.parse(type,s));
        } catch(Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromStringWithoutUpdate(String s) {
        try {
            this.setWithoutUpdate((T) SerializationUtil.parse(type,s));
        } catch(Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        this.convert();
        return this;
    }

    public TrackableObject<T> converter(TrackableValueConverter converter){
        return (TrackableObject<T>) super.converter(converter);
    }
}