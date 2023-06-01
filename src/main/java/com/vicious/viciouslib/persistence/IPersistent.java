package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.network.packet.IPacket;
import com.vicious.viciouslib.persistence.storage.AttrInfo;
import com.vicious.viciouslib.persistence.vson.VSONMap;
import com.vicious.viciouslib.persistence.vson.value.VSONMapping;

public interface IPersistent {
    default void load(){
        PersistenceHandler.load(this);
    }
    default void save(){
        PersistenceHandler.save(this);
    }

    interface Metaful extends IPersistent {
        VSONMap getMetaMap();
        default void defaultMeta(String key, Object meta){
            if(!getMetaMap().containsKey(key)) {
                getMetaMap().put(key, meta);
            }
        }
        default <T> T getMeta(String key, Class<T> type){
            if(getMetaMap().containsKey(key)) {
                return getMetaMap().get(key).softAs(type);
            }
            else{
                return null;
            }
        }
        default void writeMeta(String key, Object meta){
            if(getMetaMap().containsKey(key)){
                getMetaMap().replace(key,new VSONMapping(meta,new AttrInfo.Named(key)));
            }
            else{
                getMetaMap().put(key,meta);
            }
        }

        default void loadMeta(VSONMap vsonMap){
            if(vsonMap != null) {
                VSONMap meta = getMetaMap();
                meta.clear();
                meta.putAll(vsonMap);
            }
        }
    }
}
