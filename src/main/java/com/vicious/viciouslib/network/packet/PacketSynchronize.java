package com.vicious.viciouslib.network.packet;


import com.vicious.viciouslib.network.Side;
import com.vicious.viciouslib.network.annotation.Directionality;
import com.vicious.viciouslib.network.annotation.Permission;
import com.vicious.viciouslib.util.BiMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//Allow sending to server but do not handle.
@Directionality({Side.CLIENT,Side.SERVER})
public class PacketSynchronize implements IPacket {
    private final BiMap<Integer, String> classMap;

    public PacketSynchronize(){
        classMap = new BiMap<>();
    }

    public PacketSynchronize(BiMap<Integer, String> classMap) {
        this.classMap = classMap;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException{
        dos.writeInt(classMap.size());
        classMap.forEach((k,v)->{
            try {
                dos.writeInt(k);
                dos.writeUTF(v);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            classMap.put(dis.readInt(), dis.readUTF());
        }
    }

    public BiMap<Integer,String> getClassMap(){
        return classMap;
    }
}
