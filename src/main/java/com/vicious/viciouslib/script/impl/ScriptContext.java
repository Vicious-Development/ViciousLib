package com.vicious.viciouslib.script.impl;

import com.vicious.viciouslib.script.Constants;
import com.vicious.viciouslib.script.condition.type.Condition;
import com.vicious.viciouslib.script.function.ScriptFunction;
import com.vicious.viciouslib.script.operations.*;

import java.util.*;

public class ScriptContext {
    private final Map<String, ScriptVariable> variables = new HashMap<>();
    private final Stack<Object> stack = new Stack<>();
    private boolean isRunning = true;
    private int i = 0;
    private Script script;

    public void setScript(Script script){
        this.script=script;
        compile();
    }

    public Object pop(){
        return stack.pop();
    }

    public void push(Object object){
        stack.push(object);
    }

    public ScriptVariable getVariable(String name){
        return variables.computeIfAbsent(name,n->new ScriptVariable());
    }

    public ScriptFunction getFunction(String functionName) {
        return script.getFunction(functionName);
    }

    public void execute(){
        isRunning=true;
        List<Operation> operations = script.getOperations();
        for (; i < operations.size() && isRunning; i++) {
            operations.get(i).accept(this);
        }
    }

    public Object executeReturns(){
        isRunning=true;
        List<Operation> operations = script.getOperations();
        for (; i < operations.size() && isRunning; i++) {
            operations.get(i).accept(this);
        }
        if(stack.isEmpty()){
            return Constants.VOID;
        }
        else{
            return stack.pop();
        }
    }

    public void exit() {
        isRunning=false;
    }

    public void jump(int index){
        this.i=index-1;
    }

    public int stackSize() {
        return stack.size();
    }

    @Override
    public String toString() {
        return script.toString();
    }

    public void compile() {
        for (Operation operation : script.getOperations()) {
            operation.setup(this);
        }
    }


    public int getIndex() {
        return i;
    }
}
