package com.vicious.viciouslib.script.operations;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.script.impl.ScriptContext;

public class Jump implements Operation{
    @Save
    public int destination;

    public Jump(){}

    public Jump(int destination){
        this.destination=destination;
    }

    @Override
    public void accept(ScriptContext context) {
        context.jump(destination);
    }

    @Override
    public String toString() {
        return "Jump [" + destination + "]";
    }
}
