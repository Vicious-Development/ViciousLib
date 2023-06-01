package com.vicious.viciouslib.network.connections.client2server;

import com.vicious.viciouslib.jarloader.ViciousEventBroadcaster;
import com.vicious.viciouslib.network.ConnectionEvent;
import com.vicious.viciouslib.network.PacketChannel;
import com.vicious.viciouslib.network.Side;
import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.PacketLexicon;
import com.vicious.viciouslib.network.packet.PacketDisconnect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class CSConnection implements IConnection {
    private final Socket serverSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public CSConnection(Socket serverSocket, ExecutorService executor) {
        this.serverSocket = serverSocket;
        try {
            this.dis = new DataInputStream(serverSocket.getInputStream());
            this.dos = new DataOutputStream(serverSocket.getOutputStream());
        } catch (Exception ignored) {
            disconnect();
        }
        ViciousEventBroadcaster.post(new ConnectionEvent.Opened(this));
        executor.submit(this::receivingThread);
    }

    public void receivingThread() {
        try {
            IConnection.super.receivingThread();
        } catch (Exception ignored) {
            disconnect();
        }
    }

    public void disconnect(){
        try {
            send(new PacketDisconnect());
            close();
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed();
    }

    @Override
    public DataInputStream dis() {
        return dis;
    }

    @Override
    public DataOutputStream dos() {
        return dos;
    }

    @Override
    public PacketLexicon getLexicon() {
        return ClientLexicon.getInstance();
    }

    @Override
    public void close() {
        try {
            this.serverSocket.close();
            ViciousEventBroadcaster.post(new ConnectionEvent.Closed(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldProcess(PacketChannel<?> channel) {
        return channel.sendSide(Side.CLIENT);
    }

    public boolean isConnected() {
        return this.serverSocket.isConnected();
    }
}
