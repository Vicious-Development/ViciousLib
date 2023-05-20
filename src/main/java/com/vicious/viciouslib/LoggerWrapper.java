package com.vicious.viciouslib;

import org.apache.logging.log4j.Logger;

public class LoggerWrapper {
    public static Logger log4jLogger;
    public static java.util.logging.Logger javaLogger;

    public static void logError(String msg, Throwable t){
        logError(msg);
        t.printStackTrace();
    }

    public static void logError(String msg){
        if(log4jLogger != null){
            log4jLogger.error(msg);
        }
        else if(javaLogger != null){
            javaLogger.warning(msg);
        }
        else System.err.println(msg);
    }
    public static void logInfo(String msg){
        if(log4jLogger != null){
            log4jLogger.info(msg);
        }
        else if(javaLogger != null){
            javaLogger.info(msg);
        }
        else System.out.println(msg);
    }
}
