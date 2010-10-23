CREATE OR REPLACE FUNCTION TAMT_getSumMetersInHourBin(_tagId text, _dayType text, _hourBin integer)
  RETURNS double precision AS
$BODY$
DECLARE
	sumMeters double precision;
BEGIN
-- 	RAISE NOTICE 'START TAMT_getSumMetersInHourBin';
-- 	RAISE NOTICE '_tagId %', _tagId;
-- 	RAISE NOTICE '_dayType %', _dayType;
-- 	RAISE NOTICE '_hourBin %', _hourBin;
	
	-- get sum meters
        SELECT INTO 
		sumMeters SUM(metersinbin)
	FROM speeddistribution
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;

--         RAISE NOTICE 'FINISH TAMT_getSumMetersInHourBin sumMeters %', sumMeters;
	RETURN sumMeters;
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getSumMetersInHourBin(_tagId text, _dayType text, _hourBin integer) OWNER TO gis;