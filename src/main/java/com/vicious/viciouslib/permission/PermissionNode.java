package com.vicious.viciouslib.permission;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PermissionNode extends HashMap<String,PermissionNode>{
    private PermissionNode parent;
    private final String name;
    public PermissionNode(String name) {
        this.name = name;
    }

    public void addChild(PermissionNode node){
        put(node.getName(),node);
    }

    public String getName() {
        return name;
    }

    public PermissionNode getParent() {
        return parent;
    }

    public void setParent(PermissionNode parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void destroy(){
        parent.remove(this.getName());
    }

    public List<String> getPath() {
        List<String> parentPath = parent != null ? parent.getPath() : new LinkedList<>();
        parentPath.add(getName());
        return parentPath;
    }
}
