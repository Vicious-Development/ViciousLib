package com.vicious.viciouslib.database.sqlcomponents;

public class SQLStatements {
    public static final SQLCommandPart WHERE = new SQLCommandPart("WHERE");
    public static final SQLCommandPart FROM = new SQLCommandPart("FROM");
    public static final SQLCommandPart STAR = new SQLCommandPart("*");
    public static final SQLCommandPart SELECT = new SQLCommandPart("SELECT");
    public static final SQLCommandPart AND = new SQLCommandPart("AND");
    public static final SQLCommandPart OR = new SQLCommandPart("OR");
    public static final SQLCommandPart LIKE = new SQLCommandPart("LIKE");
    public static final SQLCommandPart SHOW = new SQLCommandPart("SHOW");
    public static final SQLCommandPart UPDATE = new SQLCommandPart("UPDATE");
    public static final SQLCommandPart PARENR = new SQLCommandPart("(");
    public static final SQLCommandPart PARENL = new SQLCommandPart(")");
    public static final SQLCommandPart COMMA = new SQLCommandPart(",");
    public static final SQLCommandPart SET = new SQLCommandPart("SET");
    public static final SQLCommandPart INTO = new SQLCommandPart("INTO");
    public static final SQLCommandPart INSERT = new SQLCommandPart("INSERT");
    public static final SQLCommandPart VALUES = new SQLCommandPart("VALUES");
    public static final SQLCommandPart CREATE = new SQLCommandPart("CREATE");
    public static final SQLCommandPart TABLE = new SQLCommandPart("TABLE");
    public static final SQLCommandPart TABLES = new SQLCommandPart("TABLES");
    public static final SQLCommandPart INT = new SQLCommandPart("INT");
    public static final SQLCommandPart NOT = new SQLCommandPart("NOT");
    public static final SQLCommandPart NULL = new SQLCommandPart("NULL");
    public static final SQLCommandPart NOTNULL = new SQLCommandPart(NOT,NULL);
    public static final SQLCommandPart VARCHAR = new SQLCommandPart("VARCHAR");
    public static final SQLCommandPart PRIMARY = new SQLCommandPart("PRIMARY");
    public static final SQLCommandPart KEY = new SQLCommandPart("KEY");
    public static final SQLCommandPart ADD = new SQLCommandPart("ADD");
    public static final SQLCommandPart COLUMN = new SQLCommandPart("COLUMN");
    public static final SQLCommandPart AFTER = new SQLCommandPart("AFTER");
    public static final SQLCommandPart ALTER = new SQLCommandPart("ALTER");
    public static final SQLCommandPart BEFORE = new SQLCommandPart("BEFORE");
    public static final SQLCommandPart LIMIT = new SQLCommandPart("LIMIT");
    public static final SQLCommandPart ORDER = new SQLCommandPart("ORDER");
    public static final SQLCommandPart BY = new SQLCommandPart("BY");
    public static final SQLCommandPart INTNOTNULL = new SQLCommandPart(INT,NOT,NULL);
    public static final SQLCommandPart INTNULL = new SQLCommandPart(INT,NULL);
    public static final SQLCommandPart ADDCOLUMN = new SQLCommandPart(ADD,COLUMN);
    public static final SQLCommandPart ORDERBY = new SQLCommandPart(ORDER,BY);
    public static final SQLCommandPart DELETE = new SQLCommandPart("DELETE");

    public static final SQLCommandPart OFSIZE(SQLCommandPart part, int size){
        return part.concat(new SQLCommandPart("("+size+")"));
    }
    public static final <T extends SQLCommandPart> SQLCommandPart PARENTHESATE(T... parts){
        return SQLCommandPart.of("(" + new SQLCommandPart(parts) + ")");
    }
    public static final SQLBooleanVariable TABLEBOOLEAN(String tableName){
        return new SQLBooleanVariable(new SQLCommandPart("TABLE_NAME"),new SQLVariable(tableName));
    }
}

