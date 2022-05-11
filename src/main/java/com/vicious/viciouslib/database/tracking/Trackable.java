package com.vicious.viciouslib.database.tracking;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.sqlcomponents.*;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslib.staticinheritance.StaticField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Trackable<T extends Trackable<T>> {
    public static TrackingHandler handler;
    public Trackable(TrackableValue<?>... extraValues){
        for (TrackableValue<?> extraValue : extraValues) {
            values.put(extraValue.name,extraValue);
        }
    }
    protected SQLMappedVariable dirtyMap = new SQLMappedVariable();
    public final Map<String, TrackableValue<?>> values = new LinkedHashMap<>();

    public static SQLCommand getSQLSelectCommand(){
        return null;
    }
    public static SQLCommand getSQLSelectWhereCommand(SQLBooleanVariable bool){
        return null;
    }
    public static SQLCommand getSQLDeleteCommand(SQLBooleanVariable bool){return null;}
    public abstract SQLCommand getSQLUpdateCommand();
    public Object[] toSQLStrings(TrackableValue<?>[] vals){
        Object[] objs = new Object[values.size()];
        for (int i = 0; i < vals.length; i++) {
            objs[i] = vals[i].SQLString();
        }
        return objs;
    }
    public SQLCommand getSQLNewCommand() {
        try {
            return SQLCommands.INSERTINTOLOCATION(sqlVarList(), SQLCommandPart.of(StaticField.get(this.getClass(), String.class, "tablename")), new SQLListVariable(toSQLStrings(values.values().toArray(new TrackableValue[0]))));
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public Class<?>[] getColumnTypes(){
        Class<?>[] classes = new Class<?>[values.size()];
        TrackableValue<?>[] vals = values.values().toArray(new TrackableValue[0]);
        for (int i = 0; i < values.size(); i++) {
            classes[i]=vals[i].getSQLClassType();
        }
        return classes;
    }
    protected SQLListStatement sqlVarList(){
        String[] keys = values.keySet().toArray(new String[0]);
        return new SQLListStatement(keys);
    }
    protected <E extends TrackableValue<?>> E add(E t){
        values.put(t.name,t);
        return t;
    }
    public void markDirty(String variablename, Object var){
        dirtyMap.add(variablename,var);
        validateHandler();
        handler.queueUpdate(this);
    }
    public static void setHandler(TrackingHandler instance) {
        handler=instance;
    }
    protected void update() {
        try {
            handler.getDB().safeExecute(getSQLUpdateCommand(),this);
            dirtyMap.clear();
        } catch(SQLException e){
            LoggerWrapper.logError("Failed to update a tracked object: " + e);
        }
    }
    protected Integer getNewIntID(String tableName) {
        Integer transactionID = 1;
        try {
            SQLCommand command = SQLCommands.SELECTLASTKEY(SQLCommandPart.of(tableName),SQLCommandPart.of("id"));
            ResultSet resultSet = handler.getDB().safeExecuteQuery(command, this, 0);
            while (resultSet.next()) {
                transactionID = (resultSet.getInt(1) + 1);
            }
            resultSet.close();
            //resultSet.close();
        } catch (Exception ex) {
            LoggerWrapper.logError("Failed to get an id " + ex);
            ex.printStackTrace();
        }
        return transactionID;
    }
    public T updateValue(String name, Object value) throws Exception {
        try {
            values.get(name).setUnchecked(value);
        } catch(Exception e){
            throw new Exception("Value not found, value names are cAsE SEnSITive!");
        }
        return (T)this;
    }
    public Object getValue(String name){
        return values.get(name).value();
    }
    public static <T extends Trackable<T>> T objectFromSQL(SQLCommand sql, Supplier<T> supplier){
        ResultSet rs = null;
        try {
            rs = handler.getDB().safeExecuteQuery(sql, supplier.get(), 0);
        } catch(SQLException e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        if(rs == null) return null;
        try {
            while (rs.next()) {
                return (T)supplier.get().setValues(rs);
            }
            rs.close();
        }
        catch(SQLException e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static <T extends Trackable<T>> List<T> objectsFromSQL(SQLCommand sql, Supplier<T> supplier){
        ResultSet rs = null;
        try {
            rs = handler.getDB().safeExecuteQuery(sql, supplier.get(), 0);
        } catch(SQLException e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        List<T> lst = new ArrayList<>();
        if(rs == null) return lst;
        try {
            while (rs.next()) {
                lst.add((T)supplier.get().setValues(rs));
            }
            rs.close();
        }
        catch(SQLException e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        return lst;
    }
    public static <T extends Trackable<T>> List<T> objectsFromSQLIgnoreExceptions(SQLCommand sql, Supplier<T> supplier){
        ResultSet rs = null;
        try {
            rs = handler.getDB().safeExecuteQuery(sql, supplier.get(), 0);
        } catch(SQLException e){
        }
        List<T> lst = new ArrayList<>();
        if(rs == null) return lst;
        try {
            while (rs.next()) {
                lst.add((T)supplier.get().setValues(rs));
            }
            rs.close();
        }
        catch(SQLException e){
        }
        return lst;
    }

    protected Trackable<T> setValues(ResultSet rs) {
        for (String s : values.keySet()) {
            try {
                values.get(s).setFromSQL(rs);
            } catch(Exception e){
                LoggerWrapper.logError("Failed to set a trackablevalue. Caused by:" + e);
                e.printStackTrace();
            }
        }
        return this;
    }
    public void forEachValue(Predicate<TrackableValue<?>> predicate, Consumer<TrackableValue<?>> run){
        for (TrackableValue<?> value : values.values()) {
            try {
                if (predicate.test(value)) {
                    run.accept(value);
                }
            } catch(Exception ignored){}
        }
    }
    protected static SQLCommand getSQLOverwriteColumnValuesCommand(Trackable<?> template, TrackableValue<?> value) {
        try {
            return SQLCommands.UPDATEINLOCATION(new SQLMappedVariable().add(value.name, value.value()),SQLCommandPart.of(StaticField.get(template.getClass(), String.class, "tablename")));
        } catch (Exception e){
            LoggerWrapper.logError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    protected void validateHandler() {
        if(handler == null) handler = DefaultTrackingHandler.getInstance();
    }
}