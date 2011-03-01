-- Function: tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed()

-- DROP FUNCTION tamt_reducetagfromspeeddistributiontrafficflowtagvehiclespeed();

CREATE OR REPLACE FUNCTION TAMT_deleteAnalysisAndSupportRecords(_regionIdToDelete text)
  RETURNS void AS
$BODY$
DECLARE
	
BEGIN 	
-- 		 * - daytypeperyearoption (by regionid)
		DELETE FROM daytypeperyearoption WHERE regionid = _regionIdToDelete;
		 
-- 		 * - defaulttrafficflow (by regionid)
		DELETE FROM defaulttrafficflow WHERE regionid = _regionIdToDelete;
-- 		 * - soakbin (by regionid)
		DELETE FROM soakbin WHERE regionid = _regionIdToDelete;
-- 		 * - speeddistobserved (by tag)
		DELETE FROM speeddistobserved WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);
-- 		 * - speeddistributiontrafficflow (by tag)
		DELETE FROM speeddistributiontrafficflow WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);
-- 		 * - speeddistributiontrafficflowtagvehiclespeed (by tag)
		DELETE FROM speeddistributiontrafficflowtagvehiclespeed WHERE tagid IN (SELECT id FROM tagdetails WHERE region = _regionIdToDelete);
-- 		 * - trafficcount (by regionid)
		DELETE FROM trafficcount WHERE regionid = _regionIdToDelete;
-- 		 * - trafficflowreport (by regionid)
		DELETE FROM trafficflowreport WHERE regionid = _regionIdToDelete;
-- 		 * - tripbin (by regionid)
		DELETE FROM tripbin WHERE regionid = _regionIdToDelete;
-- 		 * - trips (by regionid) 	
		DELETE FROM trips WHERE regionid = _regionIdToDelete;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_deleteAnalysisAndSupportRecords(text) OWNER TO gis;
