package com.company.database_interface;

import com.company.database_model.Message;
import com.company.database_model.ModelFactory;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class MessageDao {

    private Connection _connecton = null;

    public MessageDao(Connection connection) {
        _connecton = connection;
    }

    public void enqueueMessage(Message message) {

    }

    public Message dequeueMessage() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        Message m = ModelFactory.createMessage(0, 0, 0, t, "bla");
        return m;
    }

    public Message peekMessage() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        Message m = ModelFactory.createMessage(0, 0, 0, t, "bla");
        return m;
    }
}
