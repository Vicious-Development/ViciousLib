package com.vicious.viciouslib.database.tracking.values;

import com.vicious.viciouslib.database.objectTypes.LongText;
import com.vicious.viciouslib.database.objectTypes.MediumText;
import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.Trackable;
import com.vicious.viciouslib.database.tracking.interfaces.TrackableValueConverter;
import com.vicious.viciouslib.database.tracking.interfaces.TrackableValueJSONParser;
import com.vicious.viciouslib.database.tracking.interfaces.TrackableValueSQLParser;
import com.vicious.viciouslib.serialization.SerializationUtil;
import com.vicious.viciouslib.util.VLUtil;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class TrackableObject<T> extends TrackableValue<T> {
    protected static final Map<Class<?>, TrackableValueSQLParser<?>> sqlparsers = new HashMap<>();
    protected static final Map<Class<?>, TrackableValueJSONParser<?>> jsonparsers = new HashMap<>();
    //To add support for a class type, add a parser here. No Enums allowed.
    static {
        sqlparsers.put(Boolean.class,(r, t)->r.getBoolean(t.name));
        sqlparsers.put(Integer.class,(r, t)->r.getInt(t.name));
        sqlparsers.put(Double.class,(r, t)->r.getDouble(t.name));
        sqlparsers.put(Float.class,(r, t)->r.getFloat(t.name));
        sqlparsers.put(Byte.class,(r, t)->r.getByte(t.name));
        sqlparsers.put(Short.class,(r, t)->r.getShort(t.name));
        sqlparsers.put(Long.class,(r, t)->r.getLong(t.name));
        sqlparsers.put(String.class,(r, t)->r.getString(t.name));
        sqlparsers.put(UUID.class,(r, t)->UUID.fromString(r.getString(t.name)));
        sqlparsers.put(MediumText.class,(r, t)->new MediumText(r.getString(t.name)));
        sqlparsers.put(LongText.class,(r, t)->new LongText(r.getString(t.name)));
        sqlparsers.put(Date.class,(r, t)-> VLUtil.DATEFORMAT.parse(r.getString(t.name)));
        sqlparsers.put(SQLVector3i.class,(r, t)->SQLVector3i.parseVector3i(r.getString(t.name)));
    }

    static {
        jsonparsers.put(Boolean.class,(j, t)->j.getBoolean(t.name));
        jsonparsers.put(Integer.class,(j, t)->j.getInt(t.name));
        jsonparsers.put(Double.class,(j, t)->j.getDouble(t.name));
        jsonparsers.put(Float.class,(j, t)->j.getFloat(t.name));
        //Idk if this works so if it errors out. Blame the Thon.
        jsonparsers.put(Byte.class,(j, t)->j.getInt(t.name));
        jsonparsers.put(Short.class,(j, t)->j.getInt(t.name));
        jsonparsers.put(Long.class,(j, t)->j.getLong(t.name));
        jsonparsers.put(String.class,(j, t)->j.getString(t.name));
        jsonparsers.put(UUID.class,(j, t)->UUID.fromString(j.getString(t.name)));
        jsonparsers.put(MediumText.class,(j, t)->new MediumText(j.getString(t.name)));
        jsonparsers.put(LongText.class,(j, t)->new LongText(j.getString(t.name)));
        jsonparsers.put(Date.class,(j, t)-> VLUtil.DATEFORMAT.parse(j.getString(t.name)));
        jsonparsers.put(SQLVector3i.class,(j, t)->SQLVector3i.parseVector3i(j.getString(t.name)));
    }
    //Some objects don't get converted into SQL language properly, registering a SQLConverter ensures that the string provided is compatible.

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
        try {
            this.setWithoutUpdate(((TrackableValueSQLParser<T>) sqlparsers.get(type)).parse(rs, this));
        } catch(Exception e){
            this.setWithoutUpdate(null);
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromJSON(JSONObject jo) {
        try {
            this.setWithoutUpdate(((TrackableValueJSONParser<T>) jsonparsers.get(type)).parse(jo, this));
        } catch(Exception e){
            this.setWithoutUpdate(null);
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromStringWithUpdate(String s) throws Exception{
        try {
            this.set((T) SerializationUtil.parse(type,s));
        } catch(Exception e){
            this.set(null);
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromStringWithoutUpdate(String s) {
        try {
            this.setWithoutUpdate((T) SerializationUtil.parse(type,s));
        } catch(Exception e){
            this.setWithoutUpdate(null);
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> converter(TrackableValueConverter converter){
        return (TrackableObject<T>) super.converter(converter);
    }
}