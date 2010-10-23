CREATE OR REPLACE FUNCTION TAMT_updateSpeedDistributionRowsFromClosestDistribution(_tagId text, _dayType text, _hourBin integer)
  RETURNS void AS
$BODY$
DECLARE
	sumSeconds double precision;
	sumMeters double precision;
BEGIN

	-- RAISE NOTICE 'START TAMT_updateSpeedDistributionPercentageValues';
	
        -- get sum seconds
	SELECT * INTO sumSeconds FROM TAMT_getSumSecondsInHourBin(_tagId, _dayType, _hourBin);
	-- RAISE NOTICE 'OUTER sumSeconds %', sumSeconds;
    
	-- get sum meters
	SELECT * INTO sumMeters FROM TAMT_getSumMetersInHourBin(_tagId, _dayType, _hourBin);
	-- RAISE NOTICE 'OUTER sumMeters %', sumMeters;

	-- if sumSeconds or sumMeters = 0, we cannot divide by them, so RETURN
	IF sumSeconds IS NULL OR sumSeconds = 0 OR sumMeters = 0 OR sumMeters IS NULL
	THEN
		-- RAISE NOTICE 'skipping...';
		UPDATE speeddistribution
			SET
			percentsecondsinbin = 0,
			percentmetersinbin = 0
			WHERE tagid = _tagId
			AND daytype = _dayType 
			AND hourbin = _hourBin;
		RETURN;
	ELSE
		
        -- update the percentseconds and percentmeters
        UPDATE speeddistribution
		SET
        	percentsecondsinbin = secondsinbin / sumSeconds,
        	percentmetersinbin = metersinbin / sumMeters
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;

	END IF;
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getSumMetersInHourBin(_tagId text, _dayType text, _hourBin integer) OWNER TO gis;