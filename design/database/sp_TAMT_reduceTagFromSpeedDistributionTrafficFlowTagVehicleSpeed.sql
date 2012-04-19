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
	_regionid text;
	_default_zone_type text;
BEGIN

 	RAISE NOTICE 'START TAMT_reduceTagFromSpeedDistributionTrafficFlowTagVehicleSpeed';

	-- we should only be operating on the current study region
	SELECT INTO _regionid id FROM studyregion WHERE iscurrentregion = TRUE;

	-- should not drop this one, but should zero out any rows for this region id
	DELETE FROM speeddistributiontrafficflowvehiclespeed WHERE regionid = _regionid;
	 	
	-- clean up previous data 
	RAISE NOTICE 'Drop and recreate tmp_speeddistribution_tagless with only data for current region';
	DROP TABLE IF EXISTS tmp_speeddistribution_tagless;
	CREATE TABLE tmp_speeddistribution_tagless WITH (OIDS = TRUE) AS 
		SELECT 
			*
		FROM speeddistributiontrafficflowtagvehiclespeed -- data generated in reduce day type
		WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionid);
	-- add a few columns:
	ALTER TABLE tmp_speeddistribution_tagless ADD tagMeters double precision;
	ALTER TABLE tmp_speeddistribution_tagless ADD tagSeconds double precision;
	-- we also want to report out in kph
	ALTER TABLE tmp_speeddistribution_tagless ADD tagKilometers double precision;
	ALTER TABLE tmp_speeddistribution_tagless ADD tagHours double precision;
	ALTER TABLE tmp_speeddistribution_tagless ADD tagKph double precision;
	
	 	
	-- Step 0.1: get current study region
	SELECT INTO study_region 
		id, 
		st_area(st_transform(geometry,900913)) as area
	FROM studyregion where id = _regionid;
	RAISE NOTICE 'study_region.id=%', study_region.id;
	
	-- Step 0.1a: get blocklength for studyregion's default zone type
	SELECT INTO _default_zone_type default_zone_type FROM studyregion WHERE id = study_region.id;
	IF _default_zone_type = '#COM'
	THEN
		SELECT INTO blocklength commercial_block_length from studyregion WHERE id = study_region.id;
	ELSIF _default_zone_type = '#IND'
	THEN
		SELECT INTO blocklength industrial_block_length from studyregion WHERE id = study_region.id;
	ELSIF _default_zone_type = '#RES'
	THEN
		SELECT INTO blocklength residential_block_length from studyregion WHERE id = study_region.id;
	END IF;
	RAISE NOTICE '--blocklength=%', blocklength;
	
	-- Step 0.2: get tags for current study region
	FOR tag IN SELECT * from tagdetails where region = study_region.id
	LOOP
		RAISE NOTICE '--tagid=%', tag.id;
		
		-- Step 1: getUserDefinedRoadsLength for the current tag
		SELECT INTO lengthUserDefinedRoadsByTag SUM(st_length(st_transform(geometry,900913))) 
			from roaddetails WHERE tag_id = tag.id;
		--RAISE NOTICE '----lengthUserDefinedRoadsByTag=%', lengthUserDefinedRoadsByTag;

		-- Step 2: Calculate proxy length for user-defined zones
		-- Step 2a: blocklength has already been fetched
		
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
		RAISE NOTICE '----update tmp_speeddistribution_tagless';
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
	SELECT INTO lastspeedbin speedbinnumber 
		FROM gpspoints 
		WHERE tag_id IN (SELECT id FROM tagdetails WHERE region = _regionid)
		ORDER BY speedbinnumber DESC LIMIT 1;
	RAISE NOTICE 'lastspeedbin=%', lastspeedbin;
	
	-- Step 6b: loop over vehicle types and speedbins, summing tagseconds and tagmeters
	FOR vtype IN SELECT vehicletype FROM vehicletypes
	LOOP
		RAISE NOTICE 'vtype=%', vtype;
		FOR i IN 0..lastspeedbin
		LOOP
			RAISE NOTICE 'speedbin=%', i;
				SELECT INTO summ
					SUM(t.tagseconds) AS seconds,
					SUM(t.tagmeters) AS meters
					FROM tmp_speeddistribution_tagless t
					WHERE t.vehicletype = vtype.vehicletype
					AND speedbin = i
					LIMIT 1;
				RAISE NOTICE 'summ=%', summ;

				-- overwrite per-record tagmeters and tagseconds with 
				-- sums of those variales on vehicletype+speedbin, disregarding tags
				RAISE NOTICE 'Update tmp_speeddistribution_tagless with summ';
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
	-- Note: this is a two step copy so we can group by vehicle type and speed bin without grouping by other fields
	DROP TABLE IF EXISTS tmp_sptfvs;
	CREATE TABLE tmp_sptfvs WITH (OIDS = FALSE) AS 
		SELECT DISTINCT ON (vehicletype, speedbin)
			*
		FROM tmp_speeddistribution_tagless;	
	ALTER TABLE tmp_sptfvs RENAME averagemeterspersecond TO meterspersecond;
	ALTER TABLE tmp_sptfvs DROP tagid;
	ALTER TABLE tmp_sptfvs DROP percentvehiclesecondsperyear;
	ALTER TABLE tmp_sptfvs DROP percentvehiclemetersperyear;
	INSERT INTO speeddistributiontrafficflowvehiclespeed (regionid, vehicletype, speedbin, tagmeters, tagseconds)
		(SELECT 
			--(select id from studyregion where iscurrentregion = true),
			_regionid,
			vehicletype,
			speedbin,
			tagmeters,
			tagseconds
		FROM tmp_sptfvs);
	--DROP TABLE IF EXISTS tmp_sptfvs;

	-- Step 6d: in new table, recalculate m/s, taghours, tagkilometers, and tagkph
	
	
	-- DIVIDE BY ZERO FIX:
	-- IF tagseconds = 0, then update meterspersecond to also be 0, rather than dividing by 0
	-- IF tagseconds = 0, then update tagkph to also be 0, rather than dividing by 0
	IF tagseconds = 0
	THEN
		UPDATE speeddistributiontrafficflowvehiclespeed
			SET 
				meterspersecond = 0
				taghours = 0,
				tagkilometers = tagmeters / 1000,
				tagkph = 0
			WHERE regionid = _regionid;	
	ELSE
		UPDATE speeddistributiontrafficflowvehiclespeed
			SET 
				meterspersecond = tagmeters / tagseconds,
				taghours = tagseconds / 3600,
				tagkilometers = tagmeters / 1000,
				tagkph = (tagmeters / 1000)/(tagseconds / 3600)
			WHERE regionid = _regionid;
	ENDIF;
	
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed() OWNER TO gis;