package com.vicious.viciouslib.database.sqlcomponents;

import java.util.ArrayList;
import java.util.List;

public class SQLListStatement extends SQLCommandPart {
    private List<String> vars = new ArrayList<>();
    public SQLListStatement(List<String> varNames){
        for(String s : varNames){
            vars.add(s);
        }
        createStatement();
    }
    public SQLListStatement(String... varNames){
        for(int i = 0; i < varNames.length; i++){
            vars.add(varNames[i]);
        }
        createStatement();
    }
    public void add(String s){
        vars.add(s);
        createStatement();
    }
    public List<String> getVars(){
        return vars;
    }
    private void createStatement(){
        for(int i = 0; i < vars.size(); i++){
            statement+=vars.get(i);
            if(i < vars.size()-1){
                statement+= ",";
            }
        }
    }
    public SQLListStatement clone(){
        return new SQLListStatement(vars);
    }
}
