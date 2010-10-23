CREATE OR REPLACE FUNCTION TAMT_getSumSecondsInHourBin(_tagId text, _dayType text, _hourBin integer)
  RETURNS double precision AS
$BODY$
DECLARE
	sumSeconds double precision;
BEGIN

-- 	RAISE NOTICE 'START TAMT_getSumSecondsInHourBin';
-- 	RAISE NOTICE '_tagId %', _tagId;
-- 	RAISE NOTICE '_dayType %', _dayType;
-- 	RAISE NOTICE '_hourBin %', _hourBin;

	-- get sum seconds
        SELECT INTO 
		sumSeconds SUM(secondsinbin)
	FROM speeddistribution
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;

        -- RAISE NOTICE 'FINISH TAMT_getSumSecondsInHourBin sumSeconds %', sumSeconds;
	RETURN sumSeconds;
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getSumSecondsInHourBin(_tagId text, _dayType text, _hourBin integer) OWNER TO gis;