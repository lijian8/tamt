-- DROP FUNCTION TAMT_getClosestDistribution(_tagId text, _dayType text, _totalFlow double precision);

CREATE OR REPLACE FUNCTION TAMT_getClosestDistribution(_tagId text, _dayType text, _totalFlow double precision, _percentRange double precision)
  RETURNS SETOF RECORD AS
$BODY$
DECLARE
	my_record RECORD;
BEGIN

	-- RAISE NOTICE 'Query with daytype and threshold';
		
	SELECT INTO my_record 
		*,
		(totalflow - _totalFlow) as diff 
	FROM 
		speeddistobserved
	WHERE totalflow >= (_totalFlow - (_percentRange * _totalFlow))
	AND totalflow <= (_totalFlow + (_percentRange * _totalFlow))
	AND tagid = _tagId
	AND daytype = _dayType
	AND isobserved = true
	ORDER BY diff ASC LIMIT 1;

	-- if my_record is empty, try again without daytype or threshold
	IF my_record IS NULL
	THEN
		-- RAISE NOTICE 'No match.';
		-- RAISE NOTICE 'Requery without daytype or threshold';
		SELECT INTO my_record 
			*,
			(totalflow - _totalFlow) as diff 
		FROM 
			speeddistobserved
		WHERE tagid = _tagId
		AND isobserved = true
		ORDER BY diff ASC LIMIT 1;			
	ELSE 
		
	END IF;
	
	RETURN NEXT my_record;

END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_getClosestDistribution(_tagId text, _dayType text, _totalFlow double precision, _percentRange double precision) OWNER TO gis;