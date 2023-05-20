package com.vicious.viciouslib.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IPacket {
    void write(DataOutputStream dos) throws IOException;
    void read(DataInputStream dis) throws IOException;
}
