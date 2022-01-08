package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.sqlcomponents.SQLCommand;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslib.util.VLUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;


//JSON Trackables have support for updating JSON objects.
public class JSONTrackable<T extends JSONTrackable<T>> extends Trackable<T>{
    private List<Consumer<JSONTrackable<T>>> initalizationListeners = new ArrayList<>();
    private List<Consumer<JSONTrackable<T>>> readListeners = new ArrayList<>();
    private List<Consumer<JSONTrackable<T>>> writeListeners = new ArrayList<>();
    private boolean initialized = false;
    public void executeIfInitialized(Consumer<JSONTrackable<T>> consumer){
        initalizationListeners.add(consumer);
        if(initialized) onInitialization();
    }
    public void executeOnRead(Consumer<JSONTrackable<T>> consumer){
        readListeners.add(consumer);
    }
    public void executeOnWrite(Consumer<JSONTrackable<T>> consumer){
        writeListeners.add(consumer);
    }
    private ScheduledFuture<?> readWriteTask;
    protected JSONObject jsonObj = new JSONObject();
    public JSONTrackable(String path ,TrackableValue<?>... extraValues){
        this(FileUtil.toPath(path), extraValues);
    }
    public JSONTrackable(Path path, TrackableValue<?>... extraValues){
        super(extraValues);
        PATH=path;
        if(!Files.exists(path)) {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::save, this);
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
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::save, this);
        }
        else {
            readWriteTask = VLUtil.executeWhen((t) -> VLUtil.mightBeInitialized(TrackableValue.class, t), JSONTrackable::readFromJSON, this);
        }
    }
    public final Path PATH;
    public void onInitialization(){
        if(readWriteTask != null && !readWriteTask.isCancelled()) readWriteTask.cancel(false);
        for (Consumer<JSONTrackable<T>> consumer : initalizationListeners) {
            consumer.accept(this);
        }
        initalizationListeners.clear();
        initialized=true;
    }
    public void save(){
        if(readWriteTask != null) onInitialization();
        overWriteFile();
        for (Consumer<JSONTrackable<T>> writeListener : writeListeners) {
            writeListener.accept(this);
        }
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
            LoggerWrapper.logError("Failed to save a JSONTrackable " + getClass().getCanonicalName() + " caused by: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public JSONTrackable<T> readFromJSON(){
        if(readWriteTask != null) onInitialization();
        try {
            JSONObject obj = FileUtil.loadJSON(PATH);
            for (TrackableValue<?> value : values.values()) {
                value.setFromJSON(obj);
            }
        } catch(Exception e){
            //IOE happens if the file doesn't exist. If it doesn't no values will be updated anyways which is totally fine.
            if(!(e instanceof IOException)) {
                LoggerWrapper.logError("Failed to read a jsontrackable " + getClass().getCanonicalName() + " caused by: " + e.getMessage());
                e.printStackTrace();
            }
        }
        for (Consumer<JSONTrackable<T>> readListener : readListeners) {
            readListener.accept(this);
        }
        if(readWriteTask != null) readWriteTask = null;
        return this;
    }
}
