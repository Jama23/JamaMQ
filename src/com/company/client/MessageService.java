package com.company.client;

import com.company.exception.ClientAlreadyExistsException;
import com.company.exception.ClientDeregisterFailureException;
import com.company.exception.ClientDoesNotExistException;
import com.company.exception.ClientRegisterFailureException;
import com.company.messaging.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 08.10.2014.
 * This class represents a message service that a client can use to enqueue (dequeue) messages to (from) the database
 */
public class MessageService {

    private static Logger LOGGER_ = Logger.getLogger(MessageService.class.getCanonicalName());

    private Socket _socket;
    private DataInputStream _in;
    private DataOutputStream _out;

    private String _host;
    private int _port;

    private int _clientId;

    public MessageService(String host, int port) {
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

    public void register(String clientId) throws ClientRegisterFailureException, ClientAlreadyExistsException {
        register(clientId.hashCode());
    }

    public void register(int clientId) throws ClientRegisterFailureException, ClientAlreadyExistsException {
        _clientId = clientId;
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_REGISTER);
            _out.writeInt(clientId);
            _out.flush();

            int msgType = _in.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_CLIENT_CREATION_EXCEPTION) {
                    throw new ClientRegisterFailureException(new Exception());
                } else if (errorCode == Response.ERR_CLIENT_EXISTS_EXCEPTION) {
                    throw new ClientAlreadyExistsException(new Exception());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deregister() throws ClientDeregisterFailureException, ClientDoesNotExistException {
        try {
            _out.writeInt(8); //Size
            _out.writeInt(Response.MSG_DEREGISTER);
            _out.writeInt(_clientId);
            _out.flush();

            int msgType = _in.readInt();
            if(msgType != Response.STATUS_OK) {
                int errorCode = _in.readInt();
                if(errorCode == Response.ERR_CLIENT_DELETION_EXCEPTION) {
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
}
