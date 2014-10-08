package com.company.client;

/**
 * Created by Jan Marti on 03.10.2014.
 * This class represents a queue from the viewpoint of a system element that represents a client
 */
public class Queue {

    private final MessageService _messageService;
    private int _id;

    public Queue(MessageService messageService, int id) {
        _messageService = messageService;
        _id = id;
    }

    /*public int getId() {
        return id_;
    }

    public void put(Message msg) throws SenderDoesNotExistException, QueueDoesNotExistException, MessageEnqueueingException {
        msgService_.put(id_, msg);
    }

    //Gets oldest message from queue
    public Message get() throws NoMessageInQueueException, NoMessageFromSenderException, MessageDequeueingException, QueueDoesNotExistException {
        return msgService_.get(id_, 0, false);
    }*/

}
