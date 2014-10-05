package com.company.database_interface;

import com.company.database_model.Message;
import com.company.database_model.ModelFactory;
import com.company.exception.MessageDequeueException;
import com.company.exception.MessageEnqueueException;
import com.company.exception.MessageQueueDoesNotExistException;
import com.company.exception.MessageSenderDoesNotExistException;

import java.sql.*;

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
                throw new MessageQueueDoesNotExistException(e);
            } else if (e.getSQLState().equals("V2006")) {
                throw  new MessageSenderDoesNotExistException(e);
            }
            throw new MessageEnqueueException(e);
        }
    }

    /**
     *
     * @param reqClientId:  ID of the requesting client. If receiver ID of message is not equal to 0 (particular
     *                      receiver specified), only requesting client with that id can de-queue message.
     * @param partSenderId: If partSenderId is not equal to 0, the requesting client queries for messages from a
     *                      particular sender.
     * @param queueId:      If queueId is not equal to 0, the requesting client queries a specific queue, otherwise the
     *                      client queries over all queues in the message system.
     *                      In case client queries over all queues, partSenderId must be provided, i.e. cannot be equal
     *                      to 0.
     * @param peek:         If peek is set to true, message that is returned from database will not be deleted.
     * @return
     */
    public Message dequeueMessage(int reqClientId, int partSenderId, int queueId, boolean peek) throws MessageDequeueException {
        if (!(queueId == 0 && partSenderId == 0)) {
            try {
                CallableStatement callStat = _connection.prepareCall("{ call dequeueMessage(?,?,?,?) }");
                callStat.setInt(1, reqClientId);
                callStat.setInt(2, partSenderId);
                callStat.setInt(3, queueId);
                callStat.setBoolean(4, peek);
                ResultSet resSet = callStat.executeQuery();
                resSet.next(); // if result set is empty we get an exception
                Message message = ModelFactory.createMessage(resSet.getInt(1), resSet.getInt(2), resSet.getInt(3),
                        resSet.getInt(4), resSet.getTimestamp(5), resSet.getString(6));
                resSet.close();
                callStat.close();
                return message;
            } catch (SQLException e) {
                throw new MessageDequeueException();
            }
        } else {
            throw new MessageDequeueException();
        }
    }
}
