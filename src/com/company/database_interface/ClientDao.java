package com.company.database_interface;

import com.company.database_model.Client;
import com.company.exception.ClientAlreadyExistsException;
import com.company.exception.ClientCreateException;
import com.company.exception.ClientDeleteException;
import com.company.exception.ClientDoesNotExistException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class ClientDao {

    private Connection _connection = null;

    public ClientDao(Connection connection) {
        _connection = connection;
    }

    public void createClient(Client client) throws ClientCreateException, ClientAlreadyExistsException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call createClient(?,?) }");
            callStat.setInt(1, client.getId());
            callStat.setTimestamp(2, client.getCreationTime());
            callStat.execute();
            callStat.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2001")) { // Custom: Client already exists
                throw new ClientAlreadyExistsException(e);
            }
            throw new ClientCreateException(e);
        }
    }

    public void deleteClient(int id) throws ClientDeleteException, ClientDoesNotExistException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call deleteClient(?) }");
            callStat.setInt(1, id);
            callStat.execute();
            callStat.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2002")) { // Custom: Client does not exist
                throw new ClientDoesNotExistException(e);
            }
            throw new ClientDeleteException(e);
        }
    }
}
