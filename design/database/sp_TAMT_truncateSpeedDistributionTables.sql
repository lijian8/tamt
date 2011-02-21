-- Function: tamt_truncatespeeddistributiontables()

-- DROP FUNCTION tamt_truncatespeeddistributiontables();

CREATE OR REPLACE FUNCTION tamt_truncatespeeddistributiontables(sd text, sdo text, sdtf text, sdtftvs text, sdtfvs text)
  RETURNS void AS
$BODY$
DECLARE
	
BEGIN

 	RAISE NOTICE 'START tamt_truncatespeeddistributiontables';
 	IF sd IS NOT NULL
 	THEN
		truncate table speeddistribution;
 	END IF;

	IF sdo IS NOT NULL
 	THEN
		truncate table speeddistobserved;
 	END IF;

	IF sdtf IS NOT NULL
 	THEN
		truncate table speeddistributiontrafficflow;
 	END IF;

	IF sdtftvs IS NOT NULL
 	THEN
		truncate table speeddistributiontrafficflowtagvehiclespeed;
 	END IF;

	IF sdtftvs IS NOT NULL
 	THEN
		truncate table speeddistributiontrafficflowvehiclespeed;
 	END IF;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION tamt_truncatespeeddistributiontables(sd text, sdo text, sdtf text, sdtftvs text, sdtfvs text) OWNER TO gis;
