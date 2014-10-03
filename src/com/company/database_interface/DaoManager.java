package com.company.database_interface;

import java.sql.Connection;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class DaoManager {

    private Connection _connection = null;
    private MessageDao _messageDao = null;
    private QueueDao _queueDao = null;
    private ClientDao _clientDao = null;

    public void startConnection() {

    }

    public void endConnection() {


    }

    public void startTransaction() {


    }

    public void endTransaction() {


    }

    public void abortTransaction() {


    }

    public MessageDao getMessageDao() {
        if (_messageDao == null) {
            return new MessageDao(_connection);
        }
        return _messageDao;
    }

    public QueueDao getQueueDao() {
        if (_queueDao == null) {
            return new QueueDao(_connection);
        }
        return _queueDao;
    }

    public ClientDao getClientDao() {
        if (_clientDao == null) {
            return new ClientDao(_connection);
        }
        return _clientDao;
    }

}
