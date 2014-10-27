package com.company.logging;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class LoggerEval {

    private static Logger _INSTANCE1 = null;
    private static Logger _INSTANCE2 = null;

    public static void initLogger1(String logPath) {
        _INSTANCE1 = new Logger(logPath);
        new Thread(_INSTANCE1).start();
    }

    public static void initLogger2(String logPath) {
        _INSTANCE2 = new Logger(logPath);
        new Thread(_INSTANCE2).start();
    }

    public static Logger getLogger1() {
        return _INSTANCE1;
    }
    public static Logger getLogger2() {
        return _INSTANCE2;
    }
}
