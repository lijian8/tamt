-- Function: tamt_insertassignstatus(text)

-- DROP FUNCTION tamt_insertassignstatus(text);

CREATE OR REPLACE FUNCTION tamt_insertassignstatus(_gpstraceid text)
  RETURNS integer AS
$BODY$

# setup logging
import os, sys, logging
import datetime
LOG_FILENAME = '/tmp/plpython.log'
logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG)

# get record count for this id
sql = "SELECT recordcount FROM gpstraces WHERE id = '"+_gpstraceid+"'"
recordCount = 0
logging.debug(sql)
rv = plpy.execute(sql)
for r in rv:
	recordCount = r["recordcount"]

logging.debug(recordCount)

# prepare the update statement
insertSQL = "INSERT INTO assignstatus VALUES ("
insertSQL += "(SELECT nextval('assignstatus_id_seq')), "
insertSQL += "'"+_gpstraceid+"', "
insertSQL += str(recordCount)+", "
insertSQL += str(0)+", "
insertSQL += str(0)+", "
insertSQL += "'"+str(datetime.datetime.now())+"', "
insertSQL += str(False)+" )";
logging.debug(insertSQL)
rv = plpy.execute(insertSQL)

return 1;

$BODY$
  LANGUAGE 'plpythonu' VOLATILE
  COST 100;
ALTER FUNCTION tamt_insertassignstatus(text) OWNER TO postgres;
