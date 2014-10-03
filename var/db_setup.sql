---------------------------------------------------------------------------------------
-- Clean Database
---------------------------------------------------------------------------------------
DROP DATABASE jamamq;

---------------------------------------------------------------------------------------
-- Initialize Database
---------------------------------------------------------------------------------------
CREATE DATABASE jamamq
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'German_Switzerland.1252'
       LC_CTYPE = 'German_Switzerland.1252'
       CONNECTION LIMIT = -1;
	   
CREATE TABLE Message
(
    Id serial PRIMARY KEY,
    Sender int,
    Receiver int,
    Queue int,
    ArrivalTime timestamp,
    Message text
);

CREATE TABLE Queue
(
    Id int PRIMARY KEY,
    CreationTime timestamp
); 

CREATE TABLE Client
(
    Id int PRIMARY KEY,
    CreationTime timestamp
);

---------------------------------------------------------------------------------------
-- Stored Procedures
---------------------------------------------------------------------------------------






---------------------------------------------------------------------------------------
-- Indices
---------------------------------------------------------------------------------------

-- Message Table
  
CREATE INDEX "sender-index"
  ON message
  USING btree
  (sender);

CREATE INDEX "receiver-index"
  ON message
  USING btree
  (receiver);
  
CREATE INDEX "queue-index"
  ON message
  USING btree
  (queue);
  
CREATE INDEX "arrivaltime-index"
  ON message
  USING btree
  (arrivaltime);
  
-- Queue Table

CREATE INDEX "queue-id-index"
  ON queue
  USING btree
  (id);
  
-- Client Table

CREATE INDEX "client-id-index"
  ON client
  USING btree
  (id);