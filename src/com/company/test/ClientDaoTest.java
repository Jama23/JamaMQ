package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.ClientDao;
import com.company.database_interface.PGConnectionPool;
import com.company.exception.ClientDoesNotExistException;
import com.company.exception.ClientAlreadyExistsException;
import com.company.database_model.Client;
import com.company.database_model.ModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: nano
 * Date: 07/11/13
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class ClientDaoTest {
    Connection connection_;
    ClientDao dao_;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        connection_ = PGConnectionPool.getInstance().getConnection();
        dao_ = new ClientDao(connection_);
        PreparedStatement ps = connection_.prepareStatement("TRUNCATE TABLE client");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        connection_.close();
    }

    @Test
    public void testCreateClient() throws Exception {
        Client c = ModelFactory.createClient(10);
        dao_.createClient(c);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM client WHERE id = ?");
        ps.setInt(1, 10);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Timestamp ts = rs.getTimestamp(2);
            assertEquals("Create client does not work.", ts, c.getCreationTime());
        } else {
            assert(false);
        }
    }

    @Test(expected=ClientAlreadyExistsException.class)
    public void testCreateClient_ClientExistsException() throws Exception {
        Client c1 = ModelFactory.createClient(20);
        Client c2 = ModelFactory.createClient(20);
        dao_.createClient(c1);
        dao_.createClient(c2);
    }

    @Test
    public void testDeleteClient() throws Exception {
        Client c = ModelFactory.createClient(30);
        dao_.createClient(c);
        dao_.deleteClient(30);
        PreparedStatement ps = connection_.prepareStatement("SELECT * FROM client WHERE Id = ?");
        ps.setInt(1, 30);
        ResultSet rs = ps.executeQuery();
        assertTrue("Client could not be deleted.", !rs.next());
    }

    @Test(expected=ClientDoesNotExistException.class)
    public void testDeleteClient_ClientDoesNotExistException() throws Exception {
        Client c = ModelFactory.createClient(40);
        dao_.createClient(c);
        dao_.deleteClient(40);
        dao_.deleteClient(40);
    }
}
