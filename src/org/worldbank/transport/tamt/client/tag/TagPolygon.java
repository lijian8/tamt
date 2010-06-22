package org.worldbank.transport.tamt.client.tag;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.PolylineOptions;

public class TagPolygon extends Polygon {

	private String zoneDetailsId;

	public TagPolygon(LatLng[] points) {
		super(points);
	}

	public TagPolygon(LatLng[] points, String polygonColor, int weight,
			double opacity, String fillColor, double fillOpacity) {
		super(points, polygonColor, weight, opacity, fillColor, fillOpacity);
	}

	public void setZoneDetailsId(String zoneDetailsId) {
		this.zoneDetailsId = zoneDetailsId;
	}

	public String getZoneDetailsId() {
		return zoneDetailsId;
	}

}
