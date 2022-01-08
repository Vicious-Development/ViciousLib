package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.Database;
import com.vicious.viciouslib.database.tracking.interfaces.TickableTrackableValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//Used to track variables that should be updated on the DB
public class TrackingHandler {
    //OnWorldTick
    private Stack<Trackable<?>> dirty = new Stack<>();
    private Stack<JSONTrackable<?>> dirtyJsons = new Stack<>();
    private List<TickableTrackableValue> tickables = new ArrayList<>();
    private Database db;
    public TrackingHandler(Database db){
        this.db=db;
    }
    public void update() throws SQLException{
        try {
            int count = 0;
            //TODO: Make count a CFG option
            while (count < 5) {
                count++;
                if (dirty.size() <= 0) break;
                Trackable<?> t = dirty.pop();
                db.safeExecute(t.getSQLUpdateCommand(), t);
            }
            count = 0;
            //TODO: Make count a CFG option
            while (count < 5) {
                count++;
                if (dirtyJsons.size() <= 0) break;
                dirtyJsons.peek().save();
                dirtyJsons.pop();
            }
            tick();
        } catch(Exception e){
            if(e instanceof SQLException) throw e;
            else {
                LoggerWrapper.logError(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void addTickable(TickableTrackableValue v){
        tickables.add(v);
    }
    public void tick(){
        for (TickableTrackableValue tickable : tickables) {
            tickable.tick();
        }
    }
    public void queueUpdate(Trackable t){
        dirty.add(t);
    }
    public Database getDB(){
        return db;
    }

    public void updateFinal() {
        try {
            while (dirty.size() > 0) {
                Trackable t = dirty.remove(0);
                db.safeExecute(t.getSQLUpdateCommand(), t);
            }
        } catch(SQLException e){
            LoggerWrapper.logError("Failed to apply final updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public <T extends JSONTrackable<T>> void queueJSONUpdate(JSONTrackable<T> jsonTrackable) {
        dirtyJsons.add(jsonTrackable);
    }
}
