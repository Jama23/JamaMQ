package com.company.client;

import com.company.exception.*;

/**
 * Created by Jan Marti on 03.10.2014.
 * This class represents a queue from the viewpoint of a system element that represents a client
 */
public class Queue {

    private final ClientBackend _clientBackend;
    private int _id;

    public Queue(ClientBackend messageService, int id) {
        _clientBackend = messageService;
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void enqueueMessage(Message message) throws MessageEnqueueException, MessageEnqueueSenderDoesNotExistException, MessageEnqueueQueueDoesNotExistException {
        _clientBackend.enqueueMessage(_id, message);
    }

    /* Dequeues topmost message from queue */
    public Message dequeueMessage() {
        return _clientBackend.getMessage(0, false);
    }

    /* Dequeues message from specific sender from queue */
    public Message dequeueMessageFromSender(int sender) {
        return _clientBackend.getMessage(sender, false);
    }

    /* Dequeues topmost message from queue */
    public Message peekMessage() {
        return _clientBackend.getMessage(0, true);
    }

    /* Dequeues message from specific sender from queue */
    public Message peekMessageFromSender(int sender) {
        return _clientBackend.getMessage(sender, true);
    }

}
