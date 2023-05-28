package com.vicious.viciouslib.permission;

import java.util.*;

public class PermissionTree implements Collection<String> {
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
            return new ArrayList<>();
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

    public List<String> getLeafPaths(){
        List<String> paths = new ArrayList<>();
        root.forEach((k,v)->{
            getLeafPaths(paths,v,k);
        });
        return paths;
    }
    private void getLeafPaths(List<String> paths, PermissionNode current, String path){
        if(current.size() > 0){
            current.forEach((k,v)->{
                getLeafPaths(paths, v,path+v.getName());
            });
        }
        else{
            paths.add(path);
        }
    }
    public List<PermissionNode> getLeaves(){
        List<PermissionNode> nodes = new ArrayList<>();
        getLeaves(nodes,root);
        return nodes;
    }
    private void getLeaves(List<PermissionNode> nodes, PermissionNode current){
        if(current.size() > 0){
            current.forEach((k,v)->{
                getLeaves(nodes, v);
            });
        }
        else{
            nodes.add(current);
        }
    }

    @Override
    public int size() {
        return getLeaves().size();
    }

    @Override
    public boolean isEmpty() {
        return root.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if(o instanceof String) {
            return hasPermission((String) o);
        }
        else{
            return false;
        }
    }

    @Override
    public Iterator<String> iterator() {
        return getLeafPaths().iterator();
    }

    @Override
    public Object[] toArray() {
        return getLeafPaths().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getLeafPaths().toArray(a);
    }

    @Override
    public boolean add(String s) {
        addPermission(s);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if(o instanceof String){
            removePermission((String) o);
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if(!contains(o)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        for (String s : c) {
            add(s);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean out = true;
        for (Object o : c) {
            if(!remove(o)){
                out = false;
            }
        }
        return out;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<String> strs = getLeafPaths();
        boolean out = getLeafPaths().retainAll(c);
        clear();
        addAll(getLeafPaths());
        return out;
    }
}
