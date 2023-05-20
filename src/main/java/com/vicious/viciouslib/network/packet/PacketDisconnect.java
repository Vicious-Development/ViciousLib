package com.vicious.viciouslib.network.packet;

import com.vicious.viciouslib.network.Side;
import com.vicious.viciouslib.network.annotation.Directionality;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Directionality({Side.CLIENT,Side.SERVER})
public class PacketDisconnect implements IPacket {
    @Override
    public void write(DataOutputStream dos) throws IOException {}

    @Override
    public void read(DataInputStream dis) throws IOException {}
}
