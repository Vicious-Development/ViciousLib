package com.vicious.viciouslib.network.connections;

import com.vicious.viciouslib.network.connections.server2client.S2CConnection;

public interface Authorization {
    S2CConnection getConnection();
    class Anonymous implements Authorization {
        private final S2CConnection connection;

        public Anonymous(S2CConnection socket){
            this.connection = socket;
        }

        @Override
        public S2CConnection getConnection() {
            return connection;
        }
    }
}
