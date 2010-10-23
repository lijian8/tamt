CREATE OR REPLACE FUNCTION TAMT_convertAndBinSpeeds(_gpstraceid text)
  RETURNS integer AS
$BODY$
DECLARE
	
BEGIN
	-- RMC sentence from GPS offers "Speed over the ground in knots"
	-- knot = nautical mile per hour, therefore 
	-- for every record in gpspoints, multiply speed by 1.85200 to 
	-- convert knots to kilometers per hour
	-- insert this value into kph column

	-- then for binning, divide kph by 5 and get a mod/round
	-- e.g. 0-4.99 kph = bin 0
	-- 5-9.99 kph = bin 1
	-- 55.6 kph = 55.6/5 = 11.12, take the floor to get bin 11 e.g. floor(kph/5)
	UPDATE gpspoints 
	SET 
		kph = speed * 1.85200,
		speedbinnumber = floor(speed * 1.85200 / 5)
	WHERE gpstraceid = _gpstraceid;

        RETURN 1;
END;
$BODY$
  LANGUAGE 'plpgsql';
ALTER FUNCTION TAMT_convertAndBinSpeeds(gpstraceid text) OWNER TO gis;