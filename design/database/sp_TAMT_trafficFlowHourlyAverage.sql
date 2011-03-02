-- Function: tamt_trafficflowhourlyaverage(text, text)

-- DROP FUNCTION tamt_trafficflowhourlyaverage(text, text);

CREATE OR REPLACE FUNCTION tamt_trafficflowhourlyaverage(_tagid text, _daytype text)
  RETURNS SETOF record AS
$BODY$
DECLARE
	my_record RECORD;
BEGIN
	FOR my_record IN SELECT
		id,
		_tagid,
		_daytype,
		date_part('hour', (starttime)) as hour,
		w2 * 3600/(extract(epoch from (endtime - starttime))) as w2avg,
		w3 * 3600/(extract(epoch from (endtime - starttime))) as w3avg,
		pc * 3600/(extract(epoch from (endtime - starttime))) as pcavg,
		tx * 3600/(extract(epoch from (endtime - starttime))) as txavg,
		ldv * 3600/(extract(epoch from (endtime - starttime))) as ldvavg,
		ldc * 3600/(extract(epoch from (endtime - starttime))) as ldcavg,
		hdc * 3600/(extract(epoch from (endtime - starttime))) as hdcavg,
		mdb * 3600/(extract(epoch from (endtime - starttime))) as mdbavg,
		hdb * 3600/(extract(epoch from (endtime - starttime))) as hdbavg
		
	FROM
		trafficcount
	WHERE
		tagid = _tagid
	AND
		daytype = _daytype
	LOOP
		RETURN NEXT my_record;
	END LOOP;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION tamt_trafficflowhourlyaverage(text, text) OWNER TO postgres;
