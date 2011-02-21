-- Function: tamt_updatespeeddistributionpercentagevalues(text, text, integer)

-- DROP FUNCTION tamt_updatespeeddistributionpercentagevalues(text, text, integer);

CREATE OR REPLACE FUNCTION tamt_updatespeeddistributionpercentagevalues(_tagid text, _daytype text, _hourbin integer)
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
        	-- Changed based on conversation with John on 2011-02-17 from
        	-- percentmetersinbin = metersinbin / sumMeters
        	-- to:
        	percentmetersinbin = (secondsinbin / sumSeconds) * avgmeterspersecond
	WHERE tagid = _tagId
	AND daytype = _dayType 
	AND hourbin = _hourBin;

	END IF;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_updatespeeddistributionpercentagevalues(text, text, integer) OWNER TO postgres;