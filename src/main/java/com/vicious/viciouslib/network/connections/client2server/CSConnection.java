package com.vicious.viciouslib.network.connections.client2server;

import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.PacketLexicon;

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
            serverSocket.close();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.serverSocket.isConnected();
    }
}
