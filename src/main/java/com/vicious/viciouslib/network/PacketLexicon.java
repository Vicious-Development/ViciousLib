package com.vicious.viciouslib.network;

import com.vicious.viciouslib.jarloader.ViciousEventBroadcaster;
import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.packet.IPacket;
import com.vicious.viciouslib.network.packet.PacketSynchronize;
import com.vicious.viciouslib.util.BiMap;
import com.vicious.viciouslib.util.ClassMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketLexicon {
    private final ClassMap<PacketChannel<?>> channels = new ClassMap<>();
    private final Map<String, Class<?>> packetClasses = new HashMap<>();
    private final Map<Integer,PacketChannel<?>> idChannels = new HashMap<>();
    private Side side = Side.SERVER;

    //Ensure that PacketSynchronize always registers first.
    public PacketLexicon(BiConsumer<PacketSynchronize, IConnection> synchronizationProcessor){
        registerHandler(PacketSynchronize.class, PacketSynchronize::new,synchronizationProcessor);
    }

    public ClassMap<PacketChannel<?>> getChannels() {
        return channels;
    }

    public <T extends IPacket> void registerHandler(Class<T> packet, Supplier<T> constructor, BiConsumer<T,IConnection> processor){
        if(!channels.containsKey(packet)) {
            packetClasses.put(packet.getName(),packet);
            PacketChannel<T> channel = new PacketChannel<>(packet, constructor, processor, idChannels.size());
            channels.put(packet, channel);
            idChannels.put(channel.getId(), channel);
        }
        else{
            PacketChannel<T> channel = new PacketChannel<>(packet, constructor, processor, channels.get(packet).getId());
            channels.replace(packet,channel);
            idChannels.replace(channel.getId(),channel);
        }
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
            Class<?> cls = packetClasses.get(v);
            if(cls != null) {
                PacketChannel<?> channel = channels.get(cls);
                if (channel != null) {
                    channel.setIdentifier(k);
                    idChannels.put(k, channel);
                }
            }
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
