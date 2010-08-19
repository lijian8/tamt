-- Function: trafficcountreportshort(text, text, text)

-- DROP FUNCTION trafficcountreportshort(text, text, text);

CREATE OR REPLACE FUNCTION trafficcountreportshort(_tag text, _regionid text, _daytype text)
  RETURNS SETOF record AS
$BODY$
DECLARE
	hours integer;
	start_hour integer;
	end_hour integer;
	my_record RECORD;
BEGIN
	 hours := 0;
	 start_hour := 0;
	 end_hour := 0;
	 -- for each hour of the day, in 24hr time
	 WHILE ( hours < 24 ) LOOP
		-- set up (or reset) the time ranges
		start_hour := hours;
		end_hour := hours + 1;
		-- add up all the traffic counts
		SELECT INTO my_record
			hours as hour,
			SUM(t1.W2) +
			SUM(t1.W3) +
			SUM(t1.PC) +
			SUM(t1.TX) +
			SUM(t1.LDV) +
			SUM(t1.LDC) +
			SUM(t1.HDC) +
			SUM(t1.MDB) +
			SUM(t1.HDB) as t

			FROM 
				trafficcount t1,
				tagdetails t2
			-- that match the incoming tag
			WHERE 
				t1.tagid = t2.id
			AND
				t2.name = _tag
			-- and match the incoming region
			AND t1.regionid = _regionid
			-- and match the incoming day type
			AND t1.daytype = _daytype
			-- and only those with starttimes between the time ranges
			-- e.g. between 9 and 10 
			-- (date_part extracts the hour as an integer from starttime)
			AND date_part('hour', t1.starttime) = start_hour;
			
		RETURN NEXT my_record;
		hours := hours + 1;
		
	 END LOOP;
	 RETURN;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trafficcountreportshort(text, text, text) OWNER TO postgres;