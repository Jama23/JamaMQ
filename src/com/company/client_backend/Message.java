package com.company.client_backend;

/**
 * Created by Jan Marti on 08.10.2014.
 * This class represents a message from the viewpoint of a system element that represents a client
 */
public class Message {

    private int _sender;
    private int _receiver;
    private String _message;

    public Message(int sender, int receiver, String message) {
        _sender = sender;
        _receiver = receiver;
        _message = message;
    }

    public int getSender() { return _sender; }
    public int getReceiver() { return _receiver; }
    public String getMessage() { return _message; }

}
