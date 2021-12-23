package com.vicious.viciouslib;

import com.vicious.viciouslib.configuration.ConfigurationValue;
import com.vicious.viciouslib.configuration.JSONConfig;

import java.nio.file.Path;

public class LibCFG extends JSONConfig {
    private static LibCFG instance;
    private LibCFG(Path p){
        super(p);
    }
    public static LibCFG getInstance(){
        if(instance == null) instance = new LibCFG(LibConstants.libConfigPath);
        return instance;
    }

    public ConfigurationValue<String> universalName = add(new ConfigurationValue<>("UniversalName",()->"unset",this).description("The name used by ViciousLib in communications."));

    public ConfigurationValue<Boolean> mqEnabled = add(new ConfigurationValue<>("IsMQEnabled",()->true,this).description("Is rabbitMQ enabled?"));
    public ConfigurationValue<String> mqHost = add(new ConfigurationValue<>("MQHost",()->"unset",this).parent(mqEnabled).description("rabbitMQ Host"));
    public ConfigurationValue<String> mqUser = add(new ConfigurationValue<>("MQUser",()->"unset",this).parent(mqEnabled).description("rabbitMQ User"));
    public ConfigurationValue<String> mqPass = add(new ConfigurationValue<>("MQPass",()->"unset",this).parent(mqEnabled).description("rabbitMQ Pass"));
    public ConfigurationValue<Integer> mqHeartbeat = add(new ConfigurationValue<>("MQHeartBeat",()->30,this).parent(mqEnabled).description("rabbitMQ heartbeat."));
    public ConfigurationValue<Integer> mqTimeout = add(new ConfigurationValue<>("MQTimeout",()->60,this).parent(mqEnabled).description("rabbitMQ timeout."));
    public ConfigurationValue<Boolean> mqAutomaticRecovery = add(new ConfigurationValue<>("MQAutomaticRecovery",()->false,this).parent(mqEnabled).description("rabbitMQ automatic recovery?"));
    public ConfigurationValue<Boolean> MQ404 = add(new ConfigurationValue<>("MQHandler404",()->false,this).parent(mqEnabled).description("rabbitMQ should mq404 errors be thrown?"));
    public ConfigurationValue<Integer> mqMaxChannels = add(new ConfigurationValue<>("MQMaxChannels",()->2,this).parent(mqEnabled).description("rabbitMQ maximum channels. Min of 2."));

}
