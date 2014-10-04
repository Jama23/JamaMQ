package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.MessageDao;
import com.company.database_interface.PGConnectionPool;
import com.company.exception.MessageEnqueueException;
import com.company.exception.MessageQueueDoesNotExistException;
import com.company.exception.MessageSenderDoesNotExistException;
import com.company.database_model. Message;
import com.company.database_model.ModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageDaoTest {
    Connection connection_;
    MessageDao dao_;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        connection_ = PGConnectionPool.getInstance().getConnection();
        dao_ = new MessageDao(connection_);
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE message");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE message");
        ps.execute();
        connection_.close();
    }

    @Test
    public void testEnqueueMessage() throws Exception {

    }

    /*@Test(expected=MessageEnqueueException.class)
    public void testEnqueueMessage_MessageEnqueueException() throws Exception {

    }*/
}
