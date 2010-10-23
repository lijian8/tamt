CREATE OR REPLACE FUNCTION TAMT_getAverageSpeedInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer)
  RETURNS double precision AS
$BODY$
DECLARE
	averageSpeed double precision;
BEGIN
	averageSpeed = 0.0;

	RAISE NOTICE 'starting getAverageSpeedInBin...';
    
	SELECT INTO averageSpeed 
		AVG(kph) 
	FROM gpspoints 
	WHERE tag_id = _tagId
	AND daytype = _dayType 
	AND "hour" = _hourBin::text
	AND speedbinnumber = _speedBin;

	RAISE NOTICE 'averageSpeed %', averageSpeed;
    
	RETURN averageSpeed;
	
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getTimeInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer) OWNER TO gis;