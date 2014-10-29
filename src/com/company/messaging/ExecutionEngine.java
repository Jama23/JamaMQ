package com.company.messaging;

import com.company.database_interface.DaoManager;
import com.company.database_model.DBModelFactory;
import com.company.database_model.Message;
import com.company.exception.*;
import com.company.logging.LoggerEval;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.messaging.Response.*;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class ExecutionEngine {

    private static Logger _LOGGER = Logger.getLogger(ExecutionEngine.class.getCanonicalName());
    //private static com.company.logging.Logger _EVALLOG = LoggerEval.getLogger4();

    private DaoManager _manager = new DaoManager();

    public void process(ByteBuffer _buffer) {
        int messageType = _buffer.getInt();
        Response response;

        long startTime, stopTime;

        switch (messageType) {
            case Response.MSG_CLIENT_REGISTER:
                _LOGGER.log(Level.FINE, "MSG_CLIENT_REGISTER");
                //startTime = System.nanoTime();
                response = registerClient(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_CLIENT_REGISTER");
                break;
            case Response.MSG_CLIENT_DEREGISTER:
                _LOGGER.log(Level.FINE, "MSG_CLIENT_DEREGISTER");
                //startTime = System.nanoTime();
                response = deregisterClient(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_CLIENT_DEREGISTER");
                break;
            case Response.MSG_QUEUE_CREATE:
                _LOGGER.log(Level.FINE, "MSG_QUEUE_CREATE");
                //startTime = System.nanoTime();
                response = createQueue(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_CREATE");
                break;
            case Response.MSG_QUEUE_GET:
                _LOGGER.log(Level.FINE, "MSG_QUEUE_GET");
                //startTime = System.nanoTime();
                response = getQueue(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_GET");
                break;
            case Response.MSG_QUEUE_DELETE:
                _LOGGER.log(Level.FINE, "MSG_QUEUE_DELETE");
                //startTime = System.nanoTime();
                response = deleteQueue(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_DELETE");
                break;
            case Response.MSG_QUEUE_ENQUEUE:
                _LOGGER.log(Level.FINE, "MSG_QUEUE_ENQUEUE");
                //startTime = System.nanoTime();
                response = enqueueMessage(_buffer);
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_ENQUEUE");
                break;
            case Response.MSG_QUEUE_DEQUEUE:
                _LOGGER.log(Level.FINE, "MSG_QUEUE_DEQUEUE");
                //startTime = System.nanoTime();
                response = dequeueMessage(_buffer);
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_QUEUE_DEQUEUE");
                break;
            case Response.MSG_GET_WAITING_QUEUES:
                _LOGGER.log(Level.FINE, "MSG_GET_WAITING_QUEUES");
                //startTime = System.nanoTime();
                response = getWaitingQueues(_buffer.getInt());
                //stopTime = System.nanoTime();
                response.serialize(_buffer);
                //_EVALLOG.log(startTime + "," + stopTime + ",MSG_GET_WAITING_QUEUES");
                break;
        }
        _LOGGER.log(Level.FINE, "End of execution engine process");
    }

    private Response registerClient(int clientId) {
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getClientDao().createClient(DBModelFactory.createClient(clientId));
        } catch (ClientCreateException e) {
            _manager.abortDBTransaction();
            return err(ERR_CLIENT_CREATE_EXCEPTION);
        } catch (ClientAlreadyExistsException e) {
            _manager.abortDBTransaction();
            return err(ERR_CLIENT_ALREADY_EXISTS_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok();
    }

    private Response deregisterClient(int clientId) {
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getClientDao().deleteClient(clientId);
        } catch (ClientDeleteException e) {
            _manager.abortDBTransaction();
            return err(ERR_CLIENT_DELETE_EXCEPTION);
        } catch (ClientDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok();
    }

    private Response createQueue(int queueId) {
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getQueueDao().createQueue(DBModelFactory.createQueue(queueId));
        } catch (QueueAlreadyExistsException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_ALREADY_EXISTS_EXCEPTION);
        } catch (QueueCreateException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_CREATE_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok();
    }

    private Response getQueue(int queueId) {
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getQueueDao().getQueue(queueId);
        } catch (QueueGetException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_GET_EXCEPTION);
        } catch (QueueDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok();
    }

    private Response deleteQueue(int queueId) {
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getQueueDao().deleteQueue(queueId);
        } catch (QueueDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } catch (QueueDeleteException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_DELETE_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok();
    }

    private Response enqueueMessage(ByteBuffer buffer) {
        int senderId = buffer.getInt();
        int receiverId = buffer.getInt();
        int queueId = buffer.getInt();
        int messageLength = buffer.getInt();
        byte[] message = new byte[messageLength];
        buffer.get(message);
        Message m = DBModelFactory.createMessage(senderId, receiverId, queueId, new String(message));
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            _manager.getMessageDao().enqueueMessage(m);
        } catch (MessageEnqueueException e) {
            _manager.abortDBTransaction();
            return err(ERR_MESSAGE_ENQUEUE_EXCEPTION);
        } catch (MessageEnqueueSenderDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_SENDER_DOES_NOT_EXIST_EXCEPTION);
        } catch (MessageEnqueueQueueDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } finally {
        _manager.endDBTransaction();
        _manager.endDBConnection();
        }
        return ok();
    }

    private Response dequeueMessage(ByteBuffer buffer) {
        int receiverId = buffer.getInt();
        int senderId = buffer.getInt();
        int queueId = buffer.getInt();
        int peekFlag = buffer.getInt();
        boolean peek = false;
        if (peekFlag == 1) {
            peek = true;
        }
        Message m;
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
            m = _manager.getMessageDao().dequeueMessage(receiverId, senderId, queueId, peek);
        } catch (MessageDequeueQueueDoesNotExistException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION);
        } catch (MessageDequeueException e) {
            _manager.abortDBTransaction();
            return err(ERR_MESSAGE_DEQUEUE_EXCEPTION);
        } catch (MessageDequeueNotIntendedReceiverException e) {
            _manager.abortDBTransaction();
            return err(ERR_NO_MESSAGE_EXCEPTION);
        } catch (MessageDequeueEmptyQueueException e) {
            _manager.abortDBTransaction();
            return err(ERR_EMPTY_QUEUE_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok(m);
    }

    private Response getWaitingQueues(int clientId) {
        ArrayList<Integer> waitingQueues = new ArrayList<Integer>();
        _manager.startDBConnection();
        _manager.startDBTransaction();
        try {
             waitingQueues = _manager.getQueueDao().getWaitingQueues(clientId);
        } catch (QueueGetWaitingException e) {
            _manager.abortDBTransaction();
            return err(ERR_QUEUE_GET_WAITING_EXCEPTION);
        } finally {
            _manager.endDBTransaction();
            _manager.endDBConnection();
        }
        return ok(waitingQueues);
    }
}
