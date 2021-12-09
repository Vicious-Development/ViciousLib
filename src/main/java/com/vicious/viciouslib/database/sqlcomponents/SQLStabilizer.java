package com.vicious.viciouslib.database.sqlcomponents;

import com.vicious.viciouslib.database.objectTypes.LongText;

public class SQLStabilizer {
    //Prevents human mistakes
    public static Object stabilize(Object var){
        if(var instanceof Boolean || Boolean.TYPE.isInstance(var)){
            if((boolean)var) return 1;
            else return 0;
        }
        if(var instanceof String){
            if(((String) var).length() > 8000){
                return new LongText((String)var);
            }
        }
        return var;
    }
}