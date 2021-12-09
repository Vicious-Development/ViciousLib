package com.vicious.viciouslib.database.sqlcomponents;

public class SQLColumn extends SQLCommandPart {
    private SQLVariable name;
    private SQLCommandPart type;
    public SQLColumn(SQLVariable namedVar, SQLCommandPart type){
        this.name=namedVar;
        this.type=type;
        statement = namedVar.statement +" "+ type.statement;
    }
    public String toString(){
        return format(statement);
    }
    public SQLVariable getName(){
        return new SQLVariable(name.statement.replaceAll("'",""));
    }
    public String getRawName(){
        return name.statement.replaceAll("'","");
    }
    public void primarify(){
        type.replaceAll("NULL", "NOT NULL");
        statement = name.statement +" "+ type.statement;
    }
    public SQLCommandPart getType(){
        return type;
    }
    public SQLColumn clone(){
        return new SQLColumn(name,type);
    }
}
