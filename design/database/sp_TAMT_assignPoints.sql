-- Function: tamt_assignpoints(text)

-- DROP FUNCTION tamt_assignpoints(text);

CREATE OR REPLACE FUNCTION tamt_assignpoints(gid text)
  RETURNS integer AS
$BODY$
DECLARE

    -- record holders
    my_record RECORD;
    p gpspoints%rowtype;

    -- aggregates
    totalPoints int;
    totalAffected int;
    totalProcessed int;

    -- counters
    counter int;
    incrementCount int;

    -- placeholders
    updatedTag text;
    defaultZoneType text;
    tmp text;
    output int;

    -- important identifiers
    studyRegionId text;
    distance_tolerance numeric;
    bearing_tolerance numeric;
    tagId text;
    roadId text;
    zoneId text;
    zoneType text;

    -- flags
    hasRoadId boolean;
    hasZoneId boolean;
    
BEGIN

    totalAffected := 0;
    counter := 0;
    totalProcessed := 0;
    totalPoints := 0;
    incrementCount := 0;
    bearing_tolerance := 45.0;

    -- What studyRegion are we working on?
    SELECT INTO studyRegionId region FROM gpstraces WHERE id = gid;

    -- What is the default gps tagging tolerance for this study region?
    SELECT INTO distance_tolerance gps_tagging_tolerance FROM studyregion WHERE id = studyRegionId;

    -- If the tagging tolerance is not configured, use the default of 50m. 
    -- The UI is responsible to make sure this value is not less than 50m.
    IF distance_tolerance IS NULL
    THEN
	distance_tolerance = 50.0;
    END IF;
    
    -- How many GPS records are there for this trace?
    SELECT INTO totalPoints recordcount FROM gpstraces WHERE id = gid;
    
    -- We want an update frequency that makes sense for the size of the data
    IF totalPoints > 100 THEN
        incrementCount = round(totalPoints / 10);
    ELSIF totalPoints > 1000 THEN
        incrementCount = round(totalPoints / 100);
    ELSIF totalPoints > 10000 THEN
        incrementCount = round(totalPoints / 1000);
    END IF;

    -- record the initial status
    PERFORM TAMT_updateAssignStatus(gid, totalPoints, totalProcessed, totalAffected, false);
    
    -- main loop over points associated with this gpstrace
    FOR p IN SELECT * FROM gpspoints WHERE gpstraceid = gid ORDER BY created
    LOOP
	-- for this point record, get the nearest of the near roads with similar bearing
	tagId := '';
	roadId := '';
	hasRoadId := false;
	FOR my_record IN 
		SELECT
			r.tag_id,
			r.id,
			ST_Distance(
				ST_GeographyFromText(AsText(p.geometry)), 
				ST_GeographyFromText(AsText(r.geometry))) as distanceMeters,
			TAMT_compareBearing(p.geometry, p.bearing, r.geometry, bearing_tolerance) as similarbearing
		FROM roaddetails as r
		WHERE
			(ST_DWithin(
				ST_GeographyFromText(AsText(p.geometry)), 
				ST_GeographyFromText(AsText(r.geometry)), 
				distance_tolerance))
		AND 
			r.region = studyRegionId
		ORDER BY similarbearing DESC, distanceMeters ASC LIMIT 1
	LOOP
		tagId := my_record.tag_id;
		roadId := my_record.id;

		-- if we found a tag, record the tagId and roadId in the point record
		IF( tagId != '') THEN
			UPDATE gpspoints SET tag_id = tagId, road_id = roadId  WHERE pid = p.pid;
			totalAffected := totalAffected + 1;
			hasRoadId := true;
		END IF;
	END LOOP;

	/* If there is NO tag_id for the point by now, execute the search for the point in a zone */
	IF( hasRoadId = false ) THEN
		-- Query for which zone this point is contained by
		zoneId := '';
		zoneType := '';
		hasZoneId := false;
		FOR my_record IN 
			SELECT
				z.id,
				z.zonetype
			FROM
				zonedetails as z
			WHERE
				(ST_Contains(
					z.geometry,
					p.geometry
					))
			AND 
				z.region = studyRegionId
			LIMIT 1
			 -- we may need to find a more suitable constraint if zones overlap
			 -- or ensure elsewhere in the application that zones CANNOT overlap
		LOOP
			zoneId := my_record.id;
			zoneType := my_record.zonetype;	
				
			-- if we found a zone, record the zoneId and zoneType in the point record
			-- Note: the zoneType acts like a tag, so it is recorded in the tag_id column
			IF( zoneId != '') THEN
				UPDATE gpspoints SET tag_id = zoneType, zone_id = zoneId  WHERE pid = p.pid;
				totalAffected := totalAffected + 1;
				hasZoneId := true;
			END IF;
			
		END LOOP;
		-- UPDATE gpspoints SET road_id = '#needsZone' WHERE pid = p.pid;
	END IF;

	-- and now we apply the default zonetype to all remaining untagged points
	IF( hasZoneId = false ) THEN
		defaultZoneType := '';
		SELECT INTO defaultZoneType default_zone_type FROM studyregion
			WHERE id = studyRegionId;
		UPDATE gpspoints SET tag_id = defaultZoneType, zone_id = 'DEFAULT' WHERE pid = p.pid;
		totalAffected := totalAffected + 1;
	END IF;
	
	-- this loop helps us record the work in chunks
	counter := counter + 1;
	totalProcessed := totalProcessed + 1;
	IF( counter = incrementCount) THEN
		-- update the status after each p if the increment count is reached
		PERFORM TAMT_updateAssignStatus(gid, totalPoints, totalProcessed, totalAffected, false);
		counter := 0;
	END IF;
	
    END LOOP;

    -- when we have looped over all the points for this record, record the status
    PERFORM TAMT_updateAssignStatus(gid, totalPoints, totalProcessed, totalAffected, true);

    
    
    -- and return how many points were tagged
    RETURN totalAffected;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_assignpoints(text) OWNER TO postgres;
