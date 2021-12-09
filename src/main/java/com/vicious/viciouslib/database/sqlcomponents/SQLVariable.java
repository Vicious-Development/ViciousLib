package com.vicious.viciouslib.database.sqlcomponents;

import com.vicious.viciouslib.database.tracking.values.TrackableObject;

public class SQLVariable extends SQLCommandPart {
    protected Object var;
    public SQLVariable(SQLCommandPart... parts){
        super(parts);
    }
    public SQLVariable(Object var){
        this.var = SQLStabilizer.stabilize(var);
        createStatement(this.var);
    }
    public SQLVariable(TrackableObject<?> var){
        this.var = SQLStabilizer.stabilize(var.value());
        createStatement(this.var);
    }
    public void createStatement(Object var){
        this.statement =  "'"+var+"'";
    }
    public SQLVariable clone(){
        return new SQLVariable(var);
    }

    public Class<?> getVarClass() {
        return var.getClass();
    }
}

