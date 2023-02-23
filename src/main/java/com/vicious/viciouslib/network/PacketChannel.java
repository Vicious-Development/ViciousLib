package com.vicious.viciouslib.network;

import com.vicious.viciouslib.network.annotation.Directionality;
import com.vicious.viciouslib.network.annotation.Permission;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketChannel<T extends IPacket> {
    private final Class<T> packetClass;
    private final Supplier<T> constructor;
    private final BiConsumer<T,IConnection> processor;
    private int identifier = 0;

    public PacketChannel(Class<T> packetClass, Supplier<T> constructor, BiConsumer<T,IConnection> processor, int id){
        this.packetClass=packetClass;
        this.constructor = constructor;
        this.identifier=id;
        this.processor=processor;
    }

    public void setIdentifier(int id){
        this.identifier=id;
    }

    public T createBase(){
        return constructor.get();
    }

    public Class<T> getPacketClass() {
        return packetClass;
    }

    public String getName(IConnection connection) {
        Permission p = packetClass.getAnnotation(Permission.class);
        if(p != null){
            if(!connection.hasPermission(p.value())){
                return "";
            }
        }
        return packetClass.getName();
    }

    public int getId() {
        return identifier;
    }

    public boolean sendSide(Side side) {
        Directionality directionality = packetClass.getAnnotation(Directionality.class);
        if(directionality == null){
            return true;
        }
        else{
            for (Side dir : directionality.value()) {
                if(dir == side){
                    return true;
                }
            }
        }
        return false;
    }

    public void process(T packet, IConnection connection) {
        processor.accept(packet, connection);
    }

    public boolean hasPermission(IConnection iConnection) {
        Permission p = packetClass.getAnnotation(Permission.class);
        if(p != null){
           return iConnection.hasPermission(p.value());
        }
        else{
            return true;
        }
    }
}
