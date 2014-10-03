package com.company.database_model;

import java.sql.Timestamp;

/**
 * Created by Jan Marti on 03.10.2014.
 * This class creates message, queue and client objects used by system elements that use the database interface
 */
public class ModelFactory {

    /** Used when instantiating message object received form database */
    public static Message createMessage(int id, int sender, int receiver, int queue, Timestamp arrivalTime, String message) {
        return new Message(id, sender, receiver, queue, arrivalTime, message);
    }

    /** Used when instantiating message object that will be put to database */
    public static Message createMessage(int sender, int receiver, int queue, String message) {
        return createMessage(sender, receiver, queue, new Timestamp(System.currentTimeMillis()), message);
    }

    public static Message createMessage(int sender, int receiver, int queue, Timestamp arrivalTime, String message) {
        return createMessage(sender, receiver, queue, new Timestamp(System.currentTimeMillis()), message);
    }

    public static Queue createQueue(int id) {
        return createQueue(id, new Timestamp(System.currentTimeMillis()));
    }

    public static Queue createQueue(int id, Timestamp creationTime) {
        return new Queue(id, creationTime);
    }

    public static Client createClient(int id) {
        return createClient(id, new Timestamp(System.currentTimeMillis()));
    }

    public static Client createClient(int id, Timestamp creationTime) {
        return new Client(id, creationTime);
    }

}
