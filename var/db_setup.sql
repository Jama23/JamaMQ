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


-- Queue Table ------------------------------------------------------------------------


-- Client Table -----------------------------------------------------------------------

-- Create Client
CREATE OR REPLACE FUNCTION createClient(id integer, creationtime timestamp)
	RETURNS void AS
$BODY$
declare
begin
	IF NOT EXISTS(SELECT id FROM client WHERE id = $1) THEN
		INSERT INTO client("id", "creationtime") VALUES($1, $2);
	ELSE
		RAISE 'Client with id % already exists.', $1 USING ERRCODE = 'V2001'
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-- Delete Client
CREATE OR REPLACE FUNCTION deleteClient(identifier integer)
	RETURNS void AS
$BODY$
declare
--    ident integer;
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