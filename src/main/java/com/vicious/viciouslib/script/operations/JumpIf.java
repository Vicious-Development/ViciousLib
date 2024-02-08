package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.impl.ScriptContext;

public class JumpIf implements Operation{
    @Save
    public int destination;

    public JumpIf(){}

    public JumpIf(int destination){
        this.destination=destination;
    }

    @Override
    public void accept(ScriptContext context) {
        Object conditionResult = context.pop();
        if(conditionResult instanceof Boolean){
            if((Boolean)conditionResult){
                return;
            }
        }
        context.jump(destination);
    }

    @Override
    public String toString() {
        return "Jump [" + destination + "]";
    }
}
