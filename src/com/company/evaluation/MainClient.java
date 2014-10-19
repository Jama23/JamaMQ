package com.company.evaluation;

import com.company.client.MessageFactory;
import com.company.client.MessageService;
import com.company.client_backend.Message;
import com.company.client_backend.Queue;
import com.company.exception.*;
import com.company.logging.LoggerSingleton;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 19.10.2014.
 */
public class MainClient implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(MainClient.class.getCanonicalName());
    //private static com.company.logging.Logger _EVALLOG = LoggerSingleton.getLogger();

    private String _host;
    private int _port;

    private final int _producerCount;
    private final int _putPerProdCount;

    private final int _consumerCount;
    private final int _getPerConsCount;

    private Producer[] _producers;
    private Consumer[] _consumers;


    public static void main(String[] args) {
        if(args.length != 6) {
            System.out.println("Argument missing: host, port, number of producers, puts per producer, number of consumers, gets per consumer.");
        }
        else {
            MainClient mainClient = new MainClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
            new Thread(mainClient).start();
        }
    }

    public MainClient(String host, int port, int producerCount, int putPerProdCount, int consumerCount, int getPerConsCount) {
        _host = host;
        _port = port;
        _producerCount = producerCount;
        _putPerProdCount = putPerProdCount;
        _consumerCount = consumerCount;
        _getPerConsCount = getPerConsCount;
    }

    private void setUp() {
        MessageService messageService = new MessageService(_host, _port);
        try {
            messageService.register(1);
            messageService.createQueue(1);
            messageService.deregister();
        } catch (ClientAlreadyExistsException e) {
            e.printStackTrace();
        } catch (ClientRegisterFailureException e) {
            e.printStackTrace();
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
        } catch (QueueAlreadyExistsException e) {
            e.printStackTrace();
        } catch (QueueCreateException e) {
            e.printStackTrace();
        } catch (ClientDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientDeregisterFailureException e) {
            e.printStackTrace();
        }

        _producers = new Producer[_producerCount];
        for(int i = 0; i < _producerCount; i++) {
            _producers[i] = new Producer(_host, _port, i+1, 1);
            new Thread(_producers[i]).start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _consumers = new Consumer[_consumerCount];
        for(int j = 0; j < _consumerCount; j++) {
            _consumers[j] = new Consumer(_host, _port, _producerCount+j+1, 1);
            new Thread(_consumers[j]).start();
        }
    }

    private void tearDown() {

    }

    @Override
    public void run() {
        setUp();
    }


    public abstract class ClientThread implements Runnable {

        protected final String _host;
        protected final int _port;
        protected final int _clientId;
        protected final int _queueId;
        protected MessageService _messageService;
        protected Queue _queue;

        public ClientThread(String host, int port, int clientId, int queueId) {
            _host = host;
            _port = port;
            _clientId = clientId;
            _queueId = queueId;

            init();
        }

        protected void init() {
            _messageService = new MessageService(_host, _port);
            try {
                _messageService.register(_clientId);
                _queue = _messageService.getQueue(_queueId);
            } catch (ClientAlreadyExistsException e) {
                e.printStackTrace();
            } catch (ClientRegisterFailureException e) {
                e.printStackTrace();
            } catch (QueueGetException e) {
                e.printStackTrace();
            } catch (ClientNotRegisteredException e) {
                e.printStackTrace();
            } catch (QueueDoesNotExistException e) {
                e.printStackTrace();
            }
        }

        protected void tearDown() {
            try {
                _messageService.deregister();
            } catch (ClientDoesNotExistException e) {
                e.printStackTrace();
            } catch (ClientDeregisterFailureException e) {
                e.printStackTrace();
            }
        }

    }



    public class Producer extends ClientThread {

        protected long startTime, stopTime, elapsedTime;

        public Producer(String host, int port, int clientId, int queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            _LOGGER.log(Level.INFO, "Client " + _clientId + " started.");
            startTime = System.nanoTime();
            for (int i = 0; i < _putPerProdCount; i++) {
                try {
                    _queue.enqueueMessage(MessageFactory.createMessage("Client " + _clientId + " write: Hello Reader. Message " + (i+1)));
                } catch (MessageEnqueueException e) {
                    e.printStackTrace();
                } catch (MessageEnqueueSenderDoesNotExistException e) {
                    e.printStackTrace();
                } catch (MessageEnqueueQueueDoesNotExistException e) {
                    e.printStackTrace();
                }
            }
            stopTime = System.nanoTime();
            elapsedTime = (stopTime-startTime)/1000000000;
            tearDown();
            _LOGGER.log(Level.INFO, "Client " + _clientId + " stopped. Elapsed time: " + elapsedTime + " seconds.");
        }

    }

    public class Consumer extends ClientThread {

        protected long startTime, stopTime, elapsedTime;

        public Consumer(String host, int port, int clientId, int queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            _LOGGER.log(Level.INFO, "Client " + _clientId + " started.");
            startTime = System.nanoTime();
            for (int i = 0; i < _getPerConsCount; i++) {
                try {
                    Message message = _queue.dequeueMessage();
                    //_LOGGER.log(Level.INFO, "Client " + _clientId + " read: " + message.getMessage());
                } catch (MessageDequeueQueueDoesNotExistException e) {
                    e.printStackTrace();
                } catch (MessageDequeueException e) {
                    e.printStackTrace();
                } catch (MessageDequeueNotIntendedReceiverException e) {
                    e.printStackTrace();
                } catch (MessageDequeueEmptyQueueException e) {
                    //e.printStackTrace();
                    //_LOGGER.log(Level.INFO, "Nothing to dequeue. Empty queue.");
                    i = i-1;
                }
            }
            stopTime = System.nanoTime();
            elapsedTime = (stopTime-startTime)/1000000000;
            tearDown();
            _LOGGER.log(Level.INFO, "Client " + _clientId + " stopped. Elapsed time: " + elapsedTime + " seconds.");
        }

    }

}
