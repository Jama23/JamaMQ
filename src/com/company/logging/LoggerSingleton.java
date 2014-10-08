package com.company.logging;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class LoggerSingleton {

    private static Logger _INSTANCE = null;

    public static void initLogger(String logPath) {
        _INSTANCE = new Logger(logPath);
        new Thread(_INSTANCE).start();
    }

    public static Logger getLogger() {
        return _INSTANCE;
    }
}
