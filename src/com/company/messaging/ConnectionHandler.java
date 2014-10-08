package com.company.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class ConnectionHandler implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(ConnectionHandler.class.getCanonicalName());

    private final ExecutorService _executor;
    private final Selector _selector;
    private final SocketChannel _socketChannel;
    private final SelectionKey _key;

    private ByteBuffer _buffer;

    public ConnectionHandler(ExecutorService executor, Selector selector, SocketChannel socketChannel, SelectionKey key) {
        _executor = executor;
        _selector = selector;
        _socketChannel = socketChannel;
        _key = key;

        _buffer = ByteBuffer.allocate(2048);
    }

    @Override
    public void run() {
        if(_key.isReadable()) {
            _LOGGER.log(Level.FINE, "reading");
            read();
        } else if(_key.isWritable()) {
            _LOGGER.log(Level.FINE, "writing");
            write();
        }
    }

    private void read() {
        try {
            int bytesRead = 0;
            int bytes, limit, pos, size;

            // clear buffer
            _buffer.clear();

            // read first message size
            do {
                if((bytes = _socketChannel.read(_buffer)) < 0) {
                    _LOGGER.log(Level.FINE, "Message size bytes: " + bytes);
                    _socketChannel.close();
                    return;
                }
                bytesRead += bytes;
            } while(bytesRead < 4);

            // safe buffer limit and pos
            limit = _buffer.limit();
            pos = _buffer.position();

            // read message size from buffer
            _buffer.flip();
            size = _buffer.getInt() + 4;

            // restore limit and pos
            _buffer.limit(limit);
            _buffer.position(pos);

            // read more from the network..
            while(bytesRead < size){
                if((bytes = _socketChannel.read(_buffer)) < 0) {
                    _LOGGER.log(Level.FINE, "Message rest bytes: " + bytes);
                    _socketChannel.close();
                    return;
                }
                bytesRead += bytes;
            }

            // submit a client to the executor service
            _buffer.flip();
            _buffer.position(4);
            _executor.submit(new Connection(_buffer,
                    // register the write back callback
                    new Callback() {
                        @Override
                        public void callback() {
                            _LOGGER.log(Level.FINE, "Calling callback");
                            _key.interestOps(SelectionKey.OP_WRITE);
                            _selector.wakeup();
                            _LOGGER.log(Level.FINE, "Interest ops set");
                        }
                    }));
        } catch (IOException e) {
            _LOGGER.log(Level.SEVERE, "Could not read from socket channel");
            throw new RuntimeException(e);
        }
    }

    private void write() {
        try {
            _buffer.flip();
            _socketChannel.write(_buffer);
            _key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            _LOGGER.log(Level.SEVERE, "Could not write to socket channel");
            throw new RuntimeException(e);
        }
    }
}


