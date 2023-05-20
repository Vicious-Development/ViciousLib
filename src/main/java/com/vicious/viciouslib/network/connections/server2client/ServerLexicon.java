package com.vicious.viciouslib.network.connections.server2client;


import com.vicious.viciouslib.network.PacketLexicon;
import com.vicious.viciouslib.network.packet.PacketDisconnect;

import java.io.IOException;

public class ServerLexicon extends PacketLexicon {
    private static final ServerLexicon instance = new ServerLexicon();

    public static ServerLexicon get() {
        return instance;
    }

    public ServerLexicon() {
        super((s, c) -> {
            try {
                get().sync(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.registerHandler(PacketDisconnect.class, PacketDisconnect::new, (p, c) -> {
            if (c instanceof S2CConnection sc) {
                sc.close();
            }
        });
    }
}
