CREATE OR REPLACE FUNCTION TAMT_getTimeInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer)
  RETURNS double precision AS
$BODY$
DECLARE
	timeInBin double precision;
BEGIN
	timeInBin = 0.0;

	SELECT INTO timeInBin 
		count(*)
	FROM gpspoints 
	WHERE tag_id = _tagId
	AND daytype = _dayType 
	AND "hour" = _hourBin::text
	AND speedbinnumber = _speedBin;
	
	RETURN timeInBin;
END;	
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getTimeInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer) OWNER TO gis;