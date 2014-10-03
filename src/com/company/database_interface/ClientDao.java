package com.company.database_interface;

import com.company.database_model.Client;

import java.sql.Connection;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class ClientDao {

    private Connection _connecton = null;

    public ClientDao(Connection connection) {
        _connecton = connection;
    }

}
