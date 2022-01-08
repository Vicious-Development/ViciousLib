package com.vicious.viciouslib;

import org.apache.logging.log4j.core.Logger;

public class LoggerWrapper {
    public static Logger log4jLogger;
    public static void logError(String msg){
        if(log4jLogger != null){
            log4jLogger.error(msg);
        }
        else System.err.println(msg);
    }
    public static void logInfo(String msg){
        if(log4jLogger != null){
            log4jLogger.info(msg);
        }
        else System.out.println(msg);
    }
}
