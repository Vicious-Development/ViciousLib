package com.vicious.viciouslib.persistence;

import com.vicious.viciouslib.LibConstants;
import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.persistence.json.JSONFile;
import com.vicious.viciouslib.persistence.storage.PersistentAttribute;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class ViciousLibConfig extends JSONFile {
    private static final ViciousLibConfig INSTANCE = new ViciousLibConfig(LibConstants.libConfigPath.toString());
    public static ViciousLibConfig get(){
        return INSTANCE;
    }

    @Save(description = "The name used by ViciousLib in communications.")
    public PersistentAttribute<String> universalName = new PersistentAttribute<>("UniversalName",String.class,"unset");

    @Save(description = "RabbitMQ is a simple cross-application networking system. This is useful for servers.")
    public PersistentAttribute<Boolean> mqEnabled = new PersistentAttribute<>("MQEnabled",Boolean.class,false);

    @Save(parent = "MQEnabled")
    public PersistentAttribute<String> mqHost = new PersistentAttribute<>("Host",String.class,"unset");

    @Save(parent = "MQEnabled")
    public PersistentAttribute<String> mqUser = new PersistentAttribute<>("User",String.class,"unset");

    @Save(parent = "MQEnabled")
    public PersistentAttribute<String> mqPass = new PersistentAttribute<>("Pass",String.class,"unset");

    @Save(description = "How often the application should check that RabbitMQ is connected.", parent = "MQEnabled")
    public PersistentAttribute<Integer> mqHeartbeat = new PersistentAttribute<>("HeartBeat",Integer.class,30);

    @Save(description = "How long the application should wait to connect to an MQ server before quitting and throwing an error.", parent = "MQEnabled")
    public PersistentAttribute<Integer> mqTimeout = new PersistentAttribute<>("Timeout",Integer.class,60);

    @Save(description = "Whether or not MQ should attempt to reconnect", parent = "MQEnabled")
    public PersistentAttribute<Boolean> mqAutomaticRecovery = new PersistentAttribute<>("MQAutomaticRecovery",Boolean.class,false);

    @Save(description = "If enabled, attempting to process MQ messages without the required MQ message handlers will throw an exception in console.", parent = "MQEnabled")
    public PersistentAttribute<Boolean> mq404 = new PersistentAttribute<>("404",Boolean.class,false);

    @Save(description = "The maximum amount of MQ channels allowed.", parent = "MQEnabled")
    public PersistentAttribute<Integer> mqMaxChannels = new PersistentAttribute<>("MaxChannels",Integer.class,2);


    public ViciousLibConfig(String path) {
        super(path);
        Aunotamation.processObject(this);
    }
}
