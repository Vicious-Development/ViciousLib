package com.vicious.viciouslib.script.impl;

import com.vicious.viciouslib.script.condition.type.Condition;
import com.vicious.viciouslib.script.function.ScriptFunction;
import com.vicious.viciouslib.script.operations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Script {
    private final List<Operation> operations = new ArrayList<>();
    private final Map<String, ScriptFunction> functions = new HashMap<>();

    public ScriptFunction getFunction(String functionName) {
        return functions.get(functionName);
    }
    public void addFunction(String name, ScriptFunction function){
        this.functions.put(name, function);
    }

    public Script append(Operation action){
        operations.add(action);
        return this;
    }
    public Script set(int index, Operation action){
        while(operations.size() <= index){
            operations.add(new NoOp());
        }
        operations.set(index,action);
        return this;
    }

    public Script branch(Condition condition, List<Operation> trueBranch){
        int lengthBranch = trueBranch.size();
        append(new Compare(condition));
        int dest = operations.size()+lengthBranch+1;
        append(new JumpIf(dest));
        operations.addAll(trueBranch);
        return this;
    }
    private Script branchElse(List<Operation> falseBranch){
        operations.add(new JumpIf());
        int lengthBranch = falseBranch.size();
        int dest = operations.size()+lengthBranch+1;
        append(new JumpIf(dest));
        operations.addAll(falseBranch);
        return this;
    }
    public Script whileLoop(Condition condition, List<Operation> loop){
        int lengthBranch = loop.size();
        int dest = operations.size()+lengthBranch+3;
        int compareDest = operations.size();
        append(new Compare(condition));
        append(new JumpIf(dest));
        operations.addAll(loop);
        append(new Jump(compareDest));
        return this;
    }

    public Script branchIfElse(Condition condition, List<Operation> trueBranch, List<Operation> falseBranch){
        branch(condition,trueBranch);
        return branchElse(falseBranch);
    }
    public Script returnIf(Condition condition){
        List<Operation> returns = new ArrayList<>();
        returns.add(new Return());
        return branch(condition,returns);
    }

    public List<Operation> getOperations() {
        return operations;
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < operations.size(); j++) {
            stringBuilder.append(j).append(": ").append(operations.get(j)).append("\n");
        }
        return stringBuilder.toString();
    }
}
