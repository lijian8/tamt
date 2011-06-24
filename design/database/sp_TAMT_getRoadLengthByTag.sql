-- Function: TAMT_getRoadLengthByTag(text)

-- DROP FUNCTION TAMT_getRoadLengthByTag(text);

CREATE OR REPLACE FUNCTION TAMT_getRoadLengthByTag(_tagid text)
  RETURNS double precision AS
$BODY$
DECLARE
	roadLength double precision;
	numzones integer;
	defaultZoneType text;
	blocklength integer;
	tag RECORD;
	_studyRegion RECORD;
BEGIN
	roadLength := 0.0;
	numzones := 0;
	defaultZoneType := '';

	SELECT INTO tag * from tagdetails where id = _tagid;
	-- RAISE NOTICE '--tagid=%, name=%, description=%', tag.id, tag.name, tag.description;

	/*
	 * We need the user-defined blocklength from the current studyregion,
	 * but it needs to be the value of the match between this tag's description (RWT)
	 * and the default_zone_type of the studyregion.
	 */
	SELECT INTO _studyRegion * 
		FROM studyregion 
		WHERE iscurrentregion = true;

	defaultZoneType := _studyRegion.default_zone_type;
	
	-- Get the blocklength for the appropriate defaultZoneType
	IF defaultZoneType = '#COM'
	THEN
		SELECT INTO blocklength commercial_block_length
			FROM studyregion 
			WHERE iscurrentregion = true;
	ELSIF defaultZoneType = '#IND'
	THEN
		SELECT INTO blocklength industrial_block_length 
			FROM studyregion 
			WHERE iscurrentregion = true;
	ELSIF defaultZoneType = '#RES'
	THEN
		SELECT INTO blocklength residential_block_length 
			FROM studyregion 
			WHERE iscurrentregion = true;
	END IF;

	/*
	 * Get the road length:
	 *
	 * If the tag is a reserved-word tag (RES, COM, IND) then there is no road geometry
	 * associated with it. In this case, we need to fetch the area of all zones with
	 * the same tag (ie, RES, COM, or IND). If the area is NULL because there are no
	 * user-defined zones of that type, then check if the default study region zone type
	 * has the same tag. If it does, use the study region area. If not, set to 0.
	*/ 
	IF( tag.description = '#RES' OR
		tag.description = '#COM' OR
			tag.description = '#IND')
	THEN
		-- 1. Check if there are any zones with the same reserved-word tag
		SELECT INTO numzones count(*)
			FROM zonedetails 
			WHERE zonetype = tag.description;
		-- RAISE NOTICE 'using zone geometry, numzones=%', numzones;
		-- 2. If one or more exist, use zone area and block length to calc the proxy length
		IF( numzones > 0)
		THEN
			SELECT INTO roadLength
				SUM(st_area(st_transform(geometry,900913)) / blocklength)
				-- using ZONE area
				FROM zonedetails
				WHERE region = _studyRegion.id
				AND zonetype = tag.description;
		ELSE
		-- 3. If none exist, use the blocklength and the area of the study region
			SELECT INTO roadLength
				SUM(st_area(st_transform(geometry,900913)) / blocklength)
				-- using STUDYREGION area
				FROM studyregion
				WHERE id = _studyRegion.id;
		END IF;
	ELSE
		-- for non-reserved-word tags, use geometry
		-- RAISE NOTICE 'use road geometry	';
		SELECT INTO roadLength 
			SUM(st_length(st_transform(geometry,900913))) 
			FROM roaddetails 
			WHERE tag_id = tag.id;
	END IF;
	RETURN roadLength;
END;	
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_getRoadLengthByTag(text) OWNER TO gis;
