package com.vicious.viciouslib.database.sqlcomponents;

import java.util.ArrayList;
import java.util.List;

public class SQLColumnList extends SQLCommandPart{
    //TODO: Simplify SQL list type elements to use an interface.
    private List<SQLColumn> cols = new ArrayList<>();
    public SQLColumnList(SQLColumn... columns){
        for(int i = 0; i < columns.length; i++){
            cols.add(columns[i]);
        }
        createStatement();
    }
    public SQLColumnList(String[] keys, Class<?>[] values){
        for(int i = 0; i < keys.length; i++){
            cols.add(new SQLColumn(new SQLVariable(keys[i]), new SQLCommandPart(new SQLCommandPart(values[i].isEnum() ? "MEDIUMTEXT" : classToSQLType.get(values[i])), SQLStatements.NULL)));
        }
    }
    public void createStatement() {
        statement = "";
        for (int i = 0; i < cols.size(); i++) {
            statement += cols.get(i);
            statement += ",";
        }
    }
    public void add(SQLColumn column){
        cols.add(column);
        createStatement();
    }
    public List<SQLColumn> getCols(){
        return cols;
    }

    public SQLColumnList primarify(){
        (cols.get(0)).primarify();
        createStatement();
        return this;
    }
    public void remove(SQLColumn s) {
        cols.remove(s);
        createStatement();
    }
    public SQLColumnList clone(){
        return new SQLColumnList(cols.toArray(new SQLColumn[0]));
    }
}
