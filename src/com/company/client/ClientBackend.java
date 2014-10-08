package com.company.client;

import com.company.exception.*;
import com.company.messaging.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class ClientBackend {

    private static Logger LOGGER_ = Logger.getLogger(ClientBackend.class.getCanonicalName());

    private Socket _socket;
    private DataInputStream _in;
    private DataOutputStream _out;

    private String _host;
    private int _port;

    private int _clientId;

    public ClientBackend(String host, int port) {
        _host = host;
        _port = port;
        init(_host, _port);
    }

    public void init(String host, int port) {
        try {
            _socket = new Socket(host, port);
            _in = new DataInputStream(_socket.getInputStream());
            _out = new DataOutputStream(_socket.getOutputStream());
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open socket. Stopping.");
            throw new RuntimeException(e);
        }
    }

    private void tearDown() throws IOException {
        _in.close();
        _out.close();
        _socket.close();
    }

    /**
     * Registers a client
     * Used by class MessageService
     */
    public void register(String clientId) throws ClientAlreadyExistsException, ClientRegisterFailureException {
        _clientId = clientId.hashCode();
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_CLIENT_REGISTER);
            _out.writeInt(_clientId);
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_CLIENT_CREATE_EXCEPTION) {
                    throw new ClientRegisterFailureException(new Exception());
                } else if (errorCode == Response.ERR_CLIENT_ALREADY_EXISTS_EXCEPTION) {
                    throw new ClientAlreadyExistsException(new Exception());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deregisters a client
     * Used by class MessageService
     */
    public void deregister() throws ClientDeregisterFailureException, ClientDoesNotExistException {
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_CLIENT_DEREGISTER);
            _out.writeInt(_clientId);
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_CLIENT_DELETE_EXCEPTION) {
                    throw new ClientDeregisterFailureException(new Exception());
                } else if(errorCode == Response.ERR_CLIENT_DOES_NOT_EXIST_EXCEPTION) {
                    throw new ClientDoesNotExistException(new Exception());
                }
            }
            tearDown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a queue
     * Used by class MessageService
     */
    public Queue createQueue(String queueId) throws QueueCreateException, QueueAlreadyExistsException {
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_QUEUE_CREATE);
            _out.writeInt(queueId.hashCode());
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_QUEUE_CREATE_EXCEPTION) {
                    throw new QueueCreateException(new Exception());
                } else if(errorCode == Response.ERR_QUEUE_ALREADY_EXISTS_EXCEPTION) {
                    throw new QueueAlreadyExistsException(new Exception());
                }
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a queue
     * Used by class MessageService
     */
    public Queue getQueue(String queueId) throws QueueGetException, QueueDoesNotExistException{
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_QUEUE_GET);
            _out.writeInt(queueId.hashCode());
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_QUEUE_GET_EXCEPTION) {
                    throw new QueueGetException(new Exception());
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException(new Exception());
                }
            }
            return new Queue(this, queueId.hashCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a queue
     * Used by class MessageService
     */
    public void deleteQueue(String queueId) throws QueueDeleteException, QueueDoesNotExistException {
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_QUEUE_DELETE);
            _out.writeInt(queueId.hashCode());
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_QUEUE_DELETE_EXCEPTION) {
                    throw new QueueDeleteException(new Exception());
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new QueueDoesNotExistException(new Exception());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enqueues a message
     * Used by class Queue
     */
    public void enqueueMessage(int queueId, Message message) throws  MessageEnqueueException, MessageEnqueueSenderDoesNotExistException, MessageEnqueueQueueDoesNotExistException {
        try {
            _out.writeInt(20 + message.getMessage().getBytes().length); //Size
            _out.writeInt(Response.MSG_QUEUE_ENQUEUE);
            _out.writeInt(_clientId); //Sender
            _out.writeInt(message.getReceiver()); //Receiver
            _out.writeInt(queueId); //Queue
            _out.writeInt(message.getMessage().getBytes().length);
            _out.write(message.getMessage().getBytes());
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_MESSAGE_ENQUEUE_EXCEPTION) {
                    throw new MessageEnqueueException(new Exception());
                } else if(errorCode == Response.ERR_SENDER_DOES_NOT_EXIST_EXCEPTION) {
                    throw new MessageEnqueueSenderDoesNotExistException(new Exception());
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new MessageEnqueueQueueDoesNotExistException(new Exception());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dequeues a message
     * Used by class Queue
     */
    public Message getMessage(int queueId, int senderId, boolean peek) throws MessageDequeueException, MessageDequeueQueueDoesNotExistException, MessageDequeueEmptyQueueException, MessageDequeueNotIntendedReceiverException {
        try {
            _out.writeInt(20); //Size
            _out.writeInt(Response.MSG_QUEUE_DEQUEUE);
            _out.writeInt(_clientId); //Receiver
            _out.writeInt(senderId); //Particular sender
            _out.writeInt(queueId); //Queue
            _out.writeBoolean(peek);
            _out.flush();

            int messageType = _in.readInt();
            if(messageType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_MESSAGE_DEQUEUE_EXCEPTION) {
                    throw new MessageDequeueException(new Exception());
                } else if(errorCode == Response.ERR_QUEUE_DOES_NOT_EXIST_EXCEPTION) {
                    throw new MessageDequeueQueueDoesNotExistException(new Exception());
                } else if(errorCode == Response.ERR_EMPTY_QUEUE_EXCEPTION) {
                    throw new MessageDequeueEmptyQueueException(new Exception());
                } else if(errorCode == Response.ERR_NO_MESSAGE_EXCEPTION) {
                    throw new MessageDequeueNotIntendedReceiverException(new Exception());
                }
            }

            int sender = _in.readInt();
            int receiver = _in.readInt();
            int msgLength = _in.readInt();
            byte[] msg = new byte[msgLength];
            _in.read(msg, 0, msgLength);

            //Create object
            Message message = new Message(sender, receiver, new String(msg));
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
