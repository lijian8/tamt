-- Function: tamt_truncatespeeddistributiontables(text, text, text, text, text)

-- DROP FUNCTION tamt_truncatespeeddistributiontables(text, text, text, text, text);

CREATE OR REPLACE FUNCTION tamt_truncatespeeddistributiontables(sd text, sdo text, sdtf text, sdtftvs text, sdtfvs text)
  RETURNS void AS
$BODY$
DECLARE
	_regionid text;
BEGIN

	-- This changed from TRUNCATE to DELETE all records where the regionid is the current study region
	SELECT INTO _regionid id FROM studyregion WHERE iscurrentregion = TRUE;
	
 	RAISE NOTICE 'START tamt_truncatespeeddistributiontables';
 	IF sd IS NOT NULL
 	THEN
		--truncate table speeddistribution;
		DELETE FROM speeddistribution WHERE tagid IN
			(SELECT id FROM tagdetails WHERE region = _regionid);
 	END IF;

	IF sdo IS NOT NULL
 	THEN
		--truncate table speeddistobserved;
		DELETE FROM speeddistobserved WHERE tagid IN
			(SELECT id FROM tagdetails WHERE region = _regionid);		
 	END IF;

	IF sdtf IS NOT NULL
 	THEN
		--truncate table speeddistributiontrafficflow;
		DELETE FROM speeddistributiontrafficflow WHERE tagid IN
			(SELECT id FROM tagdetails WHERE region = _regionid);			
 	END IF;

	IF sdtftvs IS NOT NULL
 	THEN
		--truncate table speeddistributiontrafficflowtagvehiclespeed;
		DELETE FROM speeddistributiontrafficflowtagvehiclespeed WHERE tagid IN
			(SELECT id FROM tagdetails WHERE region = _regionid);		
 	END IF;

	IF sdtftvs IS NOT NULL
 	THEN
		--truncate table speeddistributiontrafficflowvehiclespeed;
		DELETE FROM speeddistributiontrafficflowtagvehiclespeed WHERE tagid IN
			(SELECT id FROM tagdetails WHERE region = _regionid);			
 	END IF;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_truncatespeeddistributiontables(text, text, text, text, text) OWNER TO gis;
