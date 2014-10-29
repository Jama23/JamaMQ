package com.company.logging;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class LoggerEval {

    private static Logger _INSTANCE1 = null;
    private static Logger _INSTANCE2 = null;
    private static Logger _INSTANCE3 = null;
    private static Logger _INSTANCE4 = null;

    public static void initLogger1(String logPath) {
        _INSTANCE1 = new Logger(logPath);
        new Thread(_INSTANCE1).start();
    }

    public static void initLogger2(String logPath) {
        _INSTANCE2 = new Logger(logPath);
        new Thread(_INSTANCE2).start();
    }

    public static void initLogger3(String logPath) {
        _INSTANCE3 = new Logger(logPath);
        new Thread(_INSTANCE3).start();
    }

    public static void initLogger4(String logPath) {
        _INSTANCE4 = new Logger(logPath);
        new Thread(_INSTANCE4).start();
    }

    public static Logger getLogger1() {
        return _INSTANCE1;
    }
    public static Logger getLogger2() {
        return _INSTANCE2;
    }
    public static Logger getLogger3() {
        return _INSTANCE3;
    }
    public static Logger getLogger4() {
        return _INSTANCE4;
    }
}
