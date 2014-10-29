package com.company.messaging;

import com.company.logging.LoggerEval;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Connection implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(Connection.class.getCanonicalName());
    private static com.company.logging.Logger _EVALLOG1 = LoggerEval.getLogger1();
    private static com.company.logging.Logger _EVALLOG2 = LoggerEval.getLogger3();

    private final ByteBuffer _buffer;
    private final Callback _callback;

    private final long _startTime;

    public Connection(long startTime, ByteBuffer buffer, Callback callback) {
        _buffer = buffer;
        _callback = callback;

        _startTime = startTime;
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime();
        _LOGGER.log(Level.FINE, "Running executor: " + Thread.currentThread().getId());

        //long startTime = System.nanoTime();

        ExecutionEngine executionEngine = new ExecutionEngine();
        executionEngine.process(_buffer);
        // calling the callback to register a write interest on the selector.
        _callback.callback();

        long stopTime2 = System.nanoTime();

        _EVALLOG1.log(stopTime + "," + stopTime2 + ",MS_LATENCY");
        _EVALLOG2.log(_startTime + "," + stopTime + ",EXEC_WAIT_TIME");
    }
}