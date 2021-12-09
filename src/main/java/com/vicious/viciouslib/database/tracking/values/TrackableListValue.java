package com.vicious.viciouslib.database.tracking.values;

import com.vicious.viciouslib.database.tracking.Trackable;
import com.vicious.viciouslib.serialization.SerializationUtil;
import com.vicious.viciouslib.util.VCUtil;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class TrackableListValue<T extends List<V>,V> extends TrackableObject<T>{
    public final Class<V> LISTTYPE;
    public TrackableListValue(String name, Supplier<T> defaultSetting, Trackable<?> tracker, Class<V> listType) {
        super(name, defaultSetting, tracker);
        LISTTYPE=listType;
    }

    public static <E> TrackableListValue<ArrayList<E>,E> listFromString(String value, Class<E> listType) {
        TrackableListValue<ArrayList<E>,E> obj = new TrackableListValue<>("", ArrayList::new,null,listType);
        obj.setWithoutUpdate((ArrayList<E>) obj.parseList(value));
        return obj;
    }

    public String SQLString(){
        if(setting == null) return null;
        return setting.toString();
    }
    public TrackableObject<T> setFromSQL(ResultSet rs) throws Exception{
        try {
            Class<? super T> typetouse = type.getSuperclass();
            if(typetouse == null) typetouse = type;
            this.setWithoutUpdate((T) parseList(rs.getString(name)));
        } catch(Exception e){
            this.setWithoutUpdate(null);
        }
        this.convert();
        return this;
    }
    public TrackableObject<T> setFromJSON(JSONObject jo) {
        try {
            Class<? super T> typetouse = type.getSuperclass();
            if(typetouse == null) typetouse = type;
            this.setWithoutUpdate((T) parseList(jo.getString(name)));
        } catch(Exception e){
            this.setWithoutUpdate(null);
        }
        this.convert();
        return this;
    }
    private List<V> parseList(String in) {
        String val = "";
        List<V> objs = new ArrayList<>();
        if(VCUtil.isEmptyOrNull(in)) return objs;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if(c == ',' || c == ']'){
                try {
                    objs.add((V) SerializationUtil.parse(LISTTYPE,val));
                } catch(Exception e){
                    System.err.println("Failed to print: " + e.getMessage());
                    e.printStackTrace();
                }
                val="";
            }
            else if(c != '['){
                val+=c;
            }
        }
        return objs;
    }
    public V get(int i){
        return setting.get(i);
    }
    public V remove(int i){
        tracker.markDirty(name,setting);
        return setting.remove(i);
    }
    public boolean remove(V val){
        if(setting.remove(val)){
            tracker.markDirty(name,setting);
            return true;
        }
        return false;
    }

    public TrackableListValue<T,V> add(V val){
        setting.add(val);
        tracker.markDirty(name,setting);

        return this;
    }
    public TrackableListValue<T,V> add(int pos, V val){
        setting.add(pos,val);
        tracker.markDirty(name,setting);

        return this;
    }
    public TrackableListValue<T,V> set(int pos, V val){
        setting.set(pos,val);
        tracker.markDirty(name,setting);

        return this;
    }
    public int size(){
        return setting.size();
    }
    public boolean contains(Object val){
        return setting.contains(val);
    }
    public TrackableListValue<T,V> clear(){
        setting.clear();
        tracker.markDirty(name,setting);
        return this;
    }
    public TrackableListValue<T,V> addAll(Collection<V> vals){
        setting.addAll(vals);
        tracker.markDirty(name,setting);
        return this;
    }
}
