package com.company.database_interface;

import com.company.database_model.Queue;

import java.sql.Connection;

/**
 * Created by Jan Marti on 03.10.2014.
 */
public class QueueDao {

    private Connection _connecton = null;

    public QueueDao(Connection connection) {
        _connecton = connection;
    }

    public void createQueue(Queue queue) {

    }

    public void deleteQueue(int id) {

    }

    public int[] getWaitingQueues(int reveiverId) {
        return new int[1];
    }

}
