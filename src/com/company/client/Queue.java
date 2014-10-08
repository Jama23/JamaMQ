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

    /*public int getId() {
        return _id;
    }*/

    public void enqueueMessage(Message message) throws MessageEnqueueException, MessageEnqueueSenderDoesNotExistException, MessageEnqueueQueueDoesNotExistException {
        _clientBackend.enqueueMessage(_id, message);
    }

    /**
     * Dequeues topmost message from queue
     */
    public Message dequeueMessage() throws MessageDequeueQueueDoesNotExistException, MessageDequeueException, MessageDequeueNotIntendedReceiverException, MessageDequeueEmptyQueueException {
        return _clientBackend.getMessage(_id, 0, false);
    }

    /**
     * Dequeues message from specific sender from queue
     */
    public Message dequeueMessageFromSender(String sender) throws MessageDequeueQueueDoesNotExistException, MessageDequeueException, MessageDequeueNotIntendedReceiverException, MessageDequeueEmptyQueueException {
        return _clientBackend.getMessage(_id, sender.hashCode(), false);
    }

    /**
     * Peeks topmost message from queue, i.e returns message but leaves it in queue
     */
    public Message peekMessage() throws MessageDequeueQueueDoesNotExistException, MessageDequeueException, MessageDequeueNotIntendedReceiverException, MessageDequeueEmptyQueueException {
        return _clientBackend.getMessage(_id, 0, true);
    }

    /**
     * Peeks topmost message from specific sender from queue, i.e returns message but leaves it in queue
     */
    public Message peekMessageFromSender(String sender) throws MessageDequeueQueueDoesNotExistException, MessageDequeueException, MessageDequeueNotIntendedReceiverException, MessageDequeueEmptyQueueException {
        return _clientBackend.getMessage(_id, sender.hashCode(), true);
    }

}
