package com.company.database_model;

import java.sql.Timestamp;

/**
 * Created by Jan Marti on 03.10.2014.
 * This class represents a queue from the viewpoint of a system element that uses the database interface
 */
public class Queue {

    private int _id;
    private Timestamp _creationTime;

    public Queue(int id, Timestamp creationTime) {
        _id = id;
        _creationTime = creationTime;
    }

    public int getId() { return _id; }
    public Timestamp getCreationTime() { return _creationTime; }

}
