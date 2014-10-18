package com.company.client;

import com.company.client_backend.ClientBackend;
import com.company.client_backend.Message;
import com.company.client_backend.Queue;
import com.company.exception.*;

import java.util.ArrayList;

/**
 * Created by Jan Marti on 08.10.2014.
 * This class represents a message service that a client can use to enqueue (dequeue) messages to (from) the database
 */
public class MessageService {

    private final ClientBackend _backend;

    public MessageService(String host, int port) { _backend = new ClientBackend(host, port); }

    public void register(int clientId) throws ClientAlreadyExistsException, ClientRegisterFailureException {
        _backend.register(clientId);
    }

    public void deregister() throws ClientDoesNotExistException, ClientDeregisterFailureException {
        _backend.deregister();
    }

    public Queue createQueue(int queueId) throws QueueAlreadyExistsException, QueueCreateException, ClientNotRegisteredException {
        if (_backend._registered) {
            return _backend.createQueue(queueId);
        } else {
            throw new ClientNotRegisteredException(new Exception());
        }
    }

    public Queue getQueue(int queueId) throws QueueDoesNotExistException, QueueGetException, ClientNotRegisteredException {
        if (_backend._registered) {
            return _backend.getQueue(queueId);
        } else {
            throw new ClientNotRegisteredException(new Exception());
        }
    }

    public void deleteQueue(int queueId) throws QueueDoesNotExistException, QueueDeleteException, ClientNotRegisteredException {
        if (_backend._registered) {
            _backend.deleteQueue(queueId);
        } else {
            throw new ClientNotRegisteredException(new Exception());
        }
    }

    public ArrayList<Integer> getWaitingQueueIds() throws QueueGetWaitingException, ClientNotRegisteredException {
        if (_backend._registered) {
            return _backend.getWaitingQueueIds();
        } else {
            throw new ClientNotRegisteredException(new Exception());
        }
    }

    public Message getMessageFromSender(int sender) throws MessageDequeueQueueDoesNotExistException, MessageDequeueException, MessageDequeueNotIntendedReceiverException, MessageDequeueEmptyQueueException, ClientNotRegisteredException {
        if (_backend._registered) {
            return _backend.getMessage(0, sender, false);
        } else {
            throw new ClientNotRegisteredException(new Exception());
        }
    }

}
