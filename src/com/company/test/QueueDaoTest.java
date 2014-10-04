package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.QueueDao;
import com.company.database_interface.PGConnectionPool;
import com.company.database_model.Client;
import com.company.exception.QueueDoesNotExistException;
import com.company.exception.QueueAlreadyExistsException;
import com.company.exception.QueueGetException;
import com.company.exception.QueueGetWaitingException;
import com.company.database_model.Queue;
import com.company.database_model.ModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueueDaoTest {
    Connection connection_;
    QueueDao dao_;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        connection_ = PGConnectionPool.getInstance().getConnection();
        dao_ = new QueueDao(connection_);
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE message, queue");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE message, queue");
        ps.execute();
        connection_.close();
    }

    @Test
    public void testCreateQueue() throws Exception {
        Queue q = ModelFactory.createQueue(11);
        dao_.createQueue(q);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM queue WHERE id = ?");
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
        Queue q1 = ModelFactory.createQueue(20);
        Queue q2 = ModelFactory.createQueue(20);
        dao_.createQueue(q1);
        dao_.createQueue(q2);
    }

    @Test
    public void testDeleteQueue() throws Exception {
        Queue c = ModelFactory.createQueue(30);
        dao_.createQueue(c);
        dao_.deleteQueue(30);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM client WHERE Id = ?");
        ps.setInt(1, 30);
        ResultSet rs = ps.executeQuery();
        assertTrue("Queue could not be deleted.", !rs.next());
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testDeleteQueue_QueueDoesNotExistException() throws Exception {
        Queue c = ModelFactory.createQueue(40);
        dao_.createQueue(c);
        dao_.deleteQueue(40);
        dao_.deleteQueue(40);
    }

    @Test
    public void testGetQueue() throws Exception {
        Queue q_in = ModelFactory.createQueue(23);
        dao_.createQueue(q_in);
        Queue q_out = dao_.getQueue(23);
        assertTrue("Retrieved queues id not equal.", q_in.getId() == q_out.getId());
        assertTrue("Retrieved queues creation time not equal.", q_in.getCreationTime().equals(q_out.getCreationTime()));
    }

    @Test(expected=QueueDoesNotExistException.class)
    public void testGetQueue_QueueDoesNotExistException() throws Exception {
        Queue q_out = dao_.getQueue(23);
    }

    @Test
    public void testGetWaitingQueues() throws Exception {
        PreparedStatement ps = connection_.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,3)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = connection_.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,2,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }
        ps = connection_.prepareStatement("INSERT INTO message (sender, receiver, queue) VALUES (1,3,4)");
        for (int i = 0; i < 4; i++) { ps.execute(); }

        List<Integer> res = dao_.getWaitingQueues(2);
        assertTrue("Retrieved queue not waiting.", res.get(0) == 3);
        assertTrue("Retrieved queue not waiting.", res.get(1) == 4);
    }
}
