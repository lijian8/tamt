package org.worldbank.transport.tamt.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Vertex implements Serializable {

	private double lat;
	private double lng;
	
	public Vertex()
	{
		
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "[lat=" + lat + ", lng=" + lng + "]";
	}
	
}
