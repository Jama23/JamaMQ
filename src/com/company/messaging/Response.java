package com.company.messaging;

import com.company.database_model.Message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Response {

    /** Status Codes */
    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;

    /** Message Types */
    public static final int MSG_CLIENT_REGISTER = 1;
    public static final int MSG_CLIENT_DEREGISTER = 2;
    public static final int MSG_QUEUE_CREATE = 3;
    public static final int MSG_QUEUE_GET = 4;
    public static final int MSG_QUEUE_DELETE = 5;
    public static final int MSG_QUEUE_ENQUEUE = 6;
    public static final int MSG_QUEUE_DEQUEUE= 7;
    public static final int MSG_GET_WAITING_QUEUES = 8;

    /** Error Codes */
    public static final int ERR_CLIENT_CREATE_EXCEPTION = 11;
    public static final int ERR_CLIENT_ALREADY_EXISTS_EXCEPTION = 12;
    public static final int ERR_CLIENT_DELETE_EXCEPTION = 13;
    public static final int ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION = 14;

    public static final int ERR_QUEUE_CREATE_EXCEPTION = 21;
    public static final int ERR_QUEUE_ALREADY_EXISTS_EXCEPTION = 22;
    public static final int ERR_QUEUE_GET_EXCEPTION = 23;
    public static final int ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION = 24;
    public static final int ERR_QUEUE_DELETE_EXCEPTION = 25;
    public static final int ERR_QUEUE_GET_WAITING_EXCEPTION = 26;

    public static final int ERR_MESSAGE_ENQUEUE_EXCEPTION = 31;
    public static final int ERR_SENDER_DOES_NOT_EXIST_EXCEPTION = 32;
    public static final int ERR_MESSAGE_DEQUEUE_EXCEPTION = 33;
    public static final int ERR_EMPTY_QUEUE_EXCEPTION = 34;
    public static final int ERR_NO_MESSAGE_EXCEPTION = 35;


    private final int _status;
    private final int _errorCode;
    private final Message _message;
    private final ArrayList<Integer> _waitingQueues;

    private static Logger _LOGGER = Logger.getLogger(Response.class.getCanonicalName());

    public static Response ok() {
        return new Response(STATUS_OK);
    }

    public static Response ok(Message message) {
        return new Response(STATUS_OK, message);
    }

    public static Response ok(ArrayList<Integer> waitingQueues) {
        return new Response(STATUS_OK, waitingQueues);
    }

    public static Response err(int errorCode) {
        return new Response(STATUS_ERROR, errorCode);
    }

    public Response(int status) {
        this(status, 0, null, null);
    }

    public Response(int status, Message message) {
        this(status, 0, message, null);
    }

    public Response(int status, ArrayList<Integer> waitingQueues) {
        this(status, 0, null, waitingQueues);
    }

    public Response(int status, int errorCode) {
        this(status, errorCode, null, null);
    }

    public Response(int status, int errorCode, Message message, ArrayList<Integer> waitingQueues) {
        _status = status;
        _errorCode = errorCode;
        _message = message;
        _waitingQueues = waitingQueues;
    }

    public void serialize(ByteBuffer buffer) {
        _LOGGER.log(Level.FINE, "Serializing response: " + _status + "/" + _errorCode);
        buffer.clear();
        buffer.putInt(_status);
        if(_status != STATUS_OK) {
            buffer.putInt(_errorCode);
            return;
        } else if(_message != null) {
            buffer.putInt(_message.getSender());
            buffer.putInt(_message.getReceiver());
            buffer.putInt(_message.getMessage().getBytes().length);
            buffer.put(_message.getMessage().getBytes());
        }
        else if (_waitingQueues != null) {
            int size = _waitingQueues.size();
            buffer.putInt(size);
            for (int waitingQueueId : _waitingQueues) {
                buffer.putInt(waitingQueueId);
            }
        }
    }
}
