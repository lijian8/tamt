package org.worldbank.transport.tamt.shared;

import java.util.Date;

public class TAMTPoint {

	private String id; // the id of this point
	private String gpsTraceId; // parent (owner) id of this point
	private double latitude; // decimal degrees WGS84
	private double longitude;  // decimal degrees WGS84
	private double bearing; // 0 - 360 degrees
	private double speed; // unit is knots. 1 knot = 1.15 mph = 1.852 kph
	private double altitude; // altitude
	private String altitudeUnits; // units for altitude
	private Date timestamp; // full time stamp
	
	public TAMTPoint()
	{
		
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setGpsTraceId(String gpsTraceId) {
		this.gpsTraceId = gpsTraceId;
	}

	public String getGpsTraceId() {
		return gpsTraceId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setAltitudeUnits(String altitudeUnits) {
		this.altitudeUnits = altitudeUnits;
	}

	public String getAltitudeUnits() {
		return altitudeUnits;
	}

	@Override
	public String toString() {
		return "TAMTPoint [" +
				"id=" + id
				+ ", gpsTraceId=" + gpsTraceId 
				+ ", altitude=" + altitude 
				+ ", altitudeUnits=" + altitudeUnits 
				+ ", bearing=" + bearing
				+ ", latitude=" + latitude
				+ ", longitude=" + longitude 
				+ ", speed=" + speed
				+ ", timestamp=" + timestamp + "]";
	}

}
