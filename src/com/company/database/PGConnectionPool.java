package com.company.database;

import org.postgresql.ds.PGPoolingDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 27.09.2014.
 */
public class PGConnectionPool {

    private static Logger LOGGER_ = Logger.getLogger(PGConnectionPool.class.getCanonicalName());
    private static PGConnectionPool INSTANCE_ = null;

    private PGPoolingDataSource source_;

    public static PGConnectionPool getInstance() {
        if(INSTANCE_ == null) {
            INSTANCE_ = new PGConnectionPool();
        }
        return INSTANCE_;
    }

    private PGConnectionPool() {
        source_ = new PGPoolingDataSource();
        source_.setServerName(DBConfiguration.getProperty("db.server.name"));
        source_.setDatabaseName(DBConfiguration.getProperty("db.database.name"));
        source_.setUser(DBConfiguration.getProperty("db.user"));
        source_.setPassword(DBConfiguration.getProperty("db.password"));
        source_.setMaxConnections(Integer.parseInt(DBConfiguration.getProperty("db.pool.max")));
        source_.setInitialConnections(Integer.parseInt(DBConfiguration.getProperty("db.pool.initial")));
    }

    public synchronized Connection getConnection() {
        Connection con = null;
        try {
            con = source_.getConnection();
        } catch (SQLException e) {
            LOGGER_.log(Level.SEVERE, "Could not retrieve connection from the connection pool.");
            throw new RuntimeException(e);
        }
        return con;
    }

}
