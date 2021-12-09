package com.vicious.viciouslib.database.sqlcomponents;

public class SQLFormatters {
    private SQLFormatExecutor executor;
    public SQLFormatters(SQLFormatExecutor s){
        executor=s;
    }
    public String format(String s){
        return "" + executor.format(s);

    }
    public static final SQLFormatters PARENTHESATE = new SQLFormatters((s) -> "(" + s + ")");
    public static final SQLFormatters DEPARENTHESATE = new SQLFormatters((s) -> s.replaceAll("[()]", ""));
}
