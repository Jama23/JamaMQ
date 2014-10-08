package com.company.database_interface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class DaoManager {

    private static Logger _LOGGER = Logger.getLogger(DaoManager.class.getCanonicalName());

    private Connection _connection = null;
    private MessageDao _messageDao = null;
    private QueueDao _queueDao = null;
    private ClientDao _clientDao = null;

    public void startDBConnection() {
        _LOGGER.log(Level.FINE, "Starting database connection.");
        _connection = PGConnectionPool.getInstance().getConnection();
    }

    public void endDBConnection() {
        try {
            _LOGGER.log(Level.FINE, "Ending database connection.");
            _connection.close();
        } catch (SQLException e) {
            _LOGGER.log(Level.SEVERE, "Ending database connection failed.");
            throw new RuntimeException(e);
        }
    }

    public void startDBTransaction() {
        try {
            _LOGGER.log(Level.FINE, "Starting database transaction.");
            _connection.setAutoCommit(false);
        } catch (SQLException e) {
            _LOGGER.log(Level.SEVERE, "Starting database transaction failed.");
            throw new RuntimeException(e);
        }
    }

    public void endDBTransaction() {
        try {
            _LOGGER.log(Level.FINE, "Ending database transaction.");
            _connection.commit();
        } catch (SQLException e) {
            _LOGGER.log(Level.SEVERE, "Ending database transaction failed.");
            throw new RuntimeException(e);
        }
    }

    public void abortDBTransaction() {
        try {
            _LOGGER.log(Level.FINE, "Aborting database transaction.");
            _connection.rollback();
        } catch (SQLException e) {
            _LOGGER.log(Level.SEVERE, "Aborting database transaction failed.");
            throw new RuntimeException(e);
        }
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
