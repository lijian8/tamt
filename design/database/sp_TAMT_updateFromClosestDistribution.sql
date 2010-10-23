-- DROP FUNCTION TAMT_getClosestDistribution(_tagId text, _dayType text, _totalFlow double precision);

CREATE OR REPLACE FUNCTION TAMT_updateFromClosestDistribution(_tagId text, _dayType text, _hourBin integer, _totalFlow double precision, _percentRange double precision)
  RETURNS SETOF RECORD AS
$BODY$
DECLARE
	closest RECORD;
	rowsToCopy speeddistribution%rowtype;
	rowToUpdate RECORD;
	tmp text;
BEGIN

	-- RAISE NOTICE 'Start TAMT_updateFromClosestDistribution';
	-- RAISE NOTICE 'Query with daytype and threshold';

	tmp := '_tagId('||_tagId||'), _dayType('||_dayType||'), _hourBin('||_hourBin||')';
	-- RAISE NOTICE 'Incoming parameters: %', tmp;
	
	SELECT INTO closest 
		*,
		ABS(totalflow - _totalFlow) as diff 
	FROM 
		speeddistobserved
	WHERE totalflow >= (_totalFlow - (_percentRange * _totalFlow))
	AND totalflow <= (_totalFlow + (_percentRange * _totalFlow))
	AND tagid = _tagId
	AND daytype = _dayType
	AND isobserved = true
	ORDER BY diff ASC LIMIT 1;

	-- RAISE NOTICE 'Closest after first pass: %', closest;

	-- if my_record is empty, try again without daytype or threshold
	IF closest IS NULL
	THEN
		-- RAISE NOTICE 'No match.';
		-- RAISE NOTICE 'Requery without daytype or threshold';
		SELECT INTO closest 
			*,
			ABS(totalflow - _totalFlow) as diff 
		FROM 
			speeddistobserved
		WHERE tagid = _tagId
		AND isobserved = true
		ORDER BY diff ASC LIMIT 1;			
	ELSE 
		
	END IF;
	
	FOR rowsToCopy IN 
		SELECT * FROM speeddistribution
			WHERE tagid = closest.tagid
			AND daytype = closest.daytype
			AND hourbin = closest.hourbin
	LOOP
		-- tmp := 'tagid=,'|| rowsToCopy.tagid ||' daytype=,'|| rowsToCopy.daytype ||' hourbin='|| rowsToCopy.hourbin ||' secondsinbin=' || rowsToCopy.secondsinbin ;
		-- RAISE NOTICE 'Do stuff here with rowsToCopy: %', tmp;
		-- tmp := 'Update tag(' || _tagId || '), daytype(' || _dayType || '), hourbin(' || _hourBin || ') with rowToCopy data, like secondsinbin(' || rowsToCopy.secondsinbin || ')'; 
		-- RAISE NOTICE 'rowsToCopy: %', rowsToCopy;
		
		UPDATE speeddistribution SET
			secondsinbin = rowsToCopy.secondsinbin,
			metersinbin = rowsToCopy.metersinbin,
			avgmeterspersecond = rowsToCopy.avgmeterspersecond,
			percentsecondsinbin = rowsToCopy.percentsecondsinbin,
			percentmetersinbin = rowsToCopy.percentmetersinbin
			FROM speeddistribution s2
			WHERE speeddistribution.tagid = _tagId
			AND speeddistribution.daytype = _dayType
			AND speeddistribution.hourbin = _hourBin
			AND speeddistribution.speedbin = rowsToCopy.speedbin;
	END LOOP;
	
	
	-- RAISE NOTICE 'End TAMT_updateFromClosestDistribution';
	
	RETURN NEXT closest;

END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_updateFromClosestDistribution(_tagId text, _dayType text, _hourBin integer, _totalFlow double precision, _percentRange double precision) OWNER TO gis;