package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.MessageDao;
import com.company.database_interface.QueueDao;
import com.company.database_interface.ClientDao;
import com.company.database_interface.PGConnectionPool;
import com.company.exception.MessageQueueDoesNotExistException;
import com.company.exception.MessageSenderDoesNotExistException;
import com.company.database_model. Message;
import com.company.database_model.Queue;
import com.company.database_model.Client;
import com.company.database_model.ModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageDaoTest {
    Connection _connection;
    MessageDao _mdao;
    QueueDao _qdao;
    ClientDao _cdao;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        _connection = PGConnectionPool.getInstance().getConnection();
        _mdao = new MessageDao(_connection);
        _qdao = new QueueDao(_connection);
        _cdao = new ClientDao(_connection);
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue, client");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue, client");
        ps.execute();
        _connection.close();
    }

    @Test
    public void testEnqueueMessage() throws Exception {
        Client c = ModelFactory.createClient(23);
        _cdao.createClient(c);
        Queue q = ModelFactory.createQueue(3);
        _qdao.createQueue(q);
        Message m = ModelFactory.createMessage(23,2,3,"Hello Database!");
        _mdao.enqueueMessage(m);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM message WHERE sender = ?");
        ps.setInt(1, 23);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            int id = rs.getInt(1);
            int sender = rs.getInt(2);
            int receiver = rs.getInt(3);
            int queue = rs.getInt(4);
            Timestamp arrivaltime = rs.getTimestamp(5);
            String message = rs.getString(6);

            assertEquals("Enqueue message does not work. Sender not correct.", sender, m.getSender());
            assertEquals("Enqueue message does not work. Receiver not correct.", receiver, m.getReceiver());
            assertEquals("Enqueue message does not work. Queue not correct.", queue, m.getQueue());
            assertEquals("Enqueue message does not work. Arrival time not correct.", arrivaltime, m.getArrivalTime());
            assertEquals("Enqueue message does not work. Message not correct.", message, m.getMessage());
        } else {
            assert(false);
        }
    }

    @Test(expected=MessageQueueDoesNotExistException.class)
    public void testEnqueueMessage_MessageQueueDoesNotExistException() throws Exception {
        Client c = ModelFactory.createClient(23);
        _cdao.createClient(c);
        Message m = ModelFactory.createMessage(23,2,3,"Hello Database!");
        _mdao.enqueueMessage(m);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM message WHERE sender = ?");
        ps.setInt(1, 23);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            int id = rs.getInt(1);
            int sender = rs.getInt(2);
            int receiver = rs.getInt(3);
            int queue = rs.getInt(4);
            Timestamp arrivaltime = rs.getTimestamp(5);
            String message = rs.getString(6);

            assertEquals("Enqueue message does not work. Sender not correct.", sender, m.getSender());
            assertEquals("Enqueue message does not work. Receiver not correct.", receiver, m.getReceiver());
            assertEquals("Enqueue message does not work. Queue not correct.", queue, m.getQueue());
            assertEquals("Enqueue message does not work. Arrival time not correct.", arrivaltime, m.getArrivalTime());
            assertEquals("Enqueue message does not work. Message not correct.", message, m.getMessage());
        } else {
            assert(false);
        }
    }

    @Test(expected=MessageSenderDoesNotExistException.class)
    public void testEnqueueMessage_MessageSenderDoesNotExistException() throws Exception {
        Queue q = ModelFactory.createQueue(3);
        _qdao.createQueue(q);
        Message m = ModelFactory.createMessage(23,2,3,"Hello Database!");
        _mdao.enqueueMessage(m);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM message WHERE sender = ?");
        ps.setInt(1, 23);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            int id = rs.getInt(1);
            int sender = rs.getInt(2);
            int receiver = rs.getInt(3);
            int queue = rs.getInt(4);
            Timestamp arrivaltime = rs.getTimestamp(5);
            String message = rs.getString(6);

            assertEquals("Enqueue message does not work. Sender not correct.", sender, m.getSender());
            assertEquals("Enqueue message does not work. Receiver not correct.", receiver, m.getReceiver());
            assertEquals("Enqueue message does not work. Queue not correct.", queue, m.getQueue());
            assertEquals("Enqueue message does not work. Arrival time not correct.", arrivaltime, m.getArrivalTime());
            assertEquals("Enqueue message does not work. Message not correct.", message, m.getMessage());
        } else {
            assert(false);
        }
    }
}
