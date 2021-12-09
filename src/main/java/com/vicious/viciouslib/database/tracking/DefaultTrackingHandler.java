package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.database.Database;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultTrackingHandler extends TrackingHandler {
    public static DefaultTrackingHandler instance;
    public DefaultTrackingHandler(Database db) {
        super(db);
        instance = this;
    }
    public static void init(Database db){
        new DefaultTrackingHandler(db);
        Trackable.setHandler(instance);
        TrackableValue.globalHandler = instance;
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            try{
                instance.update();
            }
            catch(SQLException e) {
                System.err.println("Failed to update trackable: " + e.getMessage());
                e.printStackTrace();
            }
            //TODO CONFIGURABLE
        },0,50, TimeUnit.MILLISECONDS);
    }
}
