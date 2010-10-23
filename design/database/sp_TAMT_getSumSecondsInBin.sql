CREATE OR REPLACE FUNCTION TAMT_calculatePercentSecondsPercentMetersInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer)
  RETURNS void AS
$BODY$
DECLARE
	sumSeconds double precision;
	percentSeconds double precision;

	sumMeters double precision;
	percentMeters double precision;
	
BEGIN
	RAISE NOTICE 'starting TAMT_calculatePercentSecondsPercentMetersInBin...';


	-- get sum seconds
        SELECT INTO 
		sumSeconds SUM(secondsinbin)
	FROM speeddistribution
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;

        SELECT INTO 
		sumMeters SUM(metersinbin)
	FROM speeddistribution
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;
		
	RAISE NOTICE 'sumSeconds %', sumSeconds;
	RAISE NOTICE 'sumMeters %', sumMeters;

	UPDATE speeddistribution SET 
		percentseconds = secondsinbin / sumSeconds,
		percentseconds = secondsinbin / sumSeconds
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin
	AND speedbin = _hourBin;
	
	
	
        
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_calculatePercentSecondsPercentMetersInBin(_tagId text, _dayType text, _hourBin integer, _speedBin integer) OWNER TO gis;