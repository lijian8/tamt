CREATE OR REPLACE FUNCTION TAMT_insertSpeedDistributionRecord(_tagId text, _dayType text, _hourBin integer, _speedBin integer)
  RETURNS void AS
$BODY$
DECLARE

    averageKilometersPerHourInBin double precision;
    averageMetersPerSecondInBin double precision;
    secondsInBin double precision;
    metersInBin double precision;
    sql text;
        
BEGIN

    averageKilometersPerHourInBin := 0.0;
    averageMetersPerSecondInBin := 0.0;
    secondsInBin := 0.0;
    metersInBin := 0.0;
    
    -- RAISE NOTICE 'starting...';

    -- get time in bin (seconds) and average speed in bin (kph)
    SELECT * INTO averageKilometersPerHourInBin FROM TAMT_getAverageSpeedInBin(_tagId, _dayType, _hourBin, _speedBin);
    --  RAISE NOTICE 'averageKilometersPerHourInBin %', averageKilometersPerHourInBin;

    -- If speed is null (and it might be), we cannot continue
    IF averageKilometersPerHourInBin IS NULL
    THEN
	-- RAISE NOTICE 'skipping...';
	    -- add record to speed distribution table with zeros for secondsinbin, metersinbin and avgmeterspersecond
	    sql := 'INSERT INTO speeddistribution (tagid, daytype, hourbin, speedbin, secondsinbin, metersinbin, avgmeterspersecond) VALUES';
	    sql := sql || '('''|| _tagId ||''', ''' || _dayType || ''', ' || _hourBin || ', ' || _speedBin || ', ' 
		      || 0 || ', ' || 0 || ', ' || 0 ||')';
	    -- RAISE NOTICE 'sql = %', sql;
	    EXECUTE sql;	
	RETURN;
    ELSE

    SELECT * INTO secondsInBin FROM TAMT_getTimeInBin(_tagId, _dayType, _hourBin, _speedBin);
    -- RAISE NOTICE 'secondsInBin %', secondsInBin;
        
    -- get all the units in meters and seconds
    -- 1 kph = 0.277777778 meters / second
    averageMetersPerSecondInBin = averageKilometersPerHourInBin * 0.277777778;
    -- distance = velocity * time 
    metersInBin = averageMetersPerSecondInBin * secondsInBin;

    -- RAISE NOTICE 'metersInBin = %', metersInBin;
    
    -- add record to speed distribution table
    sql := 'INSERT INTO speeddistribution (tagid, daytype, hourbin, speedbin, secondsinbin, metersinbin, avgmeterspersecond) VALUES';
    sql := sql || '('''|| _tagId ||''', ''' || _dayType || ''', ' || _hourBin || ', ' || _speedBin || ', ' 
              || secondsInBin || ', ' || metersInBin || ', ' || averageMetersPerSecondInBin ||')';
    -- RAISE NOTICE 'sql = %', sql;
    EXECUTE sql;

    END IF;
    
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_insertSpeedDistributionRecord(_tagId text, _dayType text, _hourBin integer, _speedBin integer) OWNER TO gis;
