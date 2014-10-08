package com.company.messaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Acceptor implements Runnable {

    private static Logger LOGGER_ = Logger.getLogger(Acceptor.class.getCanonicalName());

    private boolean _isRunning;

    private final ExecutorService _executor;
    private Selector _selector;
    private ServerSocketChannel _serverSocketChannel;

    public Acceptor(String host, int port, ExecutorService executor) {
        _isRunning = true;
        _executor = executor;

        init(host, port);
    }

    private void init(String host, int port) {
        System.out.print("\n");
        LOGGER_.log(Level.INFO, "Initializing Acceptor on " + host + ":" + port);
        try {
            _selector = Selector.open();
            _serverSocketChannel = ServerSocketChannel.open();
            _serverSocketChannel.configureBlocking(false);
            _serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
            SelectionKey key = _serverSocketChannel.register(_selector, SelectionKey.OP_ACCEPT);

            // Attaching the AcceptorHandler to the server channel
            key.attach(new AcceptorHandler(_executor, _selector, _serverSocketChannel));
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open the selector or server socket channel");
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        while(_isRunning) {
            try {
                _selector.select();
                Set<SelectionKey> selected = _selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    dispatch(key);
                }
            } catch (IOException e) {
                LOGGER_.log(Level.SEVERE, "Error while selecting SelectionKey");
                throw new RuntimeException(e);
            }
        }
    }

    void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        if(r != null) {
            r.run();
        }
    }
}
