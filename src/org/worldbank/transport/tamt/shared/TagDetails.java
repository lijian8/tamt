package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TagDetails implements Serializable {
	
	private String id;
	private String name;
	private String description;
	private StudyRegion region;
	
	public TagDetails() {
		
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StudyRegion getRegion() {
		return region;
	}

	public void setRegion(StudyRegion region) {
		this.region = region;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagDetails [description=");
		builder.append(description);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", region=");
		builder.append(region);
		builder.append("]");
		return builder.toString();
	}
  
  
}