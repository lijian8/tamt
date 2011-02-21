package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class StudyRegion implements Serializable {

	private String id;
	private String name;
	private String description;
	private String utcOffset;
	
	private String defaultZoneType;
	private String zoneBlockLength;
	
	private ArrayList<Vertex> vertices;
	private Vertex centroid;
	
	// user preferences
	private int mapZoomLevel;
	private Vertex mapCenter;
	private boolean currentRegion;
	
	public StudyRegion()
	{
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void setCentroid(Vertex centroid) {
		this.centroid = centroid;
	}

	public Vertex getCentroid() {
		return centroid;
	}

	public int getMapZoomLevel() {
		return mapZoomLevel;
	}

	public void setMapZoomLevel(int mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}

	public Vertex getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(Vertex mapCenter) {
		this.mapCenter = mapCenter;
	}

	public void setCurrentRegion(boolean currentRegion) {
		this.currentRegion = currentRegion;
	}

	public boolean isCurrentRegion() {
		return currentRegion;
	}

	public void setDefaultZoneType(String defaultZoneType) {
		this.defaultZoneType = defaultZoneType;
	}

	public String getDefaultZoneType() {
		return defaultZoneType;
	}

	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}

	public String getUtcOffset() {
		return utcOffset;
	}

	public String getZoneBlockLength() {
		return zoneBlockLength;
	}

	public void setZoneBlockLength(String zoneBlockLength) {
		this.zoneBlockLength = zoneBlockLength;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudyRegion [centroid=");
		builder.append(centroid);
		builder.append(", currentRegion=");
		builder.append(currentRegion);
		builder.append(", defaultZoneType=");
		builder.append(defaultZoneType);
		builder.append(", description=");
		builder.append(description);
		builder.append(", id=");
		builder.append(id);
		builder.append(", mapCenter=");
		builder.append(mapCenter);
		builder.append(", mapZoomLevel=");
		builder.append(mapZoomLevel);
		builder.append(", name=");
		builder.append(name);
		builder.append(", utcOffset=");
		builder.append(utcOffset);
		builder.append(", vertices=");
		builder.append(vertices);
		builder.append(", zoneBlockLength=");
		builder.append(zoneBlockLength);
		builder.append("]");
		return builder.toString();
	}

}
