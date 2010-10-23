-- Function: tamt_updateassignstatus(text, integer, integer, integer, boolean)

-- DROP FUNCTION tamt_updateassignstatus(text, integer, integer, integer, boolean);

CREATE OR REPLACE FUNCTION tamt_updateassignstatus(_gpstraceid text, _total integer, _processed integer, _matched integer, _completed boolean)
  RETURNS integer AS
$BODY$

import os, sys
import urllib2

# configure logging
#import logging
#import datetime
#LOG_FILENAME = '/tmp/plpython.log'
#logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG)

# http://127.0.0.1:8888/tamt/assignStatusHandler?g=9601f469-d003-46ae-b003-ff71f139aaf0&m=99&p=8
# TODO: this URL / PORT will have to change for production
webhook = "http://127.0.0.1:8888/tamt/assignStatusHandler?"

# for production
# webhook = "http://localhost:8080/assignStatusHandler?"
webhook += "g=" + _gpstraceid
webhook += "&p=" + str(_processed)
webhook += "&m=" + str(_matched)
webhook += "&t=" + str(_total)
webhook += "&c=" + str(_completed)
#logging.debug('send webhook to:' + webhook)

try:
	req = urllib2.Request(url=webhook)
	f = urllib2.urlopen(req)
#	logging.debug('done, returning')
except:
	message = "Unexpected error: ", sys.exc_info()
#	logging.debug(message)

return 1;

$BODY$
  LANGUAGE 'plpythonu' VOLATILE
  COST 100;
ALTER FUNCTION tamt_updateassignstatus(text, integer, integer, integer, boolean) OWNER TO postgres;
