package com.vicious.viciouslib.mq;

import com.vicious.viciouslib.LibCFG;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

// This class handles the core function in communication with RabbitMQ which controls the various integrations within the network.
public class MQCore {
    private static MQCore instance;

    private MQCore(){
        if(LibCFG.getInstance().mqEnabled.getBoolean()) start();
        handler = new MQMessageHandler();
    }
    public static MQCore getInstance(){
        if(instance == null){
            instance = new MQCore();
        }
        return instance;
    }
    private Connection connection;
    private MQListener listener;
    public MQMessageHandler handler;
    private MQWatchdog watchdog;
    public Channel tx;
    public Channel rx;

    // Sets the connection information from the config
    public void setConnection() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(LibCFG.getInstance().mqHost.value());
            factory.setUsername(LibCFG.getInstance().mqUser.value());
            factory.setPassword(LibCFG.getInstance().mqPass.value());
            factory.setAutomaticRecoveryEnabled(LibCFG.getInstance().mqAutomaticRecovery.value());
            factory.setRequestedHeartbeat(LibCFG.getInstance().mqHeartbeat.value());
            factory.setConnectionTimeout(LibCFG.getInstance().mqTimeout.value());
            final ExceptionHandler eh = new DefaultExceptionHandler() {
                @Override
                public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName) {
                    System.err.println(" - Error raised by: " + channel.getChannelNumber() + " Method " + methodName + ": " + exception.getMessage());
                    exception.printStackTrace();
                }
            };
            factory.setExceptionHandler(eh);
            factory.setRequestedChannelMax(LibCFG.getInstance().mqMaxChannels.value());
            connection = factory.newConnection(LibCFG.getInstance().universalName.value());
            connection.addShutdownListener(cause -> {
                String message = "MQ connection shutdown due to " + cause.getReason().protocolMethodName() + "::\n" + cause.getLocalizedMessage();
                System.err.println(message);
                if (cause.getReason().protocolMethodName().equals("connection.close")) {
                    watchdog.kill();
                    System.out.println("[FS] Detected normal MQ shutdown. Not restarting.");
                } else {
                    System.out.println("[FS] Detected improper MQ shutdown, restarting.");
                    //TODO:reimpl:
                }
            });
            connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String s) throws IOException {
                    System.err.println("MQ Connection Blocked: " + s);
                }

                @Override
                public void handleUnblocked() throws IOException {
                    System.out.println("MQ Connection Unblocked");
                }
            });
        } catch (Exception ex) {
            System.err.println("Could not create new MQ connection." + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Opens the channels for rabbitMQ
    public void setChannels() {

        // Transmit Channel
        try {
            tx = connection.createChannel(1);
            tx.queueDeclare(LibCFG.getInstance().universalName.value(), true, false, false, null);
            tx.basicQos(1);
        } catch (Exception ex) {
            System.err.println("Could not set TX channels");
            ex.printStackTrace();
        }

        // Receive Channel
        try {
            rx = connection.createChannel(2);
            rx.queueDeclare(LibCFG.getInstance().universalName.value(), true, false, false, null);
            rx.basicQos(1);
        } catch (Exception ex) {
            System.err.println("Could not set RX channels");
            ex.printStackTrace();
        }
    }

    // Starts the connection, listener, then watchdog.
    public void start() {
        try {
            setConnection();
            setChannels();
            listener = new MQListener(rx);
            System.out.println("RabbitMQ Init Complete");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Nulls the current references and forces a reinitialisation of the connection and listener objects.
    public void reconnect() {
        close("reconnect()");
        try {
            setConnection();
            setChannels();
            listener = new MQListener(rx);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Closes the channels and opens new ones
    public void channelReconnect() {
        try {
            if (rx.isOpen()) rx.close(0, "channelReconnect() method");
        } catch (Exception ex) {
            System.err.println("Can't close RX");
            ex.printStackTrace();
        }
        try {
            if (tx.isOpen()) tx.close(0, "channelReconnect() method");
        } catch (Exception ex) {
            System.err.println("Can't close TX");
            ex.printStackTrace();
        }
        try {
            setChannels();
            listener = null;
            listener = new MQListener(rx);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Sends a message to the RabbitMQ server.
    public void singleMQ(String queue, byte[] message) {
        try {
            tx.basicPublish("", queue, null, message);
            //TODO:reimpl:plugin.getComm().debug("Sent Single MQ to " + queue);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }


    // Broadcast a message using the FANOUT exchange type so all other servers get the message as well.
    public void broadcastMQ(byte[] message) {
        try {
            String queue = LibCFG.getInstance().universalName.value();
            tx.basicPublish("amq.fanout", queue, null, message);
            //TODO:reimpl:plugin.getComm().debug("Sent MQ " + queue);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Close the connection, used by the plugin shutdown method
    public void close(String reason) {
        try {
            rx.close();
            tx.close();
            connection.close();
        } catch(TimeoutException | IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Returns a string array with the config information from mq.yml
    String[] getMQConfig() {
        String file = "mq.yml";
        String[] mq = new String[3];
        try {

            // Checks if the file exists
            File inFile = new File(file);
            if (!inFile.isFile()) {
                System.err.println("Unable to find mq.yml config in server root directory!");
                return null;
            }

            // Reads the configuration information from the file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("host: "))
                    mq[0] = line.replace("host: ", "");
                if (line.contains("user: "))
                    mq[1] = line.replace("user: ", "");
                if (line.contains("pass: "))
                    mq[2] = line.replace("pass: ", "");
            }
            br.close();
        } catch (IOException ex) {
            System.err.println("Unable to find mq.yml config in server root directory!");
            ex.printStackTrace();
        }
        System.out.println("MQ Config loaded, Host: " + mq[0]);
        return mq;
    }
    public boolean isConnected(){
        return connection != null && connection.isOpen();
    }
}
