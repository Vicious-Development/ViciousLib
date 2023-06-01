package com.vicious.viciouslib.network;

import com.vicious.viciouslib.network.connections.IConnection;
import com.vicious.viciouslib.network.connections.server2client.S2CConnection;

public class ConnectionEvent {
    private final IConnection connection;
    public ConnectionEvent(IConnection connection){
        this.connection=connection;
    }

    public IConnection getConnection() {
        return connection;
    }

    public static class Opened extends ConnectionEvent{
        public Opened(IConnection connection) {
            super(connection);
        }
    }

    public static class Closed extends ConnectionEvent{
        public Closed(IConnection connection) {
            super(connection);
        }
    }
    public static class Sychronized extends ConnectionEvent{
        public Sychronized(IConnection connection) {
            super(connection);
        }
    }
}
