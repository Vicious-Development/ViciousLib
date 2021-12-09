package com.vicious.viciouslib.database.sqlcomponents;

import java.util.ArrayList;
import java.util.List;

public class SQLBooleanVariable extends SQLVariable{
    //Creates a boolean variable where row =
    public SQLBooleanVariable(SQLCommandPart row, SQLVariable expected){
        statement = "(" + row.statement + " = " + expected.statement + ")";
    }
    public SQLBooleanVariable(SQLCommandPart... parts){
        super(parts);
    }
    public static SQLBooleanVariable contains(SQLCommandPart row, SQLVariable target){
        SQLBooleanVariable var = new SQLBooleanVariable();
        var.statement = "(" + row.statement + "LIKE" + target.statement.replaceAll("'", "%");
        return var;
    }
    public static SQLBooleanVariable or(SQLBooleanVariable... bools){
        List<SQLCommandPart> res = interlace(SQLStatements.OR, bools);
        return new SQLBooleanVariable(res.toArray(new SQLCommandPart[res.size()]));
    }
    public static SQLBooleanVariable and(SQLBooleanVariable... bools){
        List<SQLCommandPart> res = interlace(SQLStatements.AND, bools);
        return new SQLBooleanVariable(res.toArray(new SQLCommandPart[res.size()]));
    }

    //TODO Move to util.
    public static <T> List<T> interlace(T interlacable, T... objects){
        List<T> res = new ArrayList<>();
        for(int i = 0; i < objects.length; i++){
            res.add(objects[i]);
            if(i < objects.length-1){
                res.add(interlacable);
            }
        }
        return res;
    }
    public static SQLBooleanVariable not(SQLCommandPart row, SQLVariable expected) {
        return new SQLBooleanVariable(new SQLCommandPart("(" + row.statement + " <> " + expected.statement + ")"));
    }
}
