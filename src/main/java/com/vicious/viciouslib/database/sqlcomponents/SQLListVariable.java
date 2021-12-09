package com.vicious.viciouslib.database.sqlcomponents;

import java.util.ArrayList;
import java.util.List;

public class SQLListVariable extends SQLVariable{
    private List<SQLVariable> vars = new ArrayList<>();
    public SQLListVariable(List<SQLVariable> vars){
        this.vars=vars;
        createStatement();
    }
    public SQLListVariable(Object... vars){
        for(int i = 0; i < vars.length; i++){
            this.vars.add(new SQLVariable(vars[i]));
        }
        createStatement();
    }
    public void add(Object o){
        vars.add(new SQLVariable(o));
        createStatement();
    }
    private void createStatement(){
        statement = "(";
        for(int i = 0; i < vars.size(); i++){
            statement+=vars.get(i);
            if(i < vars.size()-1){
                statement+= ",";
            }
        }
        statement+=")";
    }

    public List<SQLVariable> getVars() {
        return vars;
    }
    public SQLListVariable clone(){
        return new SQLListVariable(vars);
    }

    public int size() {
        return vars.size();
    }
}
