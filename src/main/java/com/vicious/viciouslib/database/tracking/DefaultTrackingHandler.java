package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.Database;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultTrackingHandler extends TrackingHandler {
    private static DefaultTrackingHandler instance;
    public static DefaultTrackingHandler getInstance(){
        if(instance == null) instance = new DefaultTrackingHandler(null);
        return instance;
    }
    public DefaultTrackingHandler(Database db) {
        super(db);
    }
    public static void initWithDatabase(Database db){
        instance = new DefaultTrackingHandler(db);
        Trackable.setHandler(instance);
        TrackableValue.globalHandler = instance;
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            try{
                instance.update();
            }
            catch(SQLException e) {
                LoggerWrapper.logError("Failed to update trackable: " + e.getMessage());
                e.printStackTrace();
            }
            //TODO CONFIGURABLE
        },0,50, TimeUnit.MILLISECONDS);
    }
}
