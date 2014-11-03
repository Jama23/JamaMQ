package com.company.logging;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class LoggerEval {

    private static Logger _INSTANCE1 = null;
    private static Logger _INSTANCE2 = null;
    private static Logger _INSTANCE3 = null;
    private static Logger _INSTANCE4 = null;
    private static Logger _INSTANCE5 = null;
    private static Logger _INSTANCE6 = null;
    private static Logger _INSTANCE7 = null;

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

    public static void initLogger5(String logPath) {
        _INSTANCE5 = new Logger(logPath);
        new Thread(_INSTANCE5).start();
    }

    public static void initLogger6(String logPath) {
        _INSTANCE6 = new Logger(logPath);
        new Thread(_INSTANCE6).start();
    }

    public static void initLogger7(String logPath) {
        _INSTANCE7 = new Logger(logPath);
        new Thread(_INSTANCE7).start();
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
    public static Logger getLogger5() {
        return _INSTANCE5;
    }
    public static Logger getLogger6() {
        return _INSTANCE6;
    }
    public static Logger getLogger7() {
        return _INSTANCE7;
    }

}
