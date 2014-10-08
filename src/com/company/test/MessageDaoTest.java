package com.company.test;

import com.company.database_interface.*;
import com.company.messaging.Configuration;
import com.company.database_model.DBModelFactory;
import com.company.exception.*;
import com.company.database_model. Message;
import com.company.database_model.Queue;
import com.company.database_model.Client;
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
        Configuration.initConfig("var/config.prop");
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
        Client c = DBModelFactory.createClient(23);
        _cdao.createClient(c);
        Queue q = DBModelFactory.createQueue(3);
        _qdao.createQueue(q);
        Message m = DBModelFactory.createMessage(23, 2, 3, "Hello Database!");
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

    @Test(expected=MessageEnqueueQueueDoesNotExistException.class)
    public void testEnqueueMessage_MessageEnqueueQueueDoesNotExistException() throws Exception {
        Client c = DBModelFactory.createClient(23);
        _cdao.createClient(c);
        Message m = DBModelFactory.createMessage(23, 2, 3, "Hello Database!");
        _mdao.enqueueMessage(m);
    }

    @Test(expected=MessageEnqueueSenderDoesNotExistException.class)
    public void testEnqueueMessage_MessageEnqueueSenderDoesNotExistException() throws Exception {
        Queue q = DBModelFactory.createQueue(3);
        _qdao.createQueue(q);
        Message m = DBModelFactory.createMessage(23, 2, 3, "Hello Database!");
        _mdao.enqueueMessage(m);
    }

    @Test
    public void testDequeueTopmostMessage() throws Exception {
        /*PreparedStatement ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,3)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,3,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }*/
        Queue q = DBModelFactory.createQueue(1);
        _qdao.createQueue(q);
        Client c1 = DBModelFactory.createClient(1);
        _cdao.createClient(c1);
        Client c2 = DBModelFactory.createClient(2);
        _cdao.createClient(c2);
        Message m1 = DBModelFactory.createMessage(1, 0, 1, "Hello1");
        Message m2 = DBModelFactory.createMessage(2, 0, 1, "Hello2");
        _mdao.enqueueMessage(m1);
        _mdao.enqueueMessage(m2);
        Message m = _mdao.dequeueMessage(3, 0, 1, false);
        assertEquals("Dequeue topmost message does not work. Sender not correct.", m1.getSender(), m.getSender());
        assertEquals("Dequeue topmost message does not work. Receiver not correct.", m1.getReceiver(), m.getReceiver());
        assertEquals("Dequeue topmost message does not work. Queue not correct.", m1.getQueue(), m.getQueue());
        assertEquals("Dequeue topmost message does not work. Arrival time not correct.", m1.getArrivalTime(), m.getArrivalTime());
        assertEquals("Dequeue topmost message does not work. Message not correct.", m1.getMessage(), m.getMessage());
    }

    /*@Test(expected=MessageDequeueQueueDoesNotExistException.class)
    public void testDequeueMessage_MessageDequeueQueueDoesNotExistException() throws Exception {

    }

    @Test(expected=MessageDequeueEmptyQueueException.class)
    public void testDequeueMessage_MessageDequeueEmptyQueueException() throws Exception {

    }

    @Test(expected=MessageDequeueNotIntendedReceiverException.class)
    public void testDequeueMessage_MessageDequeueNotIntendedReceiverException() throws Exception {

    }*/
}
