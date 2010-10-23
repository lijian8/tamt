CREATE OR REPLACE FUNCTION 
	TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow(
		activeoption text, 
		option1weekday integer, 
		option2weekday integer,
		option2saturday integer,
		option2sundayholiday integer)
  RETURNS void AS
$BODY$
DECLARE
	tag RECORD;
	vtype RECORD;
	lastspeedbin integer;
	summ RECORD;
	sumsecs double precision;
	summets double precision;
BEGIN

-- 	RAISE NOTICE 'START TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow';
-- 	RAISE NOTICE 'activeoption=%', activeoption;
-- 	RAISE NOTICE 'option1weekday=%', option1weekday;
-- 	RAISE NOTICE 'option2weekday=%', option2weekday;
-- 	RAISE NOTICE 'option2saturday=%', option2saturday;
-- 	RAISE NOTICE 'option2sundayholiday=%', option2sundayholiday;

	sumsecs := 0;
	summets := 0;

	-- drop
	DROP TABLE IF EXISTS tmp_speeddistribution_yearly_step1;
	DROP TABLE IF EXISTS speeddistributiontrafficflowtagvehiclespeed;
	
	-- during dev, always start fresh
	UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = null,
			percentvehiclemetersperyear = null;
			
	IF activeoption = '1'
	THEN
		-- RAISE NOTICE 'Proceed with option 1 calculations';
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option1weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option1weekday,
			WHERE daytype = 'WEEKDAY';
			-- This means that at the end of this query, any rows with 
			-- SATURDAY or SUNDAYHOLIDAY daytypes do NOT have percentvehiclesecondsperbinperyear
			-- or percentvehiclemetersperbinperyear
	ELSE
		-- RAISE NOTICE 'Proceed with option 2 calculations';

		-- WEEKDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2weekday,
			WHERE daytype = 'WEEKDAY';
		-- SATURDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2saturday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2saturday,
			WHERE daytype = 'SATURDAY';
		-- SUNDAY_HOLIDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2sundayholiday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2sundayholiday,
			WHERE daytype = 'SUNDAY_HOLIDAY';
		
	END IF;

	-- create a (temporary) table
	CREATE TABLE tmp_speeddistribution_yearly_step1 AS 
		SELECT 
			tagid, vehicletype,speedbin,
			percentvehiclesecondsperyear,
			percentvehiclemetersperyear,
			weightedaveragespeed as averagemeterspersecond
		FROM speeddistributiontrafficflow
		WHERE percentvehiclesecondsperyear IS NOT NULL;
		
	-- do the sum step for option2 based on tag/veh/speedbin
	-- select tags
	IF activeoption = '2'
	THEN

		SELECT INTO lastspeedbin speedbinnumber FROM gpspoints ORDER BY speedbinnumber DESC LIMIT 1;
		
		FOR tag IN SELECT DISTINCT tagid FROM tmp_speeddistribution_yearly_step1
		LOOP
			--RAISE NOTICE 'tag=%', tag;
			FOR vtype IN SELECT vehicletype FROM vehicletypes
			LOOP
				--RAISE NOTICE 'vtype=%', vtype;
				FOR i IN 0..lastspeedbin LOOP
					-- RAISE NOTICE 'speedbin=%', i;
					
					SELECT INTO summ
						SUM(t.percentvehiclesecondsperyear) AS seconds,
						SUM(t.percentvehiclemetersperyear) AS meters
						FROM tmp_speeddistribution_yearly_step1 t
						WHERE t.tagid = tag.tagid
						AND t.vehicletype = vtype.vehicletype
						AND speedbin = i
						LIMIT 1;
					
					UPDATE tmp_speeddistribution_yearly_step1
						SET
						percentvehiclesecondsperyear = summ.seconds,
						percentvehiclemetersperyear = summ.meters
						WHERE tagid = tag.tagid
						AND vehicletype = vtype.vehicletype
						AND speedbin = i;
			
					-- calculate average speed
					BEGIN
						UPDATE tmp_speeddistribution_yearly_step1
							SET averagemeterspersecond = 
							percentvehiclemetersperyear / percentvehiclesecondsperyear
							WHERE tagid = tag.tagid
							AND vehicletype = vtype.vehicletype
							AND speedbin = i;
					EXCEPTION
						WHEN division_by_zero THEN
						UPDATE tmp_speeddistribution_yearly_step1
							SET averagemeterspersecond = 0
							WHERE tagid = tag.tagid
							AND vehicletype = vtype.vehicletype
							AND speedbin = i;
					END;
						
					
				END LOOP; 
			END LOOP;
		END LOOP;
	ELSE
	END IF;

	-- move the tmp table into a more permanent space
	CREATE TABLE speeddistributiontrafficflowtagvehiclespeed AS 
		SELECT 
		DISTINCT ON(tagid, vehicletype, speedbin) *
		FROM tmp_speeddistribution_yearly_step1;

        
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION 
	TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow(
		activeoption text, 
		option1weekday integer, 
		option2weekday integer,
		option2saturday integer,
		option2sundayholiday integer)
OWNER TO gis;