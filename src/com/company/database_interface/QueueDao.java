package com.company.database_interface;

import java.sql.Connection;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class QueueDao {

    private Connection _connecton = null;

    public QueueDao(Connection connection) {
        _connecton = connection;
    }

}
