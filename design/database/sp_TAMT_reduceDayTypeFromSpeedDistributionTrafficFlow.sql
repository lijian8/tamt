-- Function: tamt_reducedaytypefromspeeddistributiontrafficflow(text, integer, integer, integer, integer)

-- DROP FUNCTION tamt_reducedaytypefromspeeddistributiontrafficflow(text, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION tamt_reducedaytypefromspeeddistributiontrafficflow(activeoption text, option1weekday integer, option2weekday integer, option2saturday integer, option2sundayholiday integer)
  RETURNS void AS
$BODY$
DECLARE
	r RECORD;
	tag RECORD;
	vtype RECORD;
	lastspeedbin integer;
	summ RECORD;
	sumsecs double precision;
	summets double precision;
BEGIN

 	RAISE NOTICE 'START TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow';
 	RAISE NOTICE 'activeoption=%', activeoption;
 	RAISE NOTICE 'option1weekday=%', option1weekday;
 	RAISE NOTICE 'option2weekday=%', option2weekday;
 	RAISE NOTICE 'option2saturday=%', option2saturday;
 	RAISE NOTICE 'option2sundayholiday=%', option2sundayholiday;

	sumsecs := 0;
	summets := 0;

	-- drop
	RAISE NOTICE 'Dropping tables...';
	DROP TABLE IF EXISTS tmp_speeddistribution_yearly_step1;
	DROP TABLE IF EXISTS speeddistributiontrafficflowtagvehiclespeed;
	
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
		
	END IF;

	-- create a (temporary) table with object IDs so we can use them for the update later
	CREATE TABLE tmp_speeddistribution_yearly_step1 WITH (OIDS=TRUE) AS 
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
			RAISE NOTICE 'tag=%', tag;
			FOR vtype IN SELECT vehicletype FROM vehicletypes
			LOOP
				RAISE NOTICE 'vtype=%', vtype;
				FOR i IN 0..lastspeedbin LOOP
					RAISE NOTICE 'speedbin=%', i;
					
					SELECT INTO summ
						SUM(t.percentvehiclesecondsperyear) AS seconds,
						SUM(t.percentvehiclemetersperyear) AS meters
						FROM tmp_speeddistribution_yearly_step1 t
						WHERE t.tagid = tag.tagid
						AND t.vehicletype = vtype.vehicletype
						AND speedbin = i
						LIMIT 1;
					RAISE NOTICE 'summ=%', summ;
					-- RAISE NOTICE 'm/s=%', summ.meters / summ.seconds;
					
					UPDATE tmp_speeddistribution_yearly_step1
						SET
						percentvehiclesecondsperyear = summ.seconds,
						percentvehiclemetersperyear = summ.meters
						WHERE tagid = tag.tagid
						AND vehicletype = vtype.vehicletype
						AND speedbin = i;
					-- calculate average speed outside of loop
						
					
				END LOOP; 
			END LOOP;
		END LOOP;
	ELSE
	END IF;

	-- move the tmp table into a more permanent space
	-- By using the DISTINCT clause we get rid of duplicate rows 
	-- (which happens in the OPTION002 case)
	CREATE TABLE speeddistributiontrafficflowtagvehiclespeed WITH(OIDS=TRUE) AS 
		SELECT 
		DISTINCT ON(tagid, vehicletype, speedbin) *
		FROM tmp_speeddistribution_yearly_step1;
		
	-- recalculate the average speed in the new table
	-- (doing it here instead of the temp table improves performance
	-- since we don't have to update rows that are eliminated during
	-- the table creation above)
	FOR r IN SELECT * FROM speeddistributiontrafficflowtagvehiclespeed
	LOOP
		IF r.percentvehiclesecondsperyear = 0
		THEN
			UPDATE speeddistributiontrafficflowtagvehiclespeed
			SET averagemeterspersecond = 0
			WHERE oid = r.oid;
		ELSE
			UPDATE speeddistributiontrafficflowtagvehiclespeed
			SET averagemeterspersecond = percentvehiclemetersperyear / percentvehiclesecondsperyear
			WHERE oid = r.oid;
		END IF;
	END LOOP;
		


        
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_reducedaytypefromspeeddistributiontrafficflow(text, integer, integer, integer, integer) OWNER TO gis;
