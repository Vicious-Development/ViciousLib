package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.database.sqlcomponents.SQLCommand;

/**
 * A trackable object not intended for storing data. Acts as a memory storage.
 */
public class UnpersistentTrackable<T extends UnpersistentTrackable<T>> extends Trackable<T>{
    @Override
    protected void update() {
        //No updating.
    }

    @Override
    public void markDirty(String variablename, Object var) {
        //No dirtiness.
    }

    @Override
    public SQLCommand getSQLUpdateCommand() {
        return null;
    }
}
