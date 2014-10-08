package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.QueueDao;
import com.company.database_interface.PGConnectionPool;
import com.company.database_model.DBModelFactory;
import com.company.exception.QueueDoesNotExistException;
import com.company.exception.QueueAlreadyExistsException;
import com.company.database_model.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueueDaoTest {
    Connection _connection;
    QueueDao _dao;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        _connection = PGConnectionPool.getInstance().getConnection();
        _dao = new QueueDao(_connection);
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE message, queue");
        ps.execute();
        _connection.close();
    }

    @Test
    public void testCreateQueue() throws Exception {
        Queue q = DBModelFactory.createQueue(11);
        _dao.createQueue(q);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM queue WHERE id = ?");
        ps.setInt(1, 11);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Timestamp ts = rs.getTimestamp(2);
            assertEquals("Create queue does not work.", ts, q.getCreationTime());
        } else {
            assert(false);
        }
    }

    @Test(expected=QueueAlreadyExistsException.class)
    public void testCreateQueue_QueueAlreadyExistsException() throws Exception {
        Queue q1 = DBModelFactory.createQueue(20);
        Queue q2 = DBModelFactory.createQueue(20);
        _dao.createQueue(q1);
        _dao.createQueue(q2);
    }

    @Test
    public void testDeleteQueue() throws Exception {
        Queue c = DBModelFactory.createQueue(30);
        _dao.createQueue(c);
        _dao.deleteQueue(30);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM client WHERE Id = ?");
        ps.setInt(1, 30);
        ResultSet rs = ps.executeQuery();
        assertTrue("Queue could not be deleted.", !rs.next());
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testDeleteQueue_QueueDoesNotExistException() throws Exception {
        Queue c = DBModelFactory.createQueue(40);
        _dao.createQueue(c);
        _dao.deleteQueue(40);
        _dao.deleteQueue(40);
    }

    @Test
    public void testGetQueue() throws Exception {
        Queue q_in = DBModelFactory.createQueue(23);
        _dao.createQueue(q_in);
        Queue q_out = _dao.getQueue(23);
        assertTrue("Retrieved queues id not equal.", q_in.getId() == q_out.getId());
        assertTrue("Retrieved queues creation time not equal.", q_in.getCreationTime().equals(q_out.getCreationTime()));
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testGetQueue_QueueDoesNotExistException() throws Exception {
        Queue q_out = _dao.getQueue(23);
    }

    @Test
    public void testGetWaitingQueues() throws Exception {
        PreparedStatement ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,3)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = _connection.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,3,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }

        List<Integer> res = _dao.getWaitingQueues(2);
        assertTrue("Retrieved queue not waiting.", res.get(0) == 3);
        assertTrue("Retrieved queue not waiting.", res.get(1) == 4);
    }
}
