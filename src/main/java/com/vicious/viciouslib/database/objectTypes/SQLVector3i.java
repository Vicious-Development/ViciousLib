package com.vicious.viciouslib.database.objectTypes;

import com.vicious.viciouslib.util.Hashable;

public class SQLVector3i implements Hashable {
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
            if(c != ','){
                s+=c;
            }
            else if (i < in.length()-1){
                s+=c;
                vals[j]=Integer.parseInt(s);
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

    @Override
    public int hashCode() {
        return hash();
    }
}
