-- Function: TAMT_deleteAnalysisAndSupportRecords(text)

-- DROP FUNCTION TAMT_deleteAnalysisAndSupportRecords(text);

CREATE OR REPLACE FUNCTION TAMT_deleteAnalysisAndSupportRecords(_regionIdToDelete text)
  RETURNS void AS
$BODY$
DECLARE
	
BEGIN 	
		DELETE FROM daytypeperyearoption WHERE regionid = _regionIdToDelete;

		DELETE FROM defaulttrafficflow WHERE regionid = _regionIdToDelete;

		DELETE FROM soakbin WHERE regionid = _regionIdToDelete;

		DELETE FROM speeddistobserved WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);

		DELETE FROM speeddistributiontrafficflow WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);

		DELETE FROM speeddistributiontrafficflowtagvehiclespeed WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);

		DELETE FROM trafficcount WHERE regionid = _regionIdToDelete;

		DELETE FROM trafficflowreport WHERE regionid = _regionIdToDelete;

		DELETE FROM tripbin WHERE regionid = _regionIdToDelete;

		DELETE FROM trips WHERE regionid = _regionIdToDelete;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_deleteAnalysisAndSupportRecords(text) OWNER TO gis;
