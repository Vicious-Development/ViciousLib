package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.database.sqlcomponents.SQLCommand;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslib.util.VLUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ScheduledFuture;


//JSON Trackables have support for updating JSON objects.
public class JSONTrackable<T extends JSONTrackable<T>> extends Trackable<T>{
    private ScheduledFuture<?> readWriteTask;
    protected JSONObject jsonObj = new JSONObject();
    public JSONTrackable(String path ,TrackableValue<?>... extraValues){
        this(FileUtil.toPath(path), extraValues);
    }
    public JSONTrackable(Path path, TrackableValue<?>... extraValues){
        super(extraValues);
        PATH=path;
        if(!Files.exists(path)) {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::overWriteFile, this);
        }
        else {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::readFromJSON, this);
        }
    }
    public JSONTrackable(String path){
        this(FileUtil.toPath(path));
    }
    public JSONTrackable(Path path) {
        PATH=path;
        if(!Files.exists(path)) {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::overWriteFile, this);
        }
        else {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::readFromJSON, this);
        }
    }
    public final Path PATH;
    public void save(){
        if(readWriteTask != null) readWriteTask.cancel(false);
        overWriteFile();
        if(readWriteTask != null) readWriteTask = null;
    }
    @Override
    public void markDirty(String variablename, Object var) {
        jsonObj.put(variablename,var);
        validateHandler();
        handler.queueJSONUpdate(this);
    }

    public SQLCommand getSQLUpdateCommand() {
        return null;
    }

    public void overWriteFile() {
        for (TrackableValue<?> value : values.values()) {
            jsonObj.put(value.name,value.value());
        }
        FileUtil.createOrWipe(PATH);
        try {
            Files.write(PATH, jsonObj.toString(1).getBytes(), StandardOpenOption.WRITE);
        } catch(IOException e){
            System.err.println("Failed to save a JSONTrackable " + getClass().getCanonicalName() + " caused by: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public JSONTrackable<T> readFromJSON(){
        if(readWriteTask != null) readWriteTask.cancel(false);
        try {
            JSONObject obj = FileUtil.loadJSON(PATH);
            for (TrackableValue<?> value : values.values()) {
                value.setFromJSON(obj);
            }
        } catch(Exception e){
            //IOE happens if the file doesn't exist. If it doesn't no values will be updated anyways which is totally fine.
            if(!(e instanceof IOException)) {
                System.err.println("Failed to read a jsontrackable " + getClass().getCanonicalName() + " caused by: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if(readWriteTask != null) readWriteTask = null;
        return this;
    }
}
