package org.worldbank.transport.tamt.shared;

import java.util.Date;

public class GPRMCRecord {
	
/*

http://scientificcomponent.com/nmea0183.htm

$GPRMC,aaaaaa,b,cccc.cc,d,eeeee.ee,f,ggg.g,hhh.h,jjjjjj,kkk.k,l*mm

Where:

aaaaaa is the time of the fix UTC in hhmmss format
b is the validity of the fix ("A" = valid, "V" = invalid)
cccc.cc is the current latitude in ddmm.mm format
d is the latitude hemisphere ("N" = northern, "S" = southern)
eeeee.ee is the current longitude in dddmm.mm format
f is the longitude hemisphere ("E" = eastern, "W" = western)
ggg.g is the speed in knots
hhh.h is the true course in degrees
jjjjjj is the date in DDMMYY format
kkk.k is the magnetic variation in degrees
l is the direction of magnetic variation ("E" = east, "W" = west)
mm is the checksum
 */
	
	
	/*
eg4. $GPRMC,hhmmss.ss,A,llll.ll,a,yyyyy.yy,a,x.x,x.x,ddmmyy,x.x,a*hh
1    = UTC of position fix
2    = Data status (V=navigation receiver warning)
3    = Latitude of fix
4    = N or S
5    = Longitude of fix
6    = E or W
7    = Speed over ground in knots
8    = Track made good in degrees True
9    = UT date
10   = Magnetic variation degrees (Easterly var. subtracts from true course)
11   = E or W
12   = Checksum
	 */
	private double utcPositionFix;
	private String status;
	private double latitude;
	private String latitudeCardinalDirection;
	private double longitude;
	private String longitudeCardinalDirection;
	private double groundSpeedKnots;
	private double bearing; // degrees
	private Date utDate;
	private double degreesVariation;
	//private String eastOrWest; // field 11 is mostly null
	private String checkSum;
	
	public GPRMCRecord()
	{
		
	}

	public double getUtcPositionFix() {
		return utcPositionFix;
	}

	public void setUtcPositionFix(double utcPositionFix) {
		this.utcPositionFix = utcPositionFix;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLatitudeCardinalDirection() {
		return latitudeCardinalDirection;
	}

	public void setLatitudeCardinalDirection(String latitudeCardinalDirection) {
		this.latitudeCardinalDirection = latitudeCardinalDirection;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLongitudeCardinalDirection() {
		return longitudeCardinalDirection;
	}

	public void setLongitudeCardinalDirection(String longitudeCardinalDirection) {
		this.longitudeCardinalDirection = longitudeCardinalDirection;
	}

	public double getGroundSpeedKnots() {
		return groundSpeedKnots;
	}

	public void setGroundSpeedKnots(double groundSpeedKnots) {
		this.groundSpeedKnots = groundSpeedKnots;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public Date getUtDate() {
		return utDate;
	}

	public void setUtDate(Date utDate) {
		this.utDate = utDate;
	}

	public double getDegreesVariation() {
		return degreesVariation;
	}

	public void setDegreesVariation(double degreesVariation) {
		this.degreesVariation = degreesVariation;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	@Override
	public String toString() {
		return "GPRMCRecord [bearing=" + bearing + ", checkSum=" + checkSum
				+ ", degreesVariation=" + degreesVariation
				+ ", groundSpeedKnots=" + groundSpeedKnots + ", latitude="
				+ latitude + ", latitudeCardinalDirection="
				+ latitudeCardinalDirection + ", longitude=" + longitude
				+ ", longitudeCardinalDirection=" + longitudeCardinalDirection
				+ ", status=" + status + ", utDate=" + utDate
				+ ", utcPositionFix=" + utcPositionFix + "]";
	}
	
}
