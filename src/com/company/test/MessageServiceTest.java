package com.company.test;

import com.company.client.MessageFactory;
import com.company.client.MessageService;
import com.company.client_backend.Queue;
import com.company.database_interface.PGConnectionPool;
import com.company.messaging.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageServiceTest {
    Connection _connection;
    MessageService _messageService;

    @Before
    public void setUp() throws Exception {
        Configuration.initConfig("var/config.prop");
        _connection = PGConnectionPool.getInstance().getConnection();
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue, client");
        ps.execute();

        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,3)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,3,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,3,5)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
    }

    @After
    public void tearDown() throws Exception {
        Configuration.initConfig("var/config.prop");
        _connection = PGConnectionPool.getInstance().getConnection();
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue, client");
        ps.execute();
    }

    @Test
    public void testGetWaitingQueueIds() throws Exception {
        _messageService = new MessageService("localhost", 5555);
        _messageService.register(2);
        ArrayList<Integer> res = _messageService.getWaitingQueueIds();
        _messageService.deregister();

        assertTrue("Retrieved queues not waiting.", (res.get(0) == 3 && res.get(1) == 4) || (res.get(0) == 4 && res.get(1) == 3));
    }

    @Test
    public void testDeleteQueue() throws Exception {
        _messageService = new MessageService("localhost", 5555);
        _messageService.register(1);
        Queue q = _messageService.createQueue(1);
        q.enqueueMessage(MessageFactory.createMessage("Hallo1"));
        q.enqueueMessage(MessageFactory.createMessage("Hallo2"));
        q.enqueueMessage(MessageFactory.createMessage("Hallo3"));
        _messageService.deleteQueue(1);
        _messageService.deregister();

        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM message WHERE queue = ?");
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();
        assertTrue("Queue could not be deleted.", !rs.next());
    }

}
