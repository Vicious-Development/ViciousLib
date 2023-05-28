package com.vicious.viciouslib.network.connections.client2server;

import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.PacketLexicon;
import com.vicious.viciouslib.network.Side;
import com.vicious.viciouslib.network.packet.PacketDisconnect;
import com.vicious.viciouslib.network.packet.PacketSynchronize;

import java.util.function.BiConsumer;

public class ClientLexicon extends PacketLexicon {
    private static final ClientLexicon instance = new ClientLexicon(getInstance()::process);

    private void process(PacketSynchronize packetSynchronize, IConnection connection) {
        processSynchronizationPacket(packetSynchronize);
    }

    public static ClientLexicon getInstance() {
        return instance;
    }

    public ClientLexicon(BiConsumer<PacketSynchronize, IConnection> synchronizationProcessor) {
        super(synchronizationProcessor);
        setSide(Side.CLIENT);
        this.registerHandler(PacketDisconnect.class, PacketDisconnect::new, (p, c) -> {
            if (c instanceof CSConnection) {
                c.close();
            }
        });
    }
}
