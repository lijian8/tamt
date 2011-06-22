-- Function: tamt_comparebearing(geometry, double precision, geometry, double precision)

-- DROP FUNCTION tamt_comparebearing(geometry, double precision, geometry, double precision);

CREATE OR REPLACE FUNCTION tamt_comparebearing(point geometry, _pointbearing double precision, lastpoint geometry, line geometry, acceptablerange double precision)
  RETURNS boolean AS
$BODY$
DECLARE
    currentSegment geometry;
    closestSegment geometry;
    nextPointIndex int;
    currDist float;
    lastDist float;
    bearingClosestSegment float;
    bearingClosestSegmentReversed float;
    isSimilarBearing boolean;
    tmp text;
    pointbearing float8;
BEGIN

	isSimilarBearing := false;
-- 	SELECT INTO tmp ASTEXT(point);
-- 	RAISE NOTICE 'point=%',tmp;
-- 	SELECT INTO tmp ASTEXT(lastpoint);
-- 	RAISE NOTICE 'lastpoint=%',tmp;
	/*
	 * If the bearing is 0.00, then assume the GPS logger did not record bearing correctly.
	 * In this case, take the bearing of the line between the last point and this point.
	 */
-- 	pointbearing := _pointbearing;
-- 	RAISE NOTICE 'pointbearing (PRE)=%', pointbearing;
-- 	IF (pointbearing = 0::float8 ) THEN
-- 		RAISE NOTICE 'Change pointbearing based on lastpoint=%', lastpoint;
-- 		IF lastpoint IS NULL
-- 		THEN
-- 			-- This might happen on the first record in a GPS log because there is no "lastpoint"
-- 			RAISE NOTICE 'lastpoint is NULL, setting pointbearing to 0.00';
-- 			pointbearing := 0.00;
-- 		ELSE
-- 			RAISE NOTICE 'Create new azimuth=%', ST_Azimuth(lastpoint, point);
-- 			RAISE NOTICE 'Create new azimuth / (2*pi())*360=%', ST_Azimuth(lastpoint, point)/(2*pi())*360;
-- 			-- Otherwise, find out the degree angle between the last point and this point
-- 			pointbearing := ST_Azimuth(lastpoint, point)/(2*pi())*360;
-- 			RAISE NOTICE 'pointbearing has new value=%', pointbearing;
-- 		END IF;
-- 	END IF;
-- 	RAISE NOTICE 'pointbearing (POST)=%', pointbearing;

	/*
	 * Because a line may be made up of segments that "curve", the bearing of the line
	 * is not necessarily the bearing of the start point to the end point. First we 
	 * must determine which segment of the line is closest to our original point.
	 */
	FOR segment IN 1..ST_NumPoints(line)-1
	LOOP
		nextPointIndex := segment + 1;
		currentSegment := ST_MakeLine( ST_PointN(line,segment), ST_PointN(line,nextPointIndex));
		currDist := ST_Distance(point,currentSegment);
		--tmp := 'currentSegment=' || AsText(currentSegment);
		-- RAISE NOTICE '%', tmp;
		IF (closestSegment IS NULL) THEN
			closestSegment = currentSegment;
		END IF;
		IF (lastDist IS NULL) THEN
			lastDist = currDist;
		END IF;
		IF (currDist < lastDist) THEN
			closestSegment = currentSegment;
		END IF;			
	END LOOP;

	/*
	 * Now we can determine if the bearing of our original point is within
	 * the acceptable range of the bearing of the closest line segment.
	 */
	-- tmp := 'closestSegment=' || AsText(closestSegment);
	-- RAISE NOTICE '%', tmp;
	--tmp := 'closestDist=' || currDist;
	--RAISE NOTICE '%', tmp;
			
	bearingClosestSegment := ST_Azimuth(ST_StartPoint(closestSegment), ST_EndPoint(closestSegment))/(2*pi())*360;
	-- RAISE NOTICE 'bearingClosestSegment=%', bearingClosestSegment;
	bearingClosestSegmentReversed = bearingClosestSegment + 180.0;
	IF (pointBearing BETWEEN (bearingClosestSegment - acceptableRange) AND (bearingClosestSegment + acceptableRange) ) THEN
		isSimilarBearing := true;
	END IF;

	/* 
	 * We also need to take into account that the bearing for the line 
	 * (ie, the road in the TAMT Tag UI) may be reversed (ie, 180 degrees, 
	 * or, going the other way) depending on the direction it was drawn by the user.
	 */
	bearingClosestSegmentReversed = bearingClosestSegment + 180.0;
	IF (pointBearing BETWEEN (bearingClosestSegmentReversed - acceptableRange) AND (bearingClosestSegmentReversed + acceptableRange) ) THEN
		isSimilarBearing := true;
	END IF;	

	--tmp := 'isSimilarBearing=' || isSimilarBearing;
	--RAISE NOTICE 'SP2: %', tmp;
	
    RETURN isSimilarBearing;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_comparebearing(geometry, double precision, geometry, geometry, double precision) OWNER TO postgres;
