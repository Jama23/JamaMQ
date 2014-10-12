package com.company.database_interface;

import com.company.messaging.Configuration;
import org.postgresql.ds.PGPoolingDataSource;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 27.09.2014.
 */
public class PGConnectionPool {

    private static Logger _LOGGER = Logger.getLogger(PGConnectionPool.class.getCanonicalName());

    private static PGConnectionPool _INSTANCE = null;
    private PGPoolingDataSource _source;

    public static PGConnectionPool getInstance() {
        if(_INSTANCE == null) {
            _INSTANCE = new PGConnectionPool();
        }
        return _INSTANCE;
    }

    private PGConnectionPool() {
        _source = new PGPoolingDataSource();
        _source.setServerName(Configuration.getProperty("db.server.name"));
        _source.setPortNumber(Integer.parseInt(Configuration.getProperty("db.server.port")));
        _source.setDatabaseName(Configuration.getProperty("db.database.name"));
        _source.setUser(Configuration.getProperty("db.user"));
        _source.setPassword(Configuration.getProperty("db.password"));
        _source.setMaxConnections(Integer.parseInt(Configuration.getProperty("db.pool.max")));
        _source.setInitialConnections(Integer.parseInt(Configuration.getProperty("db.pool.initial")));
    }

    public synchronized Connection getConnection() {
        Connection con = null;
        try {
            con = _source.getConnection();
        } catch (SQLException e) {
            _LOGGER.log(Level.SEVERE, "Could not retrieve connection from the connection pool.");
            throw new RuntimeException(e);
        }
        return con;
    }

}