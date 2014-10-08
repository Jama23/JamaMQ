package com.company.client;

import com.company.exception.*;

/**
 * Created by Jan Marti on 08.10.2014.
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

    public Queue createQueue(String queueId) {
        return _backend.createQueue(queueId);
    }

    public Queue getQueue(String queueId) throws QueueDoesNotExistException {
        return _backend.getQueue(queueId);
    }

    public void deleteQueue(String queueId) throws QueueDoesNotExistException {
        _backend.deleteQueue(queueId);
    }





}
