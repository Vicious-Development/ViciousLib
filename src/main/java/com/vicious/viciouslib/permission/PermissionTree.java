package com.vicious.viciouslib.permission;

import java.util.LinkedList;
import java.util.List;

public class PermissionTree {
    private boolean hasAll = false;
    private final PermissionNode root = new PermissionNode("root");
    public boolean hasPermission(String permission){
        return hasPermission(toPathList(permission));
    }
    public boolean hasPermission(PermissionNode node){
        return hasPermission(node.getPath());
    }

    private boolean hasPermission(List<String> path) {
        return hasPermission(path,root.get(path.remove(0)));
    }

    public void clear(){
        root.clear();
    }

    public boolean hasPermission(List<String> path, PermissionNode node){
        if(hasAll) {
            return true;
        }
        if(node == null){
            return false;
        }
        node = node.get(path.remove(0));
        if(node == null){
            return false;
        }
        else if(!path.isEmpty()){
            return hasPermission(path,node);
        }
        else{
            return true;
        }
    }

    public void addPermission(String path){
        if(path.equals("*")){
            grantAll();
            return;
        }
        List<String> lst = toPathList(path);
        PermissionNode prev;
        PermissionNode node = root;
        while(!lst.isEmpty()){
            prev = node;
            String dest = lst.remove(0);
            node = node.get(dest);
            if(node == null){
                node = new PermissionNode(dest);
                node.setParent(prev);
            }
        }
    }
    public boolean removePermission(String path){
        if(path.equals("*")){
            revokeAll();
            return true;
        }
        List<String> lst = toPathList(path);
        PermissionNode node = root;
        while(!lst.isEmpty()){
            node = node.get(lst.remove(0));
            if(node == null) return false;
            if(lst.size() == 0){
                node.destroy();
            }
        }
        return true;
    }

    public List<String> toPathList(String permission){
        if(permission == null || permission.isEmpty()){
            return List.of();
        }
        List<String> permissions = new LinkedList<>();
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < permission.length(); i++) {
            char c = permission.charAt(i);
            if(i == permission.length()-1){
                val.append(c);
                permissions.add(val.toString());
            }
            else if(c == '.'){
                permissions.add(val.toString());
                val = new StringBuilder();
            }
            else{
                val.append(c);
            }
        }
        return permissions;
    }

    public void grantAll(){
        hasAll=true;
    }
    public boolean hasAll(){
        return hasAll;
    }

    public void revokeAll(){
        hasAll=false;
    }
}
