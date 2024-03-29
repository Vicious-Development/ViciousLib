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
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class CSConnection implements IConnection {
    private final Socket serverSocket;
    private InputStream stream;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean closed = false;

    public CSConnection(Socket serverSocket, ExecutorService executor) {
        this.serverSocket = serverSocket;
        try {
            this.stream=serverSocket.getInputStream();
            this.dis = new DataInputStream(stream);
            this.dos = new DataOutputStream(serverSocket.getOutputStream());
        } catch (Exception ignored) {
            disconnect();
        }
        ViciousEventBroadcaster.post(new ConnectionEvent.Opened(this));
        executor.submit(this::receivingThread);
    }

    public void receivingThread() {
        IConnection.super.receivingThread();
        closed=true;
        disconnect();
    }

    public void disconnect(){
        try {
            if(!isClosed()) {
                this.send(new PacketDisconnect());
                this.close();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed() || closed;
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
    public InputStream stream() {
        return stream;
    }

    @Override
    public PacketLexicon getLexicon() {
        return ClientLexicon.getInstance();
    }

    @Override
    public void close() {
        try {
            if(!this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
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

    public InetAddress ip(){
        return serverSocket.getInetAddress();
    }

    @Override
    public int hashCode() {
        InetAddress ip = ip();
        return Objects.hash(ip.getHostAddress(), Arrays.hashCode(ip.getAddress()),ip.getCanonicalHostName());
    }
}
