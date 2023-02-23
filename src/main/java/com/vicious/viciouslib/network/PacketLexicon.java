package com.vicious.viciouslib.network;

import com.vicious.viciouslib.network.packet.PacketSynchronize;
import com.vicious.viciouslib.util.BiMap;
import com.vicious.viciouslib.util.ClassMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketLexicon {
    private final ClassMap<PacketChannel<?>> channels = new ClassMap<>();
    private final Map<Integer,PacketChannel<?>> idChannels = new HashMap<>();
    private Side side = Side.SERVER;

    //Ensure that PacketSynchronize always registers first.
    public PacketLexicon(BiConsumer<PacketSynchronize,IConnection> synchronizationProcessor){
        registerHandler(PacketSynchronize.class, PacketSynchronize::new,synchronizationProcessor);
    }



    public <T extends IPacket> void registerHandler(Class<T> packet, Supplier<T> constructor, BiConsumer<T,IConnection> processor){
        PacketChannel<T> channel = new PacketChannel<>(packet, constructor, processor, idChannels.size());
        channels.put(packet,channel);
        idChannels.put(channel.getId(),channel);
    }

    public void sync(IConnection connection) throws IOException {
        BiMap<Integer, String> classMap = new BiMap<>();
        channels.forEach((i,p)->{
            classMap.put(p.getId(),p.getName(connection));
        });
        connection.send(new PacketSynchronize(classMap));
    }

    public void processSynchronizationPacket(PacketSynchronize packet){
        idChannels.clear();
        packet.getClassMap().forEach((k,v)->{
            try {
                Class<?> cls = Class.forName(v);
                PacketChannel<?> channel = channels.get(cls);
                if(channel != null){
                    channel.setIdentifier(k);
                    idChannels.put(k,channel);
                }
            } catch (ClassNotFoundException ignored) {}
        });
    }

    public void clear(){
        channels.clear();
        idChannels.clear();
    }

    public int identifierOf(IPacket packet) {
        return channels.get(packet.getClass()).getId();
    }

    public PacketChannel<?> getChannel(int id){
        return idChannels.get(id);
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }
}
