package com.vicious.viciouslib.network.packet;

import com.vicious.viciouslib.network.IPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketMessage implements IPacket {
    private String message;

    public PacketMessage(){}
    public PacketMessage(String message){
        this.message = message;
    }
    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(message);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        message = dis.readUTF();
    }

    public String getMessage() {
        return message;
    }

}
