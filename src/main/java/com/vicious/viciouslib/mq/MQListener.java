package com.vicious.viciouslib.mq;

import com.vicious.viciouslib.LibCFG;
import com.rabbitmq.client.*;

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
            rx.queueBind(LibCFG.getInstance().universalName.value(), "amq.fanout", "");
            consumer = new DefaultConsumer(rx) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        MQCore.getInstance().handler.handleMessage(body);
                    } catch (Exception ex) {
                        System.err.println("Couldn't handle MQ message @ handleDelivery()" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            };
            rx.basicConsume(LibCFG.getInstance().universalName.value(), true, consumer);
            System.out.println("MQ Listener Consumer Established");
        } catch (Exception ex) {
            System.err.println("MQ Consumer Establishment Failed!" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}