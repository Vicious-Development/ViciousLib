package com.vicious.viciouslib.database.objectTypes;

import java.util.Objects;

public class SQLVector3i {
    public int x;
    public int y;
    public int z;
    public SQLVector3i(int x, int y, int z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public static SQLVector3i parseVector3i(String in){
        int[] vals = new int[3];
        String s = "";
        int j = 0;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (i == in.length()-1){
                s+=c;
                vals[j]=Integer.parseInt(s);
            }
            else if(c != ','){
                s+=c;
            }
            else{
                vals[j]=Integer.parseInt(s);
                s="";
                j++;
            }
        }
        return new SQLVector3i(vals[0],vals[1],vals[2]);
    }
    public String toString(){
        return x + "," + y + "," + z;
    }
    public SQLVector3i add(int x, int y, int z){
        return new SQLVector3i(this.x+x,this.y+y,this.z+z);
    }
    public SQLVector3i subtract(int x, int y, int z){
        return new SQLVector3i(this.x-x,this.y-y,this.z-z);
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLVector3i that = (SQLVector3i) o;
        return x == that.x && y == that.y && z == that.z;
    }
}
