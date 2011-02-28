-- Function: tamt_updatefromclosestdistribution(text, text, integer, double precision, double precision)

-- DROP FUNCTION tamt_updatefromclosestdistribution(text, text, integer, double precision, double precision);

CREATE OR REPLACE FUNCTION TAMT_populateEngineSoakTimeAndTripLengthForCurrentRegion()
  RETURNS SETOF record AS
$BODY$
DECLARE
	
	_regionid text;
	_minSoakInterval interval;
	minSpeedThreshold double precision;
	gpstrace RECORD;
	logs RECORD;
	r RECORD;
	
	startTrip boolean;
	startTripRecord RECORD;
	
	lastTrip boolean;
	lastTripRecord RECORD;

	endTrip timestamp;

	startSoak boolean;
	startSoakRecord RECORD;

	soakTime interval;
	tripLength double precision;
	tripGeometry geometry;

	tripAverageSpeed double precision;
	elapsedTime interval;
	elapsedTimeDoublePrecision double precision;

	binCandidate interval;
	soakBinNumber integer;
	tripBinNumber integer;

	lastMoving boolean;
	diffFromLastTrip interval;
	currentAverageSpeedInBin double precision;
	currentBinCount integer;
BEGIN

	-- we should only be operating on the current study region
	SELECT INTO _regionid id FROM studyregion WHERE iscurrentregion = TRUE;

	-- retrieve minimum soak interval for this region (this value is in seconds)
	SELECT INTO _minSoakInterval minSoakInterval FROM studyregion WHERE id = _regionid;

	-- reset the soakbin table for this region
	DELETE FROM soakbin WHERE regionid = _regionid;
	FOR i IN 0..9 LOOP
		INSERT INTO soakbin (regionid, binnumber, bincount) VALUES (_regionid,i,0);
	END LOOP;

	-- reset the tripbin table for this region
	DELETE FROM tripbin WHERE regionid = _regionid;
	
	-- reset the trips table for this region
	DELETE FROM trips WHERE regionid = _regionid;
	
	-- GPS devices are not reliable to report 0 as actual speed, so we use a threshold of
	-- 0.25kph = 0.0694444444 meters/second
	-- 0.5kph = 0.138888889
	minSpeedThreshold := 0.0694444444;
	
	-- We need to inspect every GPS record in the study region, 
	-- but must do so log by log (which gives us unique vehicles)

	FOR gpstrace IN SELECT
		DISTINCT id
		FROM gpstraces
		WHERE region = _regionid
		ORDER BY id
	LOOP
		--RAISE NOTICE 'gpstrace=%',gpstrace;
		 
		FOR logs IN SELECT 
			DISTINCT gpslogid 
			FROM gpspoints 
			WHERE gpstraceid = gpstrace.id  
			ORDER BY gpslogid
		LOOP
			--RAISE NOTICE 'logs=%',logs;
			
			startTrip := FALSE;
			lastTrip := FALSE;
			startSoak := FALSE;
			lastMoving := FALSE;
			tripLength := NULL;		

			-- For each GPS log, we need to inspect every GPS record, 
			-- in order by the timestamp they were recorded (ie created)
			FOR r IN SELECT * FROM gpspoints 
				WHERE gpslogid = logs.gpslogid
				ORDER BY created
			LOOP
				--RAISE NOTICE 'r=%',r;
				IF startTrip IS FALSE
				THEN

					-- We are only looking at moving vehicles for now
					IF r.speed > minSpeedThreshold
					THEN
						--RAISE NOTICE '*************************************************';
						
						-- Calculate trip length
						IF endTrip IS NOT NULL
						THEN
							-- Calculate trip length
							--RAISE NOTICE 'CALCULATE tripLength from startTripRecord(%, %) to lastTripRecord(%,%)', startTripRecord.pid, startTripRecord.created, lastTripRecord.pid, lastTripRecord.created;
							-- Let's use POSTGIS to calculate trip length
							-- 1. select all records from startTripRecord.pid to lastTripRecord.pid	(in PID order)
							-- 2. inject each point geometry from the resulting rows into a line
							-- 3. ask postgis for the length of the line (and watch units)
							SELECT INTO tripLength st_length(st_makeline(st_transform(geometry,900913))) FROM gpspoints where pid between startTripRecord.pid and lastTripRecord.pid;
							SELECT INTO tripGeometry st_makeline(st_transform(geometry,900913)) FROM gpspoints where pid between startTripRecord.pid and lastTripRecord.pid;
							-- calculate average speed
							elapsedTime = lastTripRecord.created - startTripRecord.created;
							-- some trips are only 1 second, so elapsedTime may be 0, but we can't divide by zero
							SELECT INTO elapsedTimeDoublePrecision extract(epoch FROM elapsedTime);
							IF elapsedTimeDoublePrecision = 0
							THEN
								elapsedTimeDoublePrecision = 1;
							END IF;
							tripAverageSpeed = tripLength / elapsedTimeDoublePrecision;
			
			
							--RAISE NOTICE 'tripLength(m)=%, elapsedTime(s)=%, tripAverageSpeed(m/s)=%, verify(kph)=%', tripLength, elapsedTime, tripAverageSpeed, (tripAverageSpeed * 3.6);
							--RAISE NOTICE '*************************************************';

							-- Determine which bin the trip length goes in
							-- TODO: insert row into trip bin
							-- Trip length bins
							-- 0	0.0-0.5 km
							-- 1	0.51 - 1 km
							-- 2	1.1 - 2 km
							-- 3	2.1 - 5 km
							-- 4	5.1 - 10 km
							-- 5	10.1 - 15 km
							-- 6	15.1 - 25 km
							-- 7	25.1 - 50 km
							-- 8	50.1 - 100km
							-- 9	over 100 km	
							-- NOTE: tripLength is in meters
							IF tripLength > 0
							THEN
								IF tripLength > 100000
								THEN
									tripBinNumber = 9;
								ELSIF tripLength > 50000
								THEN
									tripBinNumber = 8;
								ELSIF tripLength > 25000
								THEN
									tripBinNumber = 7;
								ELSIF tripLength > 15000
								THEN
									tripBinNumber = 6;
								ELSIF tripLength > 10000
								THEN
									tripBinNumber = 5;
								ELSIF tripLength > 5000
								THEN
									tripBinNumber = 4;
								ELSIF tripLength > 2000
								THEN
									tripBinNumber = 3;
								ELSIF tripLength > 1000
								THEN
									tripBinNumber = 2;
								ELSIF tripLength > 500
								THEN
									tripBinNumber = 1;
								ELSE
									tripBinNumber = 0;
								END IF;

								-- keep a record in trips table;
								INSERT INTO trips (regionid, gpstraceid, gpslogid, bin_number, trip_length, avg_speed, geometry)
									VALUES (_regionid, gpstrace.id, logs.gpslogid, tripBinNumber, tripLength, tripAverageSpeed, tripGeometry);				
							ELSE 
								-- ignore binCandidate if it is less than 0	
							END IF;										
							
						END IF;
										
						-- Starting a trip
						startTrip := TRUE;
						startTripRecord := r;
						--RAISE NOTICE 'CREATE startTripRecord(pid, created)=(%,%)', startTripRecord.pid, startTripRecord.created;

						-- Initializing lastTrip if needed
						IF lastTrip IS FALSE
						THEN
							lastTrip := TRUE;
							lastTripRecord := r;
							--RAISE NOTICE 'CREATE lastTripRecord(pid, created)=(%,%)', lastTripRecord.pid, lastTripRecord.created;
						END IF;	


								

						-- calculate the soak time
						IF startSoak IS TRUE
						THEN
							--RAISE NOTICE 'CALCULATE soakTime from startSoakRecord(pid, created)=(%,%) to r(pid, created)=(%,%)', startSoakRecord.pid, startSoakRecord.created, r.pid, r.created;
							soakTime = r.created - startSoakRecord.created;

							-- Is the soak time greater than the minimum soak interval?
							IF soakTime > _minSoakInterval
							THEN
								--RAISE NOTICE 'soakTime(%) greater than _minSoakInterval(%), update soakbin table', soakTime, _minSoakInterval;
								
								soakBinNumber := NULL;
								binCandidate = soakTime - _minSoakInterval;
								IF binCandidate > INTERVAL '0 seconds'
								THEN
									IF binCandidate > INTERVAL '720 minutes'
									THEN
										soakBinNumber = 9;
									ELSIF binCandidate > INTERVAL '480 minutes'
									THEN
										soakBinNumber = 8;
									ELSIF binCandidate > INTERVAL '360 minutes'
									THEN
										soakBinNumber = 7;
									ELSIF binCandidate > INTERVAL '240 minutes'
									THEN
										soakBinNumber = 6;
									ELSIF binCandidate > INTERVAL '180 minutes'
									THEN
										soakBinNumber = 5;
									ELSIF binCandidate > INTERVAL '120 minutes'
									THEN
										soakBinNumber = 4;
									ELSIF binCandidate > INTERVAL '60 minutes'
									THEN
										soakBinNumber = 3;
									ELSIF binCandidate > INTERVAL '30 minutes'
									THEN
										soakBinNumber = 2;
									ELSIF binCandidate > INTERVAL '15 minutes'
									THEN
										soakBinNumber = 1;
									ELSE
										soakBinNumber = 0;
									END IF;

									--RAISE NOTICE 'UPDATE soakbin at soakBinNumber(%)', soakBinNumber;
								
									-- increment the soak bin number
									UPDATE soakbin SET bincount = bincount + 1
										WHERE binNumber = soakBinNumber
										AND regionid = _regionId;					
								ELSE 
									-- ignore binCandidate if it is less than 0	
								END IF;
								-- reset startSoak for next trip
								startSoak := FALSE;
								startSoakRecord := NULL;
								soakTime := NULL;
								
							END IF;
							
						END IF;

						
					END IF;
				ELSE
					-- If we are in a trip but the speed indicates we are not moving
					-- then keep the FIRST of these non-moving records as a possible
					-- starting time for an engine soak
					IF r.speed <= minSpeedThreshold
					THEN

						-- Keep the first of the non-moving records during a trip as the startSoakRecord
						--RAISE NOTICE 'POSSIBLE SOAK (this record not moving): r.pid=%, r.created=%, lastMoving=%, last(pid,created,speed)=(%,%,%)', r.pid, r.created, lastMoving, lastRecord.pid, lastRecord.created, lastRecord.speed;
						--RAISE NOTICE 'POSSIBLE SOAK (this record not moving): r.pid=%, r.created=%, lastMoving=%', r.pid, r.created, lastMoving;
						IF startSoak IS FALSE
						THEN
							startSoakRecord = r;
							startSoak = TRUE;
							--RAISE NOTICE 'startSoakRecord pid=%', startSoakRecord.pid;
						END IF;

						-- Determine if we have a trip end based on exceeding the soak interval
						-- Note: If we contiguously not been moving for longer than
						-- the minimum soak interval, then we capture an endTrip
						-- and reset startTrip to FALSE, so it can be captured
						-- on the next loop
						IF NOT lastMoving
						THEN

							diffFromLastTrip = r.created - lastTripRecord.created;
							IF diffFromLastTrip > _minSoakInterval
							THEN
								endTrip := r.created;
								startTrip := FALSE;
								--RAISE NOTICE 'ENDTRIP DUE TO SOAK=(%,%), reset start trip to FALSE' , r.pid, endTrip;						
							END IF;						
						END IF;
					ELSE
						-- Determine if we have a trip end:
						-- If the vehicle was NOT moving in the last record,
						-- Then get the timestamp of the last trip record
						-- And subtract the timestamp of this record
						-- If the result is greater than the _minSoakInterval,
						-- We have an endTrip
						IF NOT lastMoving
						THEN
							
							diffFromLastTrip = r.created - lastTripRecord.created;
							--RAISE NOTICE 'TESTING FOR ENDTRIP at r.pid(%): diffFromLastTrip=%', r.pid, diffFromLastTrip;
							IF diffFromLastTrip > _minSoakInterval
							THEN
								--RAISE NOTICE 'END TRIP: diffFromLastTrip=%' , diffFromLastTrip;
								endTrip := r.created;
								startTrip := FALSE;
								--RAISE NOTICE 'ENDTRIP=(%,%), reset start trip to NULL' , r.pid, endTrip;
								
							ELSE
								-- ignore, this is not an endTrip
								--RAISE NOTICE 'NO ENDTRIP at (%), CONTINUE TRIP: stopped for total(%), which is less than _minSoakInterval of(%), continue trip from (%)', r.pid, diffFromLastTrip, _minSoakInterval, startTrip;
								endTrip := NULL;
								-- reset soak variables
								startSoak := FALSE;
								startSoakRecord := NULL;
							END IF;								
						END IF;
						lastTrip := FALSE;
						lastTripRecord = r;
					END IF;
				END IF;

				--lastTimeStamp = r.created;
				--lastSpeed = r.speed;
				--lastPid = r.pid;
				IF r.speed > minSpeedThreshold
				THEN
					lastMoving = TRUE;
				ELSE 
					lastMoving = FALSE;
				END IF;
				--lastRecord = r;
				--RAISE NOTICE 'GO TO NEXT RECORD';
				
			END LOOP;
		END LOOP;
	END LOOP;

	-- now use trips to populate tripbin
	INSERT INTO 
		tripbin (regionid, binnumber, bincount, avgtriplength, avgspeed)
		(SELECT 
			t.regionid,
			t.bin_number binnumber, 
			count(*) as bincount, 
			AVG(t.trip_length) avgTripLength, 
			AVG(t.avg_speed) avgSpeed 
			FROM trips as t
			WHERE regionid = _regionid
			GROUP BY regionid, bin_number
			ORDER BY bin_number);

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION TAMT_populateEngineSoakTimeAndTripLengthForCurrentRegion() OWNER TO gis;

