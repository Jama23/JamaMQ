package com.company.messaging;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class AcceptorHandler implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(AcceptorHandler.class.getCanonicalName());

    private final ExecutorService _executor;
    private final Selector _selector;
    private final ServerSocketChannel _serverSocketChannel;

    public AcceptorHandler(ExecutorService executor, Selector selector, ServerSocketChannel serverSocketChannel) {
        _executor = executor;
        _selector = selector;
        _serverSocketChannel = serverSocketChannel;
    }

    /**
     * Accept connection and attach connection handler
     */
    @Override
    public void run() {
        try {
            SocketChannel socketChannel = _serverSocketChannel.accept();
            if(socketChannel != null) {
                _LOGGER.log(Level.INFO, "Accepted new connection from: " + socketChannel.socket().getRemoteSocketAddress());
                socketChannel.configureBlocking(false);

                _selector.wakeup();
                _LOGGER.log(Level.FINE, "Waked up selector");

                SelectionKey key = socketChannel.register(_selector, SelectionKey.OP_READ);
                _LOGGER.log(Level.FINE, "Registered on selector");

                key.attach(new ConnectionHandler(_executor, _selector, socketChannel, key));
                _LOGGER.log(Level.FINE, "Attached ConnectionHandler");
            }
        } catch (IOException e) {
            _LOGGER.log(Level.SEVERE, "Could not open client socket channel");
            throw new RuntimeException(e);
        }
    }
}
