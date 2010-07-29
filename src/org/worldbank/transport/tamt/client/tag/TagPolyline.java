package org.worldbank.transport.tamt.client.tag;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.PolylineOptions;

public class TagPolyline extends Polyline {

	private String roadDetailsId;

	public TagPolyline(LatLng[] points) {
		super(points);
	}

	public TagPolyline(LatLng[] points, String color) {
		super(points, color);
	}
	
	public TagPolyline(LatLng[] points, String color, int weight) {
		super(points, color, weight);
	}
	
	public TagPolyline(LatLng[] points, String color, int weight,
			double opacity) {
		super(points, color, weight, opacity);
	}
	
	public TagPolyline(LatLng[] points, String color, int weight,
			double opacity, PolylineOptions options) {
		super(points, color, weight, opacity, options);
	}

	public String getRoadDetailsId() {
		return roadDetailsId;
	}

	public void setRoadDetailsId(String roadDetailsId) {
		this.roadDetailsId = roadDetailsId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagPolyline [(roadDetailsId=");
		builder.append(roadDetailsId);
		builder.append("), (points");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
