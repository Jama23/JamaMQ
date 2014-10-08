package com.company.messaging;

import com.company.database_model.Message;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Response {

    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;

    public static final int MSG_CLIENT_REGISTER = 1;
    public static final int MSG_CLIENT_DEREGISTER = 2;
    public static final int MSG_QUEUE_CREATE = 3;
    public static final int MSG_QUEUE_GET = 4;
    public static final int MSG_QUEUE_DELETE = 5;
    public static final int MSG_QUEUE_ENQUEUE = 6;
    public static final int MSG_QUEUE_DEQUEUE= 7;



    public static final int ERR_CLIENT_CREATE_EXCEPTION = 11;
    public static final int ERR_CLIENT_ALREADY_EXISTS_EXCEPTION = 12;
    public static final int ERR_CLIENT_DELETE_EXCEPTION = 13;
    public static final int ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION = 14;

    public static final int ERR_QUEUE_CREATE_EXCEPTION = 21;
    public static final int ERR_QUEUE_ALREADY_EXISTS_EXCEPTION = 22;
    public static final int ERR_QUEUE_GET_EXCEPTION = 23;
    public static final int ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION = 24;
    public static final int ERR_QUEUE_DELETE_EXCEPTION = 25;

    public static final int ERR_MESSAGE_ENQUEUE_EXCEPTION = 81;
    public static final int ERR_SENDER_DOES_NOT_EXIST_EXCEPTION = 82;
    public static final int ERR_MESSAGE_DEQUEUE_EXCEPTION = 83;
    public static final int ERR_EMPTY_QUEUE_EXCEPTION = 84;
    public static final int ERR_NO_MESSAGE_EXCEPTION = 85;


    /*private final int status_;
    private final int errorCode_;
    private final Message m_;

    private static Logger LOGGER_ = Logger.getLogger(Response.class.getCanonicalName());

    public static Response ok() {
        return new Response(STATUS_OK);
    }

    public static Response ok(Message m) {
        return new Response(STATUS_OK, m);
    }

    public static Response err(int errorCode) {
        return new Response(STATUS_ERROR, errorCode);
    }

    public Response(int status) {
        this(status, 0, null);
    }

    public Response(int status, Message m) {
        this(status, 0, m);
    }

    public Response(int status, int errorCode) {
        this(status, errorCode, null);
    }

    public Response(int status, int errorCode, Message m) {
        status_ = status;
        errorCode_ = errorCode;
        m_ = m;
    }

    public void serialize(ByteBuffer buffer) {
        LOGGER_.log(Level.FINE, "Serializing response: " + status_ + "/" + errorCode_);
        buffer.clear();
        buffer.putInt(status_);
        if(status_ != STATUS_OK) {
            buffer.putInt(errorCode_);
            return;
        } else if(m_ != null) {
            buffer.putInt(m_.getSender());
            buffer.putInt(m_.getReceiver());
            buffer.putInt(m_.getMessage().getBytes().length);
            buffer.put(m_.getMessage().getBytes());
        }
    }*/
}
