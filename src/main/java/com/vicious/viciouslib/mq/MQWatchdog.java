package com.vicious.viciouslib.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.vicious.viciouslib.LoggerWrapper;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MQWatchdog {
    private ScheduledFuture<?> watchdogTask;
    int counter = 0;
    static final int MAX_RETRIES = 5;

    // Starts the watchdog to monitor for any rabbitMQ disconnects as a failsafe
    public MQWatchdog() {
        if (watchdogTask != null) watchdogTask.cancel(false);
        this.watchdogTask = startWatch();
        LoggerWrapper.logInfo("MQWatchdog: Started! Woof!");
    }

    // Kills the current watchdog process.
    public void kill() {
        watchdogTask.cancel(false);
        LoggerWrapper.logInfo("Watchdog Task Cancelled");
    }

    // Task to start the watchdog
    private ScheduledFuture<?> startWatch() {
        return Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            // Attempts to retry connection to rabbit mq
            counter++;
            if (counter > MQWatchdog.MAX_RETRIES) {
                LoggerWrapper.logError("MQWatchdog Max Retry Exceeded!");
                //TODO: implement some sort of Comm handler
                //DiscordDoor.sendMessage(LibCFG.getInstance().universalName.value(), "Unable to reconnect to MQ after 5 attempts! Creating new local MQ instance... If this message repeats, restart the server.");
                MQCore.getInstance().reconnect();
                return;
            }

            // Checks connections to rabbitMQ
            if (!MQCore.getInstance().isConnected()) {
                LoggerWrapper.logError("MQWatchdog: Connection FAIL; Attempting reconnect " + counter + " of 5");
                MQCore.getInstance().reconnect();
                return;
            }

            //Check Transmitter
            Channel tx = MQCore.getInstance().tx;
            try {
                tx.basicQos(1);
            } catch (ShutdownSignalException | IOException ex) {
                LoggerWrapper.logError("TX Channel Closed: " + tx.getCloseReason().getReason().protocolMethodName());
                MQCore.getInstance().channelReconnect();
                return;
            }

            //Check Listener
            Channel rx = MQCore.getInstance().rx;
            try {
                rx.basicQos(1);
            } catch (ShutdownSignalException | IOException ex) {
                LoggerWrapper.logError("RX Channel Closed: " + rx.getCloseReason().getReason().protocolMethodName());
                MQCore.getInstance().channelReconnect();
                return;
            }
            counter = 0;
        }, 1, 1, TimeUnit.MINUTES);
    }
}
