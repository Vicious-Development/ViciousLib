package com.vicious.viciouslib.mq;

import com.rabbitmq.client.*;
import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.persistence.ViciousLibConfig;

import java.io.IOException;

// This is the consumer for RabbitMQ. It requires the channel created from MQCore in order to bind a consumer.
public class MQListener {
    private Channel rx;
    private Consumer consumer;
    // Starts listener as well.
    public MQListener(Channel rx) {
        this.rx = rx;
        listen();
    }

    // Method starts listener and sets the channel and consumer variables.
    public void listen() {
        try {
            rx.queueBind(ViciousLibConfig.get().universalName.value(), "amq.fanout", "");
            consumer = new DefaultConsumer(rx) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        MQCore.getInstance().handler.handleMessage(body);
                    } catch (Exception ex) {
                        LoggerWrapper.logError("Couldn't handle MQ message @ handleDelivery()" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            };
            rx.basicConsume(ViciousLibConfig.get().universalName.value(), true, consumer);
            LoggerWrapper.logInfo("MQ Listener Consumer Established");
        } catch (Exception ex) {
            LoggerWrapper.logError("MQ Consumer Establishment Failed!" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}