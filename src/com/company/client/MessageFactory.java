package com.company.client;

/**
 * Created by Jan Marti on 08.10.2014.
 * This class creates a client message that will be enqueued
 */
public class MessageFactory {

    /** Used when instantiating message object without specific receiver */
    public static Message createMessage(String message) {
        return createMessage(0, message);
    }

    /** Used when instantiating message object with specific receiver */
    public static Message createMessage(int receiver, String message) {
        return new Message(0, receiver, message);
    }

}
