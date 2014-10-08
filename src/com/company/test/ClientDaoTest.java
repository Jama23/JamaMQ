package com.company.test;

import com.company.database_interface.DBConfiguration;
import com.company.database_interface.ClientDao;
import com.company.database_interface.PGConnectionPool;
import com.company.database_model.DBModelFactory;
import com.company.exception.ClientDoesNotExistException;
import com.company.exception.ClientAlreadyExistsException;
import com.company.database_model.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientDaoTest {
    Connection _connection;
    ClientDao _dao;

    @Before
    public void setUp() throws Exception {
        DBConfiguration.initDBConfig("var/config.prop");
        _connection = PGConnectionPool.getInstance().getConnection();
        _dao = new ClientDao(_connection);
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE client");
        ps.execute();
    }

    @After
    public void tearDown() throws Exception {
        PreparedStatement ps = _connection.prepareStatement("TRUNCATE TABLE client");
        ps.execute();
        _connection.close();
    }

    @Test
    public void testCreateClient() throws Exception {
        Client c = DBModelFactory.createClient(11);
        _dao.createClient(c);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM client WHERE id = ?");
        ps.setInt(1, 11);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Timestamp ts = rs.getTimestamp(2);
            assertEquals("Create client does not work.", ts, c.getCreationTime());
        } else {
            assert(false);
        }
    }

    @Test(expected=ClientAlreadyExistsException.class)
    public void testCreateClient_ClientAlreadyExistsException() throws Exception {
        Client c1 = DBModelFactory.createClient(20);
        Client c2 = DBModelFactory.createClient(20);
        _dao.createClient(c1);
        _dao.createClient(c2);
    }

    @Test
    public void testDeleteClient() throws Exception {
        Client c = DBModelFactory.createClient(30);
        _dao.createClient(c);
        _dao.deleteClient(30);
        PreparedStatement ps = _connection.prepareStatement("SELECT * FROM client WHERE Id = ?");
        ps.setInt(1, 30);
        ResultSet rs = ps.executeQuery();
        assertTrue("Client could not be deleted.", !rs.next());
    }

    @Test(expected=ClientDoesNotExistException.class)
    public void testDeleteClient_ClientDoesNotExistException() throws Exception {
        Client c = DBModelFactory.createClient(40);
        _dao.createClient(c);
        _dao.deleteClient(40);
        _dao.deleteClient(40);
    }
}
