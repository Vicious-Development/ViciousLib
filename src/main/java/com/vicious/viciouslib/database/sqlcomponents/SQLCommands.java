package com.vicious.viciouslib.database.sqlcomponents;

public class SQLCommands {
    public static final SQLCommand SHOWTABLESLIKE(SQLVariable table){
        return SQLCommand.doNotValidate(SQLStatements.SHOW,SQLStatements.TABLES,SQLStatements.LIKE, table);
    }
    public static final SQLCommand SELECTVARSFROMLOCATION(SQLListStatement varList, SQLCommandPart location) {
        return SQLCommand.doNotValidate(SQLStatements.SELECT, varList.deparenthesate(), SQLStatements.FROM, location);
    }

    public static final SQLCommand SELECTVARSFROMLOCATIONWHERE(SQLListStatement varList, SQLCommandPart location, SQLBooleanVariable bool){
        return SQLCommand.doNotValidate(SQLStatements.SELECT, varList.deparenthesate(), SQLStatements.FROM,location, SQLStatements.WHERE,bool);
    }

    public static final SQLCommand UPDATEINLOCATIONWHERE(SQLMappedVariable updated, SQLCommandPart location, SQLBooleanVariable bool){
        return SQLCommand.doNotValidate(SQLStatements.UPDATE, location, SQLStatements.SET, updated.toSetPart(), SQLStatements.WHERE, bool);
    }
    public static final SQLCommand UPDATEINLOCATION(SQLMappedVariable updated, SQLCommandPart location){
        return SQLCommand.doNotValidate(SQLStatements.UPDATE, location, SQLStatements.SET, updated.toSetPart());
    }

    public static final SQLCommand INSERTINTOLOCATION(SQLListStatement inserted, SQLCommandPart location, SQLListVariable values){
        return SQLCommand.doNotValidate(SQLStatements.INSERT, SQLStatements.INTO, location, inserted.parenthesate(), SQLStatements.VALUES, values);
    }

    public static final SQLCommand CREATETABLE(SQLVariable name, SQLColumnList cols, SQLVariable primarykey){
        return SQLCommand.doNotValidate(SQLStatements.CREATE, SQLStatements.TABLE, name, SQLStatements.PARENTHESATE(cols, SQLStatements.PRIMARY, SQLStatements.KEY, primarykey.parenthesate())).replaceAll("'","`");
    }

    public static final SQLCommand GETCOLUMNSINTABLE(SQLBooleanVariable tableBoolean){
        return SQLCommand.doNotValidate(SQLStatements.SELECT, SQLStatements.STAR, SQLStatements.FROM, SQLCommandPart.of("INFORMATION_SCHEMA.COLUMNS"), SQLStatements.WHERE, tableBoolean);
    }

    public static final SQLCommand GETCOLUMNSINTABLE(String tableName){
        return GETCOLUMNSINTABLE(SQLStatements.TABLEBOOLEAN(tableName));
    }

    public static final SQLCommand SELECTLASTKEY(SQLCommandPart tableName, SQLCommandPart key){
        return SQLCommand.doNotValidate(SQLStatements.SELECT,key,SQLStatements.FROM,tableName,SQLStatements.ORDERBY,key);
    }

    public static final SQLCommand GETTABLEOFNAME(SQLBooleanVariable tableBoolean){
        return SQLCommand.doNotValidate(SQLStatements.SELECT, SQLStatements.STAR, SQLStatements.FROM, SQLCommandPart.of("INFORMATION_SCHEMA.TABLES"), SQLStatements.WHERE, tableBoolean);
    }

    public static final SQLCommand GETTABLEOFNAME(String tableName){
        return GETTABLEOFNAME(SQLStatements.TABLEBOOLEAN(tableName));
    }

    public static final SQLCommand ADDCOLUMNSAFTER(SQLVariable tablename, SQLColumnList cols, SQLVariable preColumn){
        SQLCommandPart additionPart = new SQLCommandPart();
        for(int i = 0; i < cols.getCols().size(); i++){
            SQLColumn column = cols.getCols().get(i);
            additionPart = additionPart.concat(SQLStatements.ADDCOLUMN).concat(column).concat(SQLStatements.AFTER).concat(preColumn);
            preColumn = column.getName();
            if(i < cols.getCols().size()-1){
                additionPart = additionPart.concat(SQLStatements.COMMA);
            }
        }

        return SQLCommand.doNotValidate(SQLStatements.ALTER,SQLStatements.TABLE, tablename, additionPart).replaceAll("'","`");
    }
    public static final SQLCommand DELETE(String tableName, SQLBooleanVariable bool){
        return SQLCommand.doNotValidate(SQLStatements.DELETE, SQLStatements.FROM, new SQLCommandPart(tableName), SQLStatements.WHERE, bool);
    }
}
