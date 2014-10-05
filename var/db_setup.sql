---------------------------------------------------------------------------------------
-- Postgresql Database Setup -- JamaMQ -- October 2014 _-------------------------------
---------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------



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
    id serial PRIMARY KEY,
    sender int,
    receiver int,
    queue int,
    arrivaltime timestamp,
    message text
);

CREATE TABLE Queue
(
    id int PRIMARY KEY,
    creationTime timestamp
); 

CREATE TABLE Client
(
    id int PRIMARY KEY,
    creationTime timestamp
);



---------------------------------------------------------------------------------------
-- Stored Procedures
---------------------------------------------------------------------------------------

-- Message Table ----------------------------------------------------------------------

-- Enqueue Message
CREATE OR REPLACE FUNCTION enqueueMessage(sender_ integer, receiver_ integer, queue_ integer, arrivaltime_ timestamp, message_ varchar)
	RETURNS integer AS
$BODY$
declare
     identifier integer;
begin
    IF EXISTS(SELECT id FROM queue WHERE Id = $3) THEN
        IF EXISTS(SELECT id FROM client WHERE Id = $1) THEN
	        INSERT INTO message("sender", "receiver", "queue", "arrivaltime", "message") VALUES($1, $2, $3, $4, $5) RETURNING id into identifier;
            return identifier;
        ELSE
            RAISE 'No client with id % found.', $1 USING ERRCODE = 'V2006';
        END IF;
	ELSE
	    RAISE 'No queue with id % found.', $3 USING ERRCODE = 'V2005';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Dequeue / Peek Message
CREATE OR REPLACE FUNCTION dequeueMessage(reqclient_ integer, partsender_ integer, queue_ integer, peek boolean)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	IF ($3 != 0) THEN
		-- Dequeue message from a particular queue
		IF EXISTS(SELECT id FROM queue WHERE Id = $3) THEN
			IF ($2 != 0) THEN
				-- Dequeue message from queue with id 'queue_' with sender id set to 'partsender_'
				IF EXISTS(SELECT id FROM message WHERE sender = $2 AND (receiver = 0 OR receiver = $1) AND queue = $3) THEN
					SELECT * INTO result_record FROM message WHERE sender = $2 AND (receiver = 0 OR receiver = $1) AND queue = $3 ORDER BY arrivaltime ASC LIMIT 1;
					IF (!peek) THEN
						DELETE FROM message WHERE id = result_record.id;
					END IF;
					return result_record;
				ELSE
					RAISE 'No message in queue with id % and sender with id % for client with id % found.', $3, $2, $1 USING ERRCODE = 'V2008';
				END IF;
			ELSE
				-- Dequeue oldest (topmost) message from queue with id 'queue_'
				IF EXISTS(SELECT id FROM message WHERE queue = $3) THEN
					SELECT * INTO result_record FROM message WHERE queue = $3 ORDER BY arrivaltime ASC LIMIT 1;
					IF (result_record.receiver = 0 OR result_record.receiver = $1) THEN
						IF (!peek) THEN
							DELETE FROM message WHERE id = result_record.id;
						END IF;
						return result_record;
					ELSE
						RAISE 'Topmost message of queue with id % is not intended for client with id %.', $3, $1 USING ERRCODE = 'V2010';
					END IF;
				ELSE
					RAISE 'No message in queue with id %.', $3 USING ERRCODE = 'V2009';
				END IF;
			END IF;
		ELSE
            RAISE 'No queue with id % found.', $3 USING ERRCODE = 'V2007';
        END IF;
	ELSE
		-- Dequeue message from any queue with sender id set to 'partsender_'
		IF EXISTS(SELECT id FROM message WHERE sender = $2 AND (receiver = 0 OR receiver = $1)) THEN
			SELECT * INTO result_record FROM message WHERE sender = $2 AND (receiver = 0 OR receiver = $1) ORDER BY arrivaltime ASC LIMIT 1;
			IF (!peek) THEN
				DELETE FROM message WHERE id = result_record.id;
			END IF;
			return result_record;
		ELSE
			RAISE 'No message with sender id % for client with id % found.', $2, $1 USING ERRCODE = 'V2011';
		END IF;
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Queue Table ------------------------------------------------------------------------

-- Create Queue
CREATE OR REPLACE FUNCTION createQueue(id_ integer, creationtime_ timestamp)
	RETURNS void AS
$BODY$
declare
begin
	IF NOT EXISTS(SELECT id FROM queue WHERE id = $1) THEN
		INSERT INTO queue("id", "creationtime") VALUES($1, $2);
	ELSE
		RAISE 'Queue with id % already exists.', $1 USING ERRCODE = 'V2003';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Delete Queue
CREATE OR REPLACE FUNCTION deleteQueue(id_ integer)
	RETURNS void AS
$BODY$
declare
begin
    IF EXISTS(SELECT id FROM queue WHERE id = $1) THEN
	    DELETE FROM queue WHERE Id = $1;
	ELSE
	    RAISE 'Queue with id % does not exist.', $1 USING ERRCODE = 'V2004';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Get Queue
CREATE OR REPLACE FUNCTION getQueue(id_ integer)
	RETURNS queue AS
$BODY$
declare
    result_record queue;
begin
    IF EXISTS(SELECT * FROM queue WHERE id = $1) THEN
	    SELECT * FROM queue INTO result_record WHERE id = $1;
	    return result_record;
	ELSE
	    RAISE 'Queue with id % does not exist.', $1 USING ERRCODE = 'V2004';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Get Waiting Queues
CREATE OR REPLACE FUNCTION getWaitingQueues(recid_ integer)
	RETURNS TABLE(queueid integer) AS
$BODY$
declare
begin
    Return QUERY
	SELECT DISTINCT queue FROM message WHERE receiver = $1;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Client Table -----------------------------------------------------------------------

-- Create Client
CREATE OR REPLACE FUNCTION createClient(id_ integer, creationtime_ timestamp)
	RETURNS void AS
$BODY$
declare
begin
	IF NOT EXISTS(SELECT id FROM client WHERE id = $1) THEN
		INSERT INTO client("id", "creationtime") VALUES($1, $2);
	ELSE
		RAISE 'Client with id % already exists.', $1 USING ERRCODE = 'V2001';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Delete Client
CREATE OR REPLACE FUNCTION deleteClient(id_ integer)
	RETURNS void AS
$BODY$
declare
begin
    IF EXISTS(SELECT id FROM client WHERE id = $1) THEN
	    DELETE FROM client WHERE id = $1;
	ELSE
	    RAISE 'Client with id % does not exist.', $1 USING ERRCODE = 'V2002';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;



---------------------------------------------------------------------------------------
-- Indices
---------------------------------------------------------------------------------------

-- Message Table ----------------------------------------------------------------------
  
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
  
-- Queue Table ------------------------------------------------------------------------

CREATE INDEX "queue-id-index"
  ON queue
  USING btree
  (id);
  
-- Client Table -----------------------------------------------------------------------

CREATE INDEX "client-id-index"
  ON client
  USING btree
  (id);