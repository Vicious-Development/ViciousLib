package com.vicious.viciouslib.network.connections;

import com.vicious.viciouslib.network.PacketChannel;
import com.vicious.viciouslib.network.PacketLexicon;
import com.vicious.viciouslib.network.packet.IPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IConnection {

    boolean isClosed();
    DataInputStream dis();
    DataOutputStream dos();
    PacketLexicon getLexicon();

    void close();
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

    default void receivingThread() throws IOException {
        while(!isClosed()){
            if(dis().available() > 0){
                int id = dis().readInt();
                PacketChannel<?> channel = getLexicon().getChannel(id);
                if(channel != null) {
                    if (shouldProcess(channel)) {
                        receive(channel);
                    }
                }
            }
        }
    }

    default boolean hasPermission(String value){
        return false;
    }
}
