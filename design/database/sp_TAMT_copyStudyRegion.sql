-- Function:TAMT_copyStudyRegion(text, text))

-- DROP FUNCTION TAMT_copyStudyRegion(text, text)

CREATE OR REPLACE FUNCTION TAMT_copyStudyRegion(_regionIdToCopy text, _newName text)
  RETURNS void AS
$BODY$
DECLARE
	_studyregionRecord RECORD;
	_tagDetailsRecord RECORD;
	_roadDetailsRecord RECORD;
	_zoneDetailsRecord RECORD;
	_dayTypePerYearOptionRecord RECORD;
BEGIN

 	--RAISE NOTICE 'START TAMT_copyStudyRegion';
	--RAISE NOTICE 'Copy _regionIdToCopy(%) to _newName(%)', _regionIdToCopy, _newName;

	-- Step 1: Update the studyregion table
	SELECT INTO _studyregionRecord * FROM studyregion WHERE id = _regionIdToCopy;
	_studyregionRecord.pid = (SELECT nextval('studyregion_pid_seq'));
	_studyregionRecord.name = _newName;
	_studyregionRecord.id = (SELECT _generate_uuid_v4());
	_studyregionRecord.iscurrentregion = false;
	INSERT INTO studyregion (pid, id, name, description, geometry, 
				mapzoomlevel, mapcenter, iscurrentregion,
				default_zone_type, utcoffset, minsoakinterval,
				commercial_block_length, industrial_block_length, residential_block_length)
		VALUES (
			_studyregionRecord.pid,
			_studyregionRecord.id,
			_studyregionRecord.name,
			_studyregionRecord.description,
			_studyregionRecord.geometry,
			_studyregionRecord.mapzoomlevel,
			_studyregionRecord.mapcenter,
			_studyregionRecord.iscurrentregion,
			_studyregionRecord.default_zone_type,
			_studyregionRecord.utcoffset,
			_studyregionRecord.minsoakinterval,
			_studyregionRecord.commercial_block_length,
			_studyregionRecord.industrial_block_length,
			_studyregionRecord.residential_block_length
		);

	-- Step 2: Update the tagdetails table
	FOR _tagDetailsRecord IN SELECT * FROM tagDetails WHERE region = _regionIdToCopy
	LOOP
		--RAISE NOTICE '_tagDetailsRecord=%',_tagDetailsRecord;
		INSERT INTO tagDetails (id, name, description, region, original_tag_id)
			VALUES (
				(SELECT _generate_uuid_v4()),
				_tagDetailsRecord.name,
				_tagDetailsRecord.description,
				_studyregionRecord.id,
				_tagDetailsRecord.id -- the original tag id (for use in later relations)
			);
	END LOOP;
		
	
	-- Step 3: Update the roaddetails table
	FOR _roadDetailsRecord IN SELECT * FROM roaddetails WHERE region = _regionIdToCopy
	LOOP
		--RAISE NOTICE '_roadDetailsRecord=%',_roadDetailsRecord;
		INSERT INTO roaddetails (pid, id, "name", description, region, tag_id, geometry, centroid)
			VALUES (
				(SELECT nextval('roaddetails_pid_seq')),
				(SELECT _generate_uuid_v4()),
				_roadDetailsRecord.name,
				_roadDetailsRecord.description,
				_studyregionRecord.id,
				-- the update of the tag_id has to refer to the id of the newly copied tag
				(SELECT id FROM tagdetails WHERE original_tag_id = _roadDetailsRecord.tag_id),
				_roadDetailsRecord.geometry,
				_roadDetailsRecord.centroid
			);
	END LOOP;	

	-- Step 4: Update the zonedetails table
	FOR _zoneDetailsRecord IN SELECT * FROM zonedetails WHERE region = _regionIdToCopy
	LOOP
		--RAISE NOTICE '_zoneDetailsRecord=%',_zoneDetailsRecord;
		INSERT INTO zonedetails (pid, id, name, description, region, zonetype, geometry)
			VALUES (
				(SELECT nextval('zonedetails_pid_seq')),
				(SELECT _generate_uuid_v4()),
				_zoneDetailsRecord.name,
				_zoneDetailsRecord.description,
				_studyregionRecord.id,
				_zoneDetailsRecord.zonetype,
				_zoneDetailsRecord.geometry
			);
	END LOOP;

	-- Step 5: Also add a default OPTION001 record to the dayTypePerYearOption table
	INSERT INTO daytypeperyearoption (id, regionid, activeoption, option1weekday)
		VALUES (
			(SELECT _generate_uuid_v4()),
			_studyregionRecord.id,
			1,
			260
		);	

	-- Now clean out our markers so we can copy again without duplication issues
	UPDATE tagdetails SET original_tag_id = NULL;
	
 	--RAISE NOTICE 'END TAMT_copyStudyRegion';
 	
 	
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION TAMT_copyStudyRegion(text, text) OWNER TO gis;
