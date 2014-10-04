package com.company.database_interface;

import com.company.database_model.Message;
import com.company.database_model.ModelFactory;
import com.company.exception.MessageEnqueueException;
import com.company.exception.MessageQueueDoesNotExistException;
import com.company.exception.MessageSenderDoesNotExistException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class MessageDao {

    private Connection _connection = null;

    public MessageDao(Connection connection) {
        _connection = connection;
    }

    public void enqueueMessage(Message message) throws MessageEnqueueException, MessageSenderDoesNotExistException, MessageQueueDoesNotExistException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call enqueueMessage(?,?,?,?,?) }");
            callStat.setInt(1, message.getSender());
            callStat.setInt(2, message.getReceiver());
            callStat.setInt(3, message.getQueue());
            callStat.setTimestamp(4, message.getArrivalTime());
            callStat.setString(5, message.getMessage());
            callStat.execute();
            callStat.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2005")) {
                throw new MessageSenderDoesNotExistException(e);
            } else if (e.getSQLState().equals("V2006")) {
                throw  new MessageQueueDoesNotExistException(e);
            }
            throw new MessageEnqueueException(e);
        }
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
