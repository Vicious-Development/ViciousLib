package com.vicious.viciouslib.network.connections;

import com.vicious.viciouslib.network.PacketChannel;
import com.vicious.viciouslib.network.PacketLexicon;
import com.vicious.viciouslib.network.packet.IPacket;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public interface IConnection {
    boolean isClosed();
    DataInputStream dis();
    DataOutputStream dos();
    InputStream stream();
    PacketLexicon getLexicon();

    void close();

    default int readID() throws IOException {
        int b0 = stream().read();
        int b1 = stream().read();
        int b2 = stream().read();
        int b3 = stream().read();
        if((b0 | b1 | b2 | b3) < 0){
            throw new EOFException();
        }
        return ((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
    }
    default <T extends IPacket> void receive(PacketChannel<T> channel) throws IOException{
        T packet = channel.createBase();
        packet.read(dis());
        channel.process(packet,this);
    }

    default void send(IPacket packet) throws IOException {
        dos().writeInt(getLexicon().identifierOf(packet));
        packet.write(dos());
        dos().flush();
    }

    default boolean shouldProcess(PacketChannel<?> channel){
        if(channel.hasPermission(this)) {
            return channel.sendSide(getLexicon().getSide());
        }
        else{
            return false;
        }
    }

    default void receivingThread() {
        while(!isClosed()){
            try {
                int id = readID();
                PacketChannel<?> channel = getLexicon().getChannel(id);
                if (channel != null) {
                    if (shouldProcess(channel)) {
                        receive(channel);
                    }
                }
            } catch (Exception e){
                break;
            }
        }
    }

    default boolean hasPermission(String value){
        return false;
    }
}
