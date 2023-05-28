package com.vicious.viciouslib.network.connections.server2client;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.network.*;
import com.vicious.viciouslib.network.annotation.Permission;
import com.vicious.viciouslib.network.connections.IConnectable;
import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.packet.IPacket;
import com.vicious.viciouslib.network.packet.IllegalPacketException;
import com.vicious.viciouslib.network.packet.PacketDisconnect;
import com.vicious.viciouslib.permission.IHasPermissions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S2CConnection implements IConnection, IHasPermissions {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private IConnectable user;

    public S2CConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        try {
            this.dis = new DataInputStream(clientSocket.getInputStream());
            this.dos = new DataOutputStream(clientSocket.getOutputStream());
            this.user.setConnection(this);
        } catch (IOException e) {
            LoggerWrapper.logError("Encountered error establishing data stream", e);
        }

        LoggerWrapper.logInfo("Established connection with: " + this.ip());
        executor.submit(this::receivingThread);
    }

    public InetAddress ip() {
        return this.clientSocket.getInetAddress();
    }

    public void receivingThread() {
        try {
            IConnection.super.receivingThread();
        } catch (Exception var2) {
            this.internalServerError(var2);
        }
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
            this.send(new PacketDisconnect());
            this.close();
        } catch (IOException var2) {
            LoggerWrapper.logError("Encountered an exception while disconnecting", var2);
        }

    }

    public IConnectable getUser() {
        return this.user;
    }

    public void setUser(IConnectable user) {
        this.user = user;
    }

    public boolean hasPermission(String permission) {
        if (this.user instanceof IHasPermissions p) {
            return permission == null || p.hasPermission(permission);
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
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}