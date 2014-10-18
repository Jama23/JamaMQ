package com.company.database_interface;

import com.company.database_model.DBModelFactory;
import com.company.database_model.Queue;
import com.company.exception.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class QueueDao {

    private Connection _connection = null;

    public QueueDao(Connection connection) {
        _connection = connection;
    }

    public void createQueue(Queue queue) throws QueueCreateException, QueueAlreadyExistsException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call createQueue(?,?) }");
            callStat.setInt(1, queue.getId());
            callStat.setTimestamp(2, queue.getCreationTime());
            callStat.execute();
            callStat.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2003")) {
                throw new QueueAlreadyExistsException(e);
            }
            throw new QueueCreateException(e);
        }
    }

    public void deleteQueue(int id) throws QueueDeleteException, QueueDoesNotExistException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call deleteQueue(?) }");
            callStat.setInt(1, id);
            callStat.execute();
            callStat.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2004")) {
                throw new QueueDoesNotExistException(e);
            }
            throw new QueueDeleteException(e);
        }
    }

    public Queue getQueue(int id) throws QueueGetException, QueueDoesNotExistException {
        try {
            CallableStatement callStat = _connection.prepareCall("{ call getQueue(?) }");
            callStat.setInt(1, id);
            ResultSet resSet = callStat.executeQuery();
            resSet.next(); // Existence of record is already checked inside stored procedure on database
            Queue queue = DBModelFactory.createQueue(resSet.getInt(1), resSet.getTimestamp(2));
            resSet.close();
            callStat.close();
            return queue;
        } catch (SQLException e) {
            if (e.getSQLState().equals("V2004")) {
                throw new QueueDoesNotExistException(e);
            }
            throw new QueueGetException(e);
        }
    }

    public ArrayList<Integer> getWaitingQueues(int receiverId) throws QueueGetWaitingException {
        ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            CallableStatement callStat = _connection.prepareCall("{ call getWaitingQueues(?) }");
            callStat.setInt(1, receiverId);
            ResultSet resSet = callStat.executeQuery();
            while (resSet.next()) {
                result.add(resSet.getInt(1));
            }
            resSet.close();
            callStat.close();
            return result;
        } catch (SQLException e) {
            throw new QueueGetWaitingException(e);
        }
    }

}
