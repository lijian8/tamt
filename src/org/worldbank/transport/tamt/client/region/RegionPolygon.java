package org.worldbank.transport.tamt.client.region;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.PolylineOptions;

public class RegionPolygon extends Polygon {

	private String regionDetailsId;
	private String regionName;
	private Vertex mapCenter;
	private int mapZoomLevel;

	public RegionPolygon(LatLng[] points) {
		super(points);
	}

	public RegionPolygon(LatLng[] points, String polygonColor, int weight,
			double opacity, String fillColor, double fillOpacity) {
		super(points, polygonColor, weight, opacity, fillColor, fillOpacity);
	}

	public void setRegionDetailsId(String regionDetailsId) {
		this.regionDetailsId = regionDetailsId;
	}

	public String getRegionDetailsId() {
		return regionDetailsId;
	}

	public Vertex getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(Vertex mapCenter) {
		this.mapCenter = mapCenter;
	}

	public int getMapZoomLevel() {
		return mapZoomLevel;
	}

	public void setMapZoomLevel(int mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionName() {
		return regionName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegionPolygon [mapCenter=");
		builder.append(mapCenter);
		builder.append(", mapZoomLevel=");
		builder.append(mapZoomLevel);
		builder.append(", regionDetailsId=");
		builder.append(regionDetailsId);
		builder.append(", regionName=");
		builder.append(regionName);
		builder.append("]");
		return builder.toString();
	}



}
