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
	private String residentialZoneBlockLength;
	private String industrialZoneBlockLength;
	private String commercialZoneBlockLength;
	
	private String minimumSoakInterval;
	
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

	public String getResidentialZoneBlockLength() {
		return residentialZoneBlockLength;
	}

	public void setResidentialZoneBlockLength(String residentialZoneBlockLength) {
		this.residentialZoneBlockLength = residentialZoneBlockLength;
	}

	public String getIndustrialZoneBlockLength() {
		return industrialZoneBlockLength;
	}

	public void setIndustrialZoneBlockLength(String industrialZoneBlockLength) {
		this.industrialZoneBlockLength = industrialZoneBlockLength;
	}

	public String getCommercialZoneBlockLength() {
		return commercialZoneBlockLength;
	}

	public void setCommercialZoneBlockLength(String commercialZoneBlockLength) {
		this.commercialZoneBlockLength = commercialZoneBlockLength;
	}

	public String getMinimumSoakInterval() {
		return minimumSoakInterval;
	}

	public void setMinimumSoakInterval(String minimumSoakInterval) {
		this.minimumSoakInterval = minimumSoakInterval;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudyRegion [centroid=");
		builder.append(centroid);
		builder.append(", commercialZoneBlockLength=");
		builder.append(commercialZoneBlockLength);
		builder.append(", currentRegion=");
		builder.append(currentRegion);
		builder.append(", defaultZoneType=");
		builder.append(defaultZoneType);
		builder.append(", description=");
		builder.append(description);
		builder.append(", id=");
		builder.append(id);
		builder.append(", industrialZoneBlockLength=");
		builder.append(industrialZoneBlockLength);
		builder.append(", mapCenter=");
		builder.append(mapCenter);
		builder.append(", mapZoomLevel=");
		builder.append(mapZoomLevel);
		builder.append(", minimumSoakInterval=");
		builder.append(minimumSoakInterval);
		builder.append(", name=");
		builder.append(name);
		builder.append(", residentialZoneBlockLength=");
		builder.append(residentialZoneBlockLength);
		builder.append(", utcOffset=");
		builder.append(utcOffset);
		builder.append(", vertices=");
		builder.append(vertices);
		builder.append("]");
		return builder.toString();
	}

	
}
