package com.company.database_model;

import java.sql.Timestamp;

/**
 * Created by Jan Marti on 03.10.2014.
 * This class represents a message from the viewpoint of a system element that uses the database interface
 */
public class Message {

    private int _id;
    private int _sender;
    private int _receiver;
    private int _queue;
    private Timestamp _arrivalTime;
    private String _message;

    /** Constructor database get */
    public Message(int id, int sender, int receiver, int queue, Timestamp arrivalTime, String message) {
        _id = id;
        _sender = sender;
        _receiver = receiver;
        _queue = queue;
        _arrivalTime = arrivalTime;
        _message = message;
    }

    /** Constructor database put */
    public Message(int sender, int receiver, int queue, Timestamp arrivalTime, String message) {
        _id = 0; // dummy id
        _sender = sender;
        _receiver = receiver;
        _queue = queue;
        _arrivalTime = arrivalTime;
        _message = message;
    }

    public int getId() { return _id; }
    public int getSender() { return _sender; }
    public int getReceiver() { return _receiver; }
    public int getQueue() { return _queue; }
    public Timestamp getArrivalTime() { return _arrivalTime; }
    public String getMessage() { return _message; }

}
