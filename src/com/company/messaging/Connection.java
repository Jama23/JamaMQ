package com.company.messaging;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Connection implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(Connection.class.getCanonicalName());

    private final ByteBuffer _buffer;
    private final Callback _callback;

    public Connection(ByteBuffer buffer, Callback callback) {
        _buffer = buffer;
        _callback = callback;
    }

    @Override
    public void run() {
        _LOGGER.log(Level.FINE, "Running executor: " + Thread.currentThread().getId());
        ExecutionEngine executionEngine = new ExecutionEngine();
        executionEngine.process(_buffer);

        // calling the callback to register a write interest on the selector.
        _callback.callback();
    }
}