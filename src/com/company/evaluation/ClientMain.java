package com.company.evaluation;

import com.company.client.MessageFactory;
import com.company.client.MessageService;
import com.company.client_backend.Message;
import com.company.client_backend.Queue;
import com.company.exception.*;
import com.company.logging.LoggerEval;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 19.10.2014.
 */
public class ClientMain implements Runnable {

    private static Logger _LOGGER = Logger.getLogger(ClientMain.class.getCanonicalName());
    private static com.company.logging.Logger _EVALLOG = LoggerEval.getLogger1();

    private String _host;
    private int _port;

    private final int _producerCount;
    private final int _putPerProdCount;

    private final int _consumerCount;
    private final int _getPerConsCount;

    private final boolean _dbPopulated;
    private final int _populationSize;

    private final int _messageSize;

    private final int _instanceNo;

    private Producer[] _producers;
    private Consumer[] _consumers;


    /*public static void main(String[] args) {
        if(args.length != 9) {
            System.out.println("Arguments needed: host (string), port (int), number of producers (int), puts per producer (int), number of consumers (int), gets per consumer (int), populated or empty db (boolean), population size (int), message size(int)");
        }
        else {
            ClientMain clientMain = new ClientMain(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Boolean.parseBoolean(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]));
            new Thread(clientMain).start();
        }
    }*/

    public ClientMain(String host, int port, int producerCount, int putPerProdCount, int consumerCount, int getPerConsCount, boolean dbPopulated, int populationSize, int messageSize, int instanceNo) {
        _host = host;
        _port = port;
        _producerCount = producerCount;
        _putPerProdCount = putPerProdCount;
        _consumerCount = consumerCount;
        _getPerConsCount = getPerConsCount;
        _dbPopulated = dbPopulated;
        _populationSize = populationSize;
        _messageSize = messageSize;
        _instanceNo = instanceNo * 1000;
    }

    private void setUp() {

        MessageService messageService = new MessageService(_host, _port);
        try {
            messageService.register(_instanceNo);
            Queue _queue = messageService.createQueue(1);
            if (_dbPopulated) {
                _LOGGER.log(Level.INFO, "Populating of db started..");
                for (int i = 0; i < _populationSize; i++) {
                    _queue.enqueueMessage(MessageFactory.createMessage("Hello JamaMQ. I'm just an initial load message."));
                }
                _LOGGER.log(Level.INFO, "Populating of db ended.");
            }
            messageService.deregister();
        } catch (ClientAlreadyExistsException e) {
            e.printStackTrace();
        } catch (ClientRegisterFailureException e) {
            e.printStackTrace();
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
        } catch (QueueAlreadyExistsException e) {
            //e.printStackTrace();
            _LOGGER.log(Level.INFO, "Queue already created..");
        } catch (QueueCreateException e) {
            e.printStackTrace();
        } catch (ClientDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientDeregisterFailureException e) {
            e.printStackTrace();
        } catch (MessageEnqueueQueueDoesNotExistException e) {
            e.printStackTrace();
        } catch (MessageEnqueueException e) {
            e.printStackTrace();
        } catch (MessageEnqueueSenderDoesNotExistException e) {
            e.printStackTrace();
        }

        _producers = new Producer[_producerCount];
        for(int i = 0; i < _producerCount; i++) {
            _producers[i] = new Producer(_host, _port, _instanceNo+i+1, 1);
            new Thread(_producers[i]).start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _consumers = new Consumer[_consumerCount];
        for(int j = 0; j < _consumerCount; j++) {
            _consumers[j] = new Consumer(_host, _port, _instanceNo+_producerCount+j+1, 1);
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

        protected long clientStartTime, clientStopTime, elapsedTime, startTime, stopTime;

        public Producer(String host, int port, int clientId, int queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            _LOGGER.log(Level.INFO, "Client " + _clientId + " started.");
            Message message;
            switch (_messageSize) {
                case 200:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                           "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
                case 500:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
                case 1000:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
                case 1500:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
                case 2000:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
                default:
                    message = MessageFactory.createMessage("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
                    break;
            }
            clientStartTime = System.nanoTime();
            for (int i = 0; i < _putPerProdCount; i++) {
                try {
                    startTime = System.nanoTime();
                    _queue.enqueueMessage(message);
                    stopTime = System.nanoTime();
                    _EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_ENQUEUE");
                } catch (MessageEnqueueException e) {
                    e.printStackTrace();
                } catch (MessageEnqueueSenderDoesNotExistException e) {
                    e.printStackTrace();
                } catch (MessageEnqueueQueueDoesNotExistException e) {
                    e.printStackTrace();
                }
            }
            clientStopTime = System.nanoTime();
            elapsedTime = (clientStopTime-clientStartTime)/1000000000;
            tearDown();
            _LOGGER.log(Level.INFO, "Client " + _clientId + " stopped. Elapsed time: " + elapsedTime + " seconds.");
        }

    }

    public class Consumer extends ClientThread {

        protected long clientStartTime, clientStopTime, elapsedTime, startTime, stopTime;

        public Consumer(String host, int port, int clientId, int queueId) {
            super(host, port, clientId, queueId);
        }

        @Override
        public void run() {
            _LOGGER.log(Level.INFO, "Client " + _clientId + " started.");
            clientStartTime = System.nanoTime();
            for (int i = 0; i < _getPerConsCount; i++) {
                try {
                    startTime = System.nanoTime();
                    Message message = _queue.dequeueMessage();
                    stopTime = System.nanoTime();
                    _EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_DEQUEUE");
                } catch (MessageDequeueQueueDoesNotExistException e) {
                    e.printStackTrace();
                } catch (MessageDequeueException e) {
                    e.printStackTrace();
                } catch (MessageDequeueNotIntendedReceiverException e) {
                    e.printStackTrace();
                } catch (MessageDequeueEmptyQueueException e) {
                    //e.printStackTrace();
                    // Does not count as dequeue operation
                    i = i-1;
                }
            }
            clientStopTime = System.nanoTime();
            elapsedTime = (clientStopTime-clientStartTime)/1000000000;
            tearDown();
            _LOGGER.log(Level.INFO, "Client " + _clientId + " stopped. Elapsed time: " + elapsedTime + " seconds.");
        }

    }

}
