package com.vicious.viciouslib.network.connections.server2client;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.jarloader.ViciousEventBroadcaster;
import com.vicious.viciouslib.network.*;
import com.vicious.viciouslib.network.annotation.Permission;
import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.connections.Authorization;
import com.vicious.viciouslib.network.packet.IPacket;
import com.vicious.viciouslib.network.packet.IllegalPacketException;
import com.vicious.viciouslib.network.packet.PacketDisconnect;
import com.vicious.viciouslib.permission.IHasPermissions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S2CConnection implements IConnection, IHasPermissions {
    public static Set<S2CConnection> connections = new HashSet<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Authorization authorization;

    public S2CConnection(Socket clientSocket) throws IOException {
        connections.add(this);
        this.clientSocket = clientSocket;
        authorization = new Authorization.Anonymous(this);
        try {
            this.dis = new DataInputStream(clientSocket.getInputStream());
            this.dos = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            LoggerWrapper.logError("Encountered error establishing data stream", e);
        }

        LoggerWrapper.logInfo("Established connection with: " + this.ip());
        ServerLexicon.get().sync(this);
        ViciousEventBroadcaster.post(new ConnectionEvent.Opened(this));
        executor.submit(this::receivingThread);
    }

    public InetAddress ip() {
        return this.clientSocket.getInetAddress();
    }

    public void setAuthorization(Authorization auth){
        this.authorization=auth;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public void receivingThread() {
        try {
            IConnection.super.receivingThread();
        } catch (Exception var2) {
            this.internalServerError(var2);
        }
        disconnect();
        LoggerWrapper.logInfo("Closed connection with " + this.ip());
    }

    public void send(IPacket packet) throws IOException {
        IConnection.super.send(packet);
    }

    public <T extends IPacket> void receive(PacketChannel<T> channel) throws IOException {
        IConnection.super.receive(channel);
    }

    private void internalServerError(Exception e) {
        LoggerWrapper.logError("Disconnected " + this.ip() + " caused by ", e);
        this.sendMessage("internal server error.");
        this.disconnect();
    }

    public void disconnect() {
        try {
            if(!isClosed()) {
                this.send(new PacketDisconnect());
                this.close();
            }
        } catch (IOException var2) {
            LoggerWrapper.logError("Encountered an exception while disconnecting", var2);
        }

    }

    public boolean hasPermission(String permission) {
        if (this.authorization instanceof IHasPermissions) {
            return permission == null || ((IHasPermissions) this.authorization).hasPermission(permission);
        }
        else{
            return false;
        }
    }

    public boolean isClosed() {
        return this.clientSocket.isClosed();
    }

    public DataInputStream dis() {
        return this.dis;
    }

    public DataOutputStream dos() {
        return this.dos;
    }

    public PacketLexicon getLexicon() {
        return ServerLexicon.get();
    }

    public boolean shouldProcess(PacketChannel<?> channel) {
        if (!IConnection.super.shouldProcess(channel)) {
            String packetName = channel.getPacketClass().getName();
            throw new IllegalPacketException("Sent a packet that cannot be processed. Caused by a lack of permission or attempted to send packet to wrong side.\nPacket: " + packetName + "\nPermission: " + this.getPermission(channel));
        } else {
            return true;
        }
    }

    private String getPermission(PacketChannel<?> channel) {
        return channel.getPacketClass().isAnnotationPresent(Permission.class) ? ((Permission)channel.getPacketClass().getAnnotation(Permission.class)).value() : "<NONE>";
    }

    public void sendMessage(String s) {
    }

    public void close() {
        try {
            if(!this.clientSocket.isClosed()) {
                this.clientSocket.close();
            }
            connections.remove(this);
            ViciousEventBroadcaster.post(new ConnectionEvent.Closed(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return ip().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ip().equals(obj);
    }
}
