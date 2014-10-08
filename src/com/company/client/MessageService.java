package com.company.client;

import com.company.exception.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan Marti on 08.10.2014.
 * This class represents a message service that a client can use to enqueue (dequeue) messages to (from) the database
 */
public class MessageService {

    private final ClientBackend _backend;

    public MessageService(String host, int port) { _backend = new ClientBackend(host, port); }

    public void register(String clientId) throws ClientAlreadyExistsException, ClientRegisterFailureException {
        _backend.register(clientId);
    }

    public void deregister() throws ClientDoesNotExistException, ClientDeregisterFailureException {
        _backend.deregister();
    }

    public Queue createQueue(String queueId) throws QueueAlreadyExistsException, QueueCreateException {
        return _backend.createQueue(queueId);
    }

    public Queue getQueue(String queueId) throws QueueDoesNotExistException, QueueGetException {
        return _backend.getQueue(queueId);
    }

    public void deleteQueue(String queueId) throws QueueDoesNotExistException, QueueDeleteException {
        _backend.deleteQueue(queueId);
    }

    public List<Integer> getWaitingQueueIds() {
        return new ArrayList<Integer>(1);
    }

    public Message getMessageFromSender(String sender) {
        return new Message(0,0,"0");
    }

}
