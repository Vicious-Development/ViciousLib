package com.vicious.viciouslib.database.sqlcomponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLMappedVariable extends SQLVariable {
    private Map<String,Object> vars = new HashMap<>();
    public SQLMappedVariable(SQLListStatement varNames, SQLListVariable vars) {
        for (int i = 0; i < varNames.getVars().size(); i++) {
            this.vars.put(varNames.getVars().get(i), vars.getVars().get(i).var);
        }
    }
    public SQLMappedVariable(List<String> varNames){
        for(String s : varNames){
            vars.put(s, null);
        }
    }
    public SQLMappedVariable(String... varNames){
        for(int i = 0; i < varNames.length; i++){
            vars.put(varNames[i], null);
        }
    }
    public SQLMappedVariable(Map<String,Object> vars){
        this.vars=vars;
    }
    public String getKeyString(){
        statement = "(";
        for(int i = 0; i < vars.size(); i++){
            statement+=vars.get(i);
            if(i < vars.size()-1){
                statement+=",";
            }
        }
        statement += ")";
        return statement;
    };

    public SQLMappedVariable add(String var, Object val) {
        val = SQLStabilizer.stabilize(val);
        if(vars.putIfAbsent(var, val) != null){
            vars.replace(var, val);
        }
        return this;
    }
    public SQLCommandPart toSetPart(){
        String sql = "";
        String[] keys = vars.keySet().toArray(new String[vars.size()]);
        Object[] values = vars.values().toArray();
        for(int i = 0; i < keys.length; i++){
            sql += keys[i] + "='" + values[i] + "'";
            if(i < keys.length-1){
                sql += ",";
            }
        }
        return new SQLCommandPart(sql);
    }
    public void clear(){
        vars.clear();
    }
    public SQLMappedVariable clone(){
        return new SQLMappedVariable(vars);
    }
}
