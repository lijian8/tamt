CREATE OR REPLACE FUNCTION 
	TAMT_updateSpeedDistributionTrafficFlowWithPercentValuesPerYear(
		activeoption text, 
		option1weekday integer, 
		option2weekday integer,
		option2saturday integer,
		option2sundayholiday integer)
  RETURNS void AS
$BODY$
DECLARE
	
BEGIN

	RAISE NOTICE 'START TAMT_updateSpeedDistributionTrafficFlowWithPercentValuesPerYear';
	RAISE NOTICE 'activeoption=%', activeoption;
	RAISE NOTICE 'option1weekday=%', option1weekday;
	RAISE NOTICE 'option2weekday=%', option2weekday;
	RAISE NOTICE 'option2saturday=%', option2saturday;
	RAISE NOTICE 'option2sundayholiday=%', option2sundayholiday;


	-- drop
	DROP TABLE IF EXISTS tmp_speeddistribution_yearly_step1;
	
	-- during dev, always start fresh
	UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = null,
			percentvehiclemetersperyear = null;
			
	IF activeoption = '1'
	THEN
		RAISE NOTICE 'Proceed with option 1 calculations';
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option1weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option1weekday
			WHERE daytype = 'WEEKDAY';
			-- This means that at the end of this query, any rows with 
			-- SATURDAY or SUNDAYHOLIDAY daytypes do NOT have percentvehiclesecondsperbinperyear
			-- or percentvehiclemetersperbinperyear
	ELSE
		RAISE NOTICE 'Proceed with option 2 calculations';

		-- WEEKDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2weekday
			WHERE daytype = 'WEEKDAY';
		-- SATURDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2saturday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2saturday
			WHERE daytype = 'SATURDAY';
		-- SUNDAY_HOLIDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2sundayholiday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2sundayholiday
			WHERE daytype = 'SUNDAY_HOLIDAY';	
		-- this completes the multiplier step, but we still have to do the sum

		-- for every tag/veh/speedbin (ignoring daytype)
			-- get a sum of the records for vehiclesecondsperday and vehiclemetersperday
			



		-- (this lookup is per row; checking on daytype)
		-- VSBYWEEK = vehiclepersecondsperday * option2weekday WHERE daytype = 'WEEKDAY'
		-- VSBYSAT = vehiclepersecondsperday * option2saturday WHERE daytype = 'SATURDAY'
		-- VSBYSUN = vehiclepersecondsperday * option2sunday WHERE daytype = 'SUNDAY_HOLIDAY'

		-- (this update is over the whole table)
		-- UPDATE percentvehiclesecondsperbinperyear = VSBYWEEK + VSBYSAT + VSBYSUN

		-- (this lookup is per row; checking on daytype)
		-- VMBYWEEK = vehiclepermetersperday * option2weekday WHERE daytype = 'WEEKDAY'
		-- VMBYSAT = vehiclepermetersperday * option2saturday WHERE daytype = 'SATURDAY'
		-- VMBYSUN = vehiclepermetersperday * option2sunday WHERE daytype = 'SUNDAY_HOLIDAY'

		-- (this update is over the whole table)
		-- UPDATE percentvehiclemetersperbinperyear = VMBYWEEK + VMBYSAT + VMBYSUN
		
	END IF;

	-- CREATE TABLE AS
	CREATE TABLE tmp_speeddistribution_yearly_step1 AS 
		SELECT 
			tagid, daytype,vehicletype,speedbin,
			percentvehiclesecondsperyear as pvspy,
			percentvehiclemetersperyear as pvmpy
		FROM speeddistributiontrafficflow
		WHERE percentvehiclesecondsperyear IS NOT NULL;
        
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION 
	TAMT_updateSpeedDistributionTrafficFlowWithPercentValuesPerYear(
		activeoption text, 
		option1weekday integer, 
		option2weekday integer,
		option2saturday integer,
		option2sundayholiday integer)
OWNER TO gis;