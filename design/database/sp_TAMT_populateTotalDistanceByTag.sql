-- Function: tamt_populatetotaldistancebytag()

-- DROP FUNCTION tamt_populatetotaldistancebytag();

CREATE OR REPLACE FUNCTION tamt_populatetotaldistancebytag()
  RETURNS void AS
$BODY$
DECLARE
	tag RECORD;
	study_region RECORD;
	lengthUserDefinedRoadsByTag numeric;
	totalMeterLengthByTag numeric;
	vkt numeric; -- vehicle kilometers traveled
	zone RECORD;
	proxyLength numeric;
	zSumArea numeric;
	blocklength numeric;
	proxyDefaultZoneLength numeric;
	tmp integer;
	vtype RECORD;
	lastspeedbin integer;
	summ RECORD;
	_regionid text;
	_default_zone_type text;
	sql text;
	numzones integer;
	defaultZoneType text;
BEGIN

 	--RAISE NOTICE 'START tamt_populatetotaldistancebytag';

	-- we should only be operating on the current study region
	SELECT INTO _regionid id FROM studyregion WHERE iscurrentregion = TRUE;

	-- should not drop this one, but should zero out any rows for this region id
	DELETE FROM totaldistancebytag WHERE regionid = _regionid;
	 	
	-- Step 0.1: get area of current study region
	SELECT INTO study_region 
		id, 
		st_area(st_transform(geometry,900913)) as area
	FROM studyregion where id = _regionid;
	--RAISE NOTICE 'study_region.id=%', study_region.id;
	
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
	--RAISE NOTICE '--_default_zone_type%', _default_zone_type;
	--RAISE NOTICE '--blocklength=%', blocklength;
	
	-- Step 0.2: get tags for current study region
	
	-- Note: in new version, tagdetails will contain distinct entries for res, com, ind for each region
	--		so they will be included here. But they don't make sense here, because entries in roaddetails
	--		are NOT tagged with RES,COM,IND. In this case, we should get all zeroes for the lengths
	--		and areas of RES,COM,IND "tags"
	
	FOR tag IN SELECT * from tagdetails where region = study_region.id
	LOOP
		--RAISE NOTICE '--tagid=%, name=%, description=%', tag.id, tag.name, tag.description;
		IF( tag.description = '#RES' OR
			tag.description = '#COM' OR
				tag.description = '#IND') THEN
			/*
			 * If the tag is a reserved-word tag (RES, COM, IND) then there is no road geometry
			 * associated with it. In this case, we need to fetch the area of all zones with
			 * the same tag (ie, RES, COM, or IND). If the area is NULL because there are no
			 * user-defined zones of that type, then check if the default study region zone type
			 * has the same tag. If it does, use the study region area. If not, set to 0.
			*/
			-- a. Check if there are any zones with the same reserved-word tag
			SELECT INTO numzones count(*)
				FROM zonedetails WHERE zonetype = tag.description;
			--RAISE NOTICE 'numzones=%', numzones;
			-- b. If one or more exist, use zone area and block length to calc the proxy length
			IF( numzones > 0)
			THEN
				SELECT INTO lengthUserDefinedRoadsByTag
					SUM(st_area(st_transform(geometry,900913)) / blocklength)
					-- using ZONE area
					FROM zonedetails
					WHERE region = study_region.id
					AND zonetype = tag.description;
			ELSE
			-- c. If none exist, use the blocklength and the area of the study region
				SELECT INTO lengthUserDefinedRoadsByTag
					SUM(st_area(st_transform(geometry,900913)) / blocklength)
					-- using STUDYREGION area
					FROM studyregion
					WHERE id = study_region.id;
			END IF;
		ELSE
			-- Step 1: getUserDefinedRoadsLength for the current tag
			--RAISE NOTICE 'use road geometry	';
			SELECT INTO lengthUserDefinedRoadsByTag SUM(st_length(st_transform(geometry,900913))) 
				from roaddetails WHERE tag_id = tag.id;		
			
		END IF;
		
		--RAISE NOTICE '----lengthUserDefinedRoadsByTag=%', lengthUserDefinedRoadsByTag;
		
		-- Step 2: Calculate proxy length for user-defined zones
		
		-- Step 2a: blocklength has already been fetched
		
		-- Step 2b: get the proxy length of user-defined zones in this region
		SELECT INTO zone 
			SUM(st_area(st_transform(geometry,900913)) / blocklength) as proxyLengthUserDefinedZonesByRegion,
			SUM(ST_Area(st_transform(geometry,900913))) as sumArea
			from zonedetails WHERE region = study_region.id;

		proxyLength = zone.proxyLengthUserDefinedZonesByRegion;
		zSumArea = zone.sumArea;
		
		-- Step 2c: if proxy length is null (ie, no user-defined zones), then set to 0
		IF proxyLength IS NULL
		THEN
			RAISE NOTICE '----proxyLength=%', proxyLength;
			RAISE NOTICE '----proxyDefaultZoneLength is NULL';
			proxyLength = 0;
			RAISE NOTICE '----proxyLength=%', proxyLength;
		END IF;

		-- Step 2d: if zone area is null (ie, no user-defined zones), then set to 0
		IF zSumArea IS NULL
		THEN
			RAISE NOTICE '----zSumArea=%', zSumArea;
			RAISE NOTICE '----sumArea is NULL';
			zSumArea = 0;
			RAISE NOTICE '----zSumArea=%', zSumArea;
		END IF;
		
		
		--RAISE NOTICE '----zone.proxyLengthUserDefinedZonesByRegion=%', zone.proxyLengthUserDefinedZonesByRegion;
		--RAISE NOTICE '----zone.sumArea=%', zone.sumArea;

		-- RAISE NOTICE '----proxyLength=%', proxyLength;
		-- RAISE NOTICE '----zSumArea=%', zSumArea;


		-- Step 3: getProxyDefaultZonesLength
		-- subtract zone.sumArea from study_region.area
		--RAISE NOTICE '----study_region.area=%', study_region.area;
		-- proxyDefaultZoneLength = (study_region.area - zone.sumArea) / blocklength;
		proxyDefaultZoneLength = (study_region.area - zSumArea) / blocklength;
		-- RAISE NOTICE '----proxyDefaultZoneLength=%', proxyDefaultZoneLength;	

		-- Step 4: getTotalMeterLengthByTag
		totalMeterLengthByTag = lengthUserDefinedRoadsByTag + 
			proxyLength +
			proxyDefaultZoneLength;
		-- RAISE NOTICE '----totalMeterLengthByTag=%', totalMeterLengthByTag;

		-- change the distance units to kilometers
		vkt = totalMeterLengthByTag / 1000;
		-- RAISE NOTICE '----kilometers=%', kilometers;

		-- Step 5: insert a record into totaldistancebytag
		-- add record to speed distribution table
		  -- pid serial NOT NULL,
		  -- tag_id text NOT NULL,
		  -- regionid text NOT NULL,
		  -- distance text NOT NULL,
		sql := 'INSERT INTO totaldistancebytag (tag_id, regionid, vkt) VALUES';
		sql := sql || '('''|| tag.id ||''', ''' || _regionid || ''', ' || vkt ||')';
		RAISE NOTICE 'sql=%', sql;
		EXECUTE sql;
		-- RAISE NOTICE 'sql = %', sql;

	END LOOP;
		
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_populatetotaldistancebytag() OWNER TO gis;
