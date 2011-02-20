CREATE OR REPLACE FUNCTION TAMT_reduceTagFromSpeedDistributionTrafficFlowTagVehicleSpeed()
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
BEGIN

 	RAISE NOTICE 'START TAMT_reduceTagFromSpeedDistributionTrafficFlowTagVehicleSpeed';
 	
	-- drop
	RAISE NOTICE 'Dropping tables...';
	DROP TABLE IF EXISTS tmp_speeddistribution_tagless;
	
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
		blocklength = 320; --TODO: get from updated studyregion datamodel 

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
	--	copy into a new table; but don't need columns percentvehiclesecondsperyear, percentvehiclemetersperyear
	--		this is Table E in http://code.google.com/p/tamt/wiki/RemovingTagFromSpeedDistributionTrafficFlow
	--	DON'T FORGET TO GO BACK AND PUT IN THE BLOCKLENGTH FACTOR IN THE UI

	-- Step 7: Fractional seconds and meters
	-- TODO:
	-- copy table E into table F (see: http://code.google.com/p/tamt/wiki/FinalSpeedBinTablePseudoCode)
	--	sum tagMeters and tagSeconds on same vehicle type (.: across all speedbins for this vehicletype)
	--	for each row, calc tagMeters/sumTagMeters and tagSeconds/sumTagSeconds
	--		once again, not sure what average speed means here.
	--	UI: present a button on Speed Bin UI to trigger the same code as fullTest in SpeedBinTests
	--	UI: present a screen to display output of table F, with downloadable CSV (like trafficflow reports)
	--	UI: nice to have -- downloadable CSV for all intermediate tables
	--					* speeddistribution, 
	--					* speeddistributiontrafficflow, 			
	--					* speeddistributiontrafficflowtagvehiclespeed,		(Table D)
	--					* speeddistributiontrafficflowvehiclespeed,		(Table E)
	--					* speeddistributiontrafficflowvehiclespeedfraction 	(Table F)
	
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_reduceTagFromSpeedDistributionTrafficFlowTagVehicleSpeed() OWNER TO gis;
