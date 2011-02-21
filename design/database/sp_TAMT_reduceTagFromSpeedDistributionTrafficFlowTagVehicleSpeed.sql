-- Function: tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed()

-- DROP FUNCTION tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed();

CREATE OR REPLACE FUNCTION tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed()
  RETURNS void AS
$BODY$
DECLARE
	tag RECORD;
	study_region RECORD;
	lengthUserDefinedRoadsByTag numeric;
	totalMeterLengthByTag numeric;
	zone RECORD;
	blocklength numeric;
	proxyDefaultZoneLength numeric;
	tmp integer;
	vtype RECORD;
	lastspeedbin integer;
	summ RECORD;
BEGIN

 	RAISE NOTICE 'START TAMT_reduceTagFromSpeedDistributionTrafficFlowTagVehicleSpeed';
 	
	-- drop
	RAISE NOTICE 'Dropping tables...';
	DROP TABLE IF EXISTS tmp_speeddistribution_tagless;
	DROP TABLE IF EXISTS speeddistributiontrafficflowvehiclespeed;
	
	CREATE TABLE tmp_speeddistribution_tagless WITH (OIDS = TRUE) AS 
		SELECT 
			*
		FROM speeddistributiontrafficflowtagvehiclespeed;
	-- add a few columns:
	ALTER TABLE tmp_speeddistribution_tagless ADD tagMeters double precision;
	ALTER TABLE tmp_speeddistribution_tagless ADD tagSeconds double precision;
	 	
	-- Step 0.1: get current study region
	SELECT INTO study_region 
		id, 
		st_area(st_transform(geometry,900913)) as area
	FROM studyregion where iscurrentregion = TRUE;
	RAISE NOTICE 'study_region.id=%', study_region.id;
	
	-- Step 0.2: get tags for current study region
	FOR tag IN SELECT * from tagdetails where region = study_region.id
	LOOP
		RAISE NOTICE '--tagid=%', tag.id;
		
		-- Step 1: getUserDefinedRoadsLength for the current tag
		SELECT INTO lengthUserDefinedRoadsByTag SUM(st_length(st_transform(geometry,900913))) 
			from roaddetails WHERE tag_id = tag.id;
		RAISE NOTICE '----lengthUserDefinedRoadsByTag=%', lengthUserDefinedRoadsByTag;

		-- Step 2: Calculate proxy length for user-defined zones
		
		-- Step 2a: get blocklength for studyregion's default zone type
		-- Step 2a: get blocklength for studyregion's default zone type
		SELECT INTO blocklength default_block_length from studyregion WHERE id = study_region.id;
		RAISE NOTICE '----blocklength=%', blocklength;
		
		-- Step 2b: get the proxy length of user-defined zones in this region
		SELECT INTO zone 
			SUM(st_area(st_transform(geometry,900913)) / blocklength) as proxyLengthUserDefinedZonesByRegion,
			SUM(ST_Area(st_transform(geometry,900913))) as sumArea
			from zonedetails WHERE region = study_region.id;
		RAISE NOTICE '----zone.proxyLengthUserDefinedZonesByRegion=%', zone.proxyLengthUserDefinedZonesByRegion;
		RAISE NOTICE '----zone.sumArea=%', zone.sumArea;

		-- Step 3: getProxyDefaultZonesLength
		-- subtract zone.sumArea from study_region.area
		proxyDefaultZoneLength = (study_region.area - zone.sumArea) / blocklength;
		RAISE NOTICE '----proxyDefaultZoneLength=%', proxyDefaultZoneLength;	

		-- Step 4: getTotalMeterLengthByTag
		totalMeterLengthByTag = lengthUserDefinedRoadsByTag + 
			zone.proxyLengthUserDefinedZonesByRegion +
			proxyDefaultZoneLength;
		RAISE NOTICE '----totalMeterLengthByTag=%', totalMeterLengthByTag;

		-- Step 5: update the table 
			-- tagMeters = percentVehicleMetersPerYear * totalMeterLengthByTag
			-- tagSeconds = percentVehicleSecondsPerYear * totalMeterLengthByTag
		UPDATE tmp_speeddistribution_tagless SET
			tagMeters = percentVehicleMetersPerYear * totalMeterLengthByTag,
			tagSeconds = percentVehicleSecondsPerYear * totalMeterLengthByTag
			WHERE tagid = tag.id;		

	END LOOP;


	-- Step 6: Collapse the tagid column
	-- TODO: 
	-- 	sum(tagmeters, tagseconds) on same vehicle type + speedbin
	-- 	averagemeterspersecond = tagmeters / tagseconds
	--	** don't need to normalize **
	--	copy into a new table; but don't need columns tagid, percentvehiclesecondsperyear, percentvehiclemetersperyear
	--		this is Table E in http://code.google.com/p/tamt/wiki/RemovingTagFromSpeedDistributionTrafficFlow

	-- Step 6a: get last speed bin for looping below
	SELECT INTO lastspeedbin speedbinnumber FROM gpspoints ORDER BY speedbinnumber DESC LIMIT 1;

	-- Step 6b: loop over vehicle types and speedbins, summing tagseconds and tagmeters
	FOR vtype IN SELECT vehicletype FROM vehicletypes
	LOOP
		--RAISE NOTICE 'vtype=%', vtype;
		FOR i IN 0..lastspeedbin
		LOOP
			--RAISE NOTICE 'speedbin=%', i;
				SELECT INTO summ
					SUM(t.tagseconds) AS seconds,
					SUM(t.tagmeters) AS meters
					FROM tmp_speeddistribution_tagless t
					WHERE t.vehicletype = vtype.vehicletype
					AND speedbin = i
					LIMIT 1;
				-- RAISE NOTICE 'summ=%', summ;

				-- overwrite per-record tagmeters and tagseconds with 
				-- sums of those variales on vehicletype+speedbin, disregarding tags
				UPDATE tmp_speeddistribution_tagless
					SET
					tagseconds = summ.seconds,
					tagmeters = summ.meters,
					-- null out useless columns
					percentvehiclesecondsperyear = null,
					percentvehiclemetersperyear = null,
					tagid = null
					WHERE vehicletype = vtype.vehicletype
					AND speedbin = i;
				
		END LOOP;
	END LOOP;

	-- Step 6c: copy data from temporary table to more permanent table and remove useless columns
	CREATE TABLE speeddistributiontrafficflowvehiclespeed WITH (OIDS = FALSE) AS 
		SELECT DISTINCT ON (vehicletype, speedbin)
			*
		FROM tmp_speeddistribution_tagless;
	ALTER TABLE speeddistributiontrafficflowvehiclespeed RENAME averagemeterspersecond TO meterspersecond;
	ALTER TABLE speeddistributiontrafficflowvehiclespeed DROP tagid;
	ALTER TABLE speeddistributiontrafficflowvehiclespeed DROP percentvehiclesecondsperyear;
	ALTER TABLE speeddistributiontrafficflowvehiclespeed DROP percentvehiclemetersperyear;

	-- Step 6d: in new table, recalculate average speed
	UPDATE speeddistributiontrafficflowvehiclespeed
		SET meterspersecond = tagmeters / tagseconds;
		

	-- Step 7: Fractional seconds and meters
	-- TODO:
	-- WAIT!! According to John's last instructions, we don't have to do Step 7 because we aren't 
	-- normalizing at this point any more. So Table E is the final table!!
		-- DEPRECATED: copy table E into table F (see: http://code.google.com/p/tamt/wiki/FinalSpeedBinTablePseudoCode)
		--	sum tagMeters and tagSeconds on same vehicle type (.: across all speedbins for this vehicletype)
		--	for each row, calc tagMeters/sumTagMeters and tagSeconds/sumTagSeconds
		--		once again, not sure what average speed means here.

	-- TODO:
	--	UI: present a button on Speed Bin UI to trigger the same code as fullTest in SpeedBinTests
	--	UI: present a screen to display output of table E, with downloadable CSV (like trafficflow reports)
	--	UI: nice to have -- downloadable CSV for all intermediate tables
	--					* speeddistribution, 
	--					* speeddistributiontrafficflow, 			
	--					* speeddistributiontrafficflowtagvehiclespeed,		(Table D)
	--					* speeddistributiontrafficflowvehiclespeed,		(Table E)
	
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed() OWNER TO gis;
