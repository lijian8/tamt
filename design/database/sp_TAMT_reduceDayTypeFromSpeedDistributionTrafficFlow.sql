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
	_regionid text;
BEGIN

 	RAISE NOTICE 'START TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow';
 	RAISE NOTICE 'activeoption=%', activeoption;
 	RAISE NOTICE 'option1weekday=%', option1weekday;
 	RAISE NOTICE 'option2weekday=%', option2weekday;
 	RAISE NOTICE 'option2saturday=%', option2saturday;
 	RAISE NOTICE 'option2sundayholiday=%', option2sundayholiday;

	sumsecs := 0;
	summets := 0;

	-- we should only be operating on the current study region
	SELECT INTO _regionid id FROM studyregion WHERE iscurrentregion = TRUE;

	-- Instead of dropping, just empty it out the rows with tagids corresponding to the current study region
	DELETE FROM speeddistributiontrafficflowtagvehiclespeed 
		WHERE tagid IN (SELECT id FROM tagdetails where region = _regionid);

	-- drop tmp tables
	RAISE NOTICE 'Dropping tables...';
	DROP TABLE IF EXISTS tmp_speeddistribution_yearly_step1;
	
	-- during analysis, always start with fresh percentages
	UPDATE speeddistributiontrafficflow
		SET
		percentvehiclesecondsperyear = null,
		percentvehiclemetersperyear = null
		WHERE tagid IN (SELECT id FROM tagdetails where region = _regionid);
			
	IF activeoption = '1'
	THEN
		RAISE NOTICE 'Proceed with option 1 calculations';
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option1weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option1weekday
			WHERE daytype = 'WEEKDAY'
			-- This means that at the end of this query, any rows with 
			-- SATURDAY or SUNDAYHOLIDAY daytypes do NOT have percentvehiclesecondsperbinperyear
			-- or percentvehiclemetersperbinperyear
			AND tagid IN (SELECT id FROM tagdetails where region = _regionid);
	ELSE
		RAISE NOTICE 'Proceed with option 2 calculations';

		-- WEEKDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2weekday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2weekday
			WHERE daytype = 'WEEKDAY'
			AND tagid IN (SELECT id FROM tagdetails where region = _regionid);
		-- SATURDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2saturday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2saturday
			WHERE daytype = 'SATURDAY'
			AND tagid IN (SELECT id FROM tagdetails where region = _regionid);
		-- SUNDAY_HOLIDAY 
		UPDATE speeddistributiontrafficflow
			SET
			percentvehiclesecondsperyear = percentvehiclesecondsperday * option2sundayholiday,
			percentvehiclemetersperyear = percentvehiclemetersperday * option2sundayholiday
			WHERE daytype = 'SUNDAY_HOLIDAY'
			AND tagid IN (SELECT id FROM tagdetails where region = _regionid);
		
	END IF;

	-- create a (temporary) table with object IDs so we can use them for the update later
	CREATE TABLE tmp_speeddistribution_yearly_step1 WITH (OIDS=TRUE) AS 
		SELECT 
			tagid, vehicletype,speedbin,
			percentvehiclesecondsperyear,
			percentvehiclemetersperyear,
			weightedaveragespeed as averagemeterspersecond
		FROM speeddistributiontrafficflow
		WHERE percentvehiclesecondsperyear IS NOT NULL
		AND tagid IN (SELECT id FROM tagdetails where region = _regionid);
		
	-- do the sum step for option2 based on tag/veh/speedbin
	-- select tags
	IF activeoption = '2'
	THEN

		SELECT INTO lastspeedbin speedbinnumber 
			FROM gpspoints 
			WHERE tag_id IN (SELECT id FROM tagdetails where region = _regionid)
			ORDER BY speedbinnumber DESC LIMIT 1;
		
		FOR tag IN 
			SELECT DISTINCT tagid 
			FROM tmp_speeddistribution_yearly_step1
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


	-- Copy data from temporary table to more permanent table and remove useless columns
	-- Note: this is a two step copy so we can group by tagid, vehicle type and speed bin without grouping by other fields
	DROP TABLE IF EXISTS tmp_sdtftvs;
	CREATE TABLE tmp_sdtftvs WITH (OIDS = FALSE) AS 
		SELECT DISTINCT ON(tagid, vehicletype, speedbin) 
		*
		FROM tmp_speeddistribution_yearly_step1;
 	INSERT INTO speeddistributiontrafficflowtagvehiclespeed 
		(tagid, vehicletype, speedbin, percentvehiclesecondsperyear, 
		 percentvehiclemetersperyear, averagemeterspersecond)
		(SELECT
			  tagid,
			  vehicletype,
			  speedbin,
			  percentvehiclesecondsperyear,
			  percentvehiclemetersperyear,
			  averagemeterspersecond
		FROM tmp_sdtftvs);
	DROP TABLE IF EXISTS tmp_sdtftvs;	
		
	-- recalculate the average speed in the new table
	-- (doing it here instead of the temp table improves performance
	-- since we don't have to update rows that are eliminated during
	-- the table creation above)
	FOR r IN 
		SELECT * FROM speeddistributiontrafficflowtagvehiclespeed
		WHERE tagid IN (SELECT id FROM tagdetails where region = _regionid)
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
