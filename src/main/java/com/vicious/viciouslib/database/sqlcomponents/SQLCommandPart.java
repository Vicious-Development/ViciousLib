package com.vicious.viciouslib.database.sqlcomponents;

import com.vicious.viciouslib.database.objectTypes.LongText;
import com.vicious.viciouslib.database.objectTypes.MediumText;
import com.vicious.viciouslib.database.objectTypes.SQLVector3i;

import java.util.*;

public class SQLCommandPart {
    protected static Map<Class<?>, String> classToSQLType = new HashMap<>();
    protected ArrayList<SQLFormatters> formatters = new ArrayList<>();
    static {
        classToSQLType.put(String.class, "VARCHAR(45)");
        classToSQLType.put(LongText.class, "LONGTEXT");
        classToSQLType.put(MediumText.class, "MEDIUMTEXT");
        classToSQLType.put(Integer.TYPE, "INT");
        classToSQLType.put(Integer.class, "INT");
        classToSQLType.put(Date.class, "VARCHAR(45)");
        classToSQLType.put(Double.class, "DOUBLE");
        classToSQLType.put(Double.TYPE, "DOUBLE");
        classToSQLType.put(Boolean.TYPE, "BOOLEAN");
        classToSQLType.put(Boolean.class, "BOOLEAN");
        classToSQLType.put(Long.class, "LONG");
        classToSQLType.put(Long.TYPE, "LONG");
        classToSQLType.put(Short.class, "SHORT");
        classToSQLType.put(Short.TYPE, "SHORT");
        classToSQLType.put(Byte.class, "BYTE");
        classToSQLType.put(UUID.class, "VARCHAR(45)");
        classToSQLType.put(SQLVector3i.class, "MEDIUMTEXT");
    }
    protected String statement = "";

    public SQLCommandPart() {

    }

    public SQLCommandPart(SQLCommandPart... parts) {
        statement = "";
        for (SQLCommandPart p : parts) {
            statement += p.toString() + " ";
        }
    }

    public SQLCommandPart concat(SQLCommandPart part) {

        return new SQLCommandPart(new SQLCommandPart(statement), part);
    }

    public SQLCommandPart(String statement) {
        this.statement = statement;
    }

    public static SQLCommandPart of(String s) {
        return new SQLCommandPart(s);
    }

    public String toString() {
        return format(statement);
    }
    protected String format(String s){
        String newS = ""+s;
        for (SQLFormatters formatter : formatters) {
            newS = "" + formatter.format(newS);
        }
        return newS;
    }

    public SQLCommandPart replaceAll(String key, String rep) {
        statement = statement.replaceAll(key, rep);
        return this;
    }

    public boolean equals(Object o) {
        if (o instanceof SQLCommandPart) {
            return ((SQLCommandPart) o).statement.equals(this.statement);
        } else return false;
    }
    public SQLCommandPart parenthesate(){
        SQLCommandPart clone = this.clone();
        clone.formatters.addAll(formatters);
        clone.formatters.add(SQLFormatters.PARENTHESATE);
        return clone;
    }

    public SQLCommandPart deparenthesate() {
        SQLCommandPart clone = this.clone();
        clone.formatters.addAll(formatters);
        clone.formatters.add(SQLFormatters.DEPARENTHESATE);
        return clone;
    }
    public SQLCommandPart clone(){
        return new SQLCommandPart(statement);
    }
}
