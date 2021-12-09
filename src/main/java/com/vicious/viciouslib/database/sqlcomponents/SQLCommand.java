package com.vicious.viciouslib.database.sqlcomponents;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

public class SQLCommand {
    List<SQLCommandPart> parts = new ArrayList<>();
    public static SQLCommand doNotValidate(SQLCommandPart... parts){
        try{
            return new SQLCommand(false, parts);
        } catch(SQLSyntaxErrorException e){
            return null;
        }
    }
    public int indexOf(SQLCommandPart part){
        return parts.indexOf(part);
    }
    public SQLCommand(SQLCommandPart... parts) throws SQLSyntaxErrorException{
        for(SQLCommandPart p : parts){
            this.parts.add(p);
        }
        validate();
    }
    public SQLCommand(boolean doValidate, SQLCommandPart... parts) throws SQLSyntaxErrorException{
        for(SQLCommandPart p : parts){
            this.parts.add(p);
        }
        if(doValidate) validate();
    }
    void validate() throws SQLSyntaxErrorException{
        for(int i = 0; i < parts.size(); i++){
            checkNextPart(i);
        }
    }
    private void checkNextPart(int pos1) throws SQLSyntaxErrorException{
        if(pos1 != parts.size()-1){
            if(parts.get(pos1) instanceof SQLVariable && parts.get(pos1+1) instanceof SQLVariable){
                throw new SQLSyntaxErrorException("Failed to parse SQLCommand! Variable without statement '" + parts.get(pos1+1).statement + "' is incorrect! at " + (pos1 + 1));
            }
        } else if(!(parts.get(pos1) instanceof SQLVariable) && parts.size() > 1){
            throw new SQLSyntaxErrorException("Failed to parse SQLCommand! Statement unclosed with variable '" + parts.get(pos1).statement + "' is incorrect! at " + pos1);
        }
    }
    public String toString(){
        String msg = "";
        for(int i = 0; i < parts.size();i++){
            SQLCommandPart p = parts.get(i);
            String ps = p.toString();
            msg += ps;
            if(i < parts.size()-1) {
                msg += " ";
            }
        }
        return msg;
    }

    public SQLCommand replaceAll(String key, String rep) {
        for(SQLCommandPart part : parts){
            part.replaceAll(key,rep);
        }
        return this;
    }

    public List<SQLCommandPart> getParts() {
        return parts;
    }

    public SQLCommandPart getPart(int i) {
        return parts.get(i);
    }

    public boolean contains(SQLCommandPart update) {
        return parts.contains(update);
    }
}
