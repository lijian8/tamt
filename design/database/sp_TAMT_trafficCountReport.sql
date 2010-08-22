-- Function: TAMT_trafficCountReport(text)

-- DROP FUNCTION TAMT_trafficCountReport(text);

CREATE OR REPLACE FUNCTION TAMT_trafficCountReport(_tagid text)
  RETURNS SETOF RECORD AS
$BODY$
DECLARE
	my_record RECORD;
	sql text;
	num_rows integer;
	tmp_data RECORD;
	my_hour integer;
	tmp text;
BEGIN

	-- call the TAMT_trafficCountHourlyAverage stored procedure as a result table
	CREATE TABLE tmp_hrly AS 
	SELECT * FROM TAMT_trafficCountHourlyAverage(_tagid) AS 
	foo(
		id text,
		tagid text, 
		hour_bucket float,
		w2_hrly float, 
		w3_hrly float, 
		pc_hrly float, 
		tx_hrly float, 
		ldv_hrly float, 
		ldc_hrly float, 
		hdc_hrly float, 
		mdb_hrly float, 
		hdb_hrly float
	);
	
	my_hour := 0;
	WHILE ( my_hour < 24 ) LOOP

		num_rows := 0;
		SELECT INTO num_rows count(*) 
			FROM tmp_hrly WHERE hour_bucket = my_hour;
	
		SELECT INTO my_record
			my_hour as hour_slice,
			round((SUM(w2_hrly)/num_rows)::numeric, 2) as w2total,
			round((SUM(w3_hrly)/num_rows)::numeric, 2) as w3total,
			round((SUM(pc_hrly)/num_rows)::numeric, 2) as pctotal,
			round((SUM(tx_hrly)/num_rows)::numeric, 2) as txtotal,
			round((SUM(ldv_hrly)/num_rows)::numeric, 2) as ldvtotal,
			round((SUM(ldc_hrly)/num_rows)::numeric, 2) as ldctotal,
			round((SUM(hdc_hrly)/num_rows)::numeric, 2) as hdctotal,
			round((SUM(mdb_hrly)/num_rows)::numeric, 2) as mdbtotal,
			round((SUM(hdb_hrly)/num_rows)::numeric, 2) as hdbtotal
		FROM 
			tmp_hrly
		WHERE 
			tagid = _tagid
		AND 
			hour_bucket = my_hour;

		my_hour := my_hour + 1;

		RETURN NEXT my_record;
		
	END LOOP;

	DROP TABLE tmp_hrly;
	
	RETURN;
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_trafficCountReport(_tagid text) OWNER TO postgres;
