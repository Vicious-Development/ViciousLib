package com.vicious.viciouslib.database.tracking.concurrency;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.sqlcomponents.SQLCommand;
import com.vicious.viciouslib.database.tracking.Trackable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Stores trackable data locally and only updates the memory when an update request is received for a trackable that exists.
 * @param <K>
 * @param <V>
 */
public class TrackableConcurrencyMap<K,V extends Trackable<V>> extends HashMap<K, ConcurrentDataEntry<V>> {
    private Supplier<V> constructor;
    private Function<Object,V> entryConstructor;
    private Function<K,SQLCommand> updateCommand;
    public TrackableConcurrencyMap(Supplier<V> constructor, Function<K,SQLCommand> updateCommand, Function<Object,V> entryConstructor){
        this.constructor=constructor;
        this.updateCommand=updateCommand;
        this.entryConstructor=entryConstructor;
    }
    public ConcurrentDataEntry<V> get(Object k){
        ConcurrentDataEntry<V> entry = super.get(k);
        if(entry == null){
            entry = new ConcurrentDataEntry<>(Trackable.objectFromSQL(updateCommand.apply((K) k),constructor));
            if(entry.value == null) {
                entry.value=entryConstructor.apply(k);
                try {
                    Trackable.handler.getDB().safeExecute(entry.value.getSQLNewCommand(), entry.value);
                } catch (SQLException e) {
                    LoggerWrapper.logError("Failed to add a data value to the database.");
                    e.printStackTrace();
                }
            }
            put((K) k,entry);
        }
        else if(entry.isDirty){
            update((K) k,entry);
        }
        return entry;
    }
    public V filterOrElse(Predicate<V> filter, Supplier<V> orElse){
        for (Entry<K, ConcurrentDataEntry<V>> kvEntry : entrySet()) {
            if(filter.test(kvEntry.getValue().value)){
                return get(kvEntry.getKey()).value;
            }
        }
        return orElse.get();
    }
    public void update(K k, ConcurrentDataEntry<V> entry){
        entry.value=Trackable.objectFromSQL(updateCommand.apply(k),constructor);
        entry.isDirty=false;
    }

    /**
     * Marks the trackable with the provided key as needing updates from the DB if it exists.
     */
    public void markDirty(K trackableKey){
        if(containsKey(trackableKey)){
            get(trackableKey).isDirty=true;
        }
    }

    /**
     * Marks all the trackables matching the provided filter as dirty.
     * @param filter
     */
    public void markDirty(Predicate<V> filter){
        for (Entry<K, ConcurrentDataEntry<V>> kvEntry : entrySet()) {
            if(filter.test(kvEntry.getValue().value)){
                kvEntry.getValue().isDirty=true;
            }
        }
    }
}
