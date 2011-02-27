package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class SpeedDistributionAggregateByDayTypeReport implements Serializable {

	String tagId;
	Date created;

	ArrayList<ArrayList> reporValues;
	
	public SpeedDistributionAggregateByDayTypeReport()
	{
		
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public ArrayList<ArrayList> getReporValues() {
		return reporValues;
	}

	public void setReporValues(ArrayList<ArrayList> reporValues) {
		this.reporValues = reporValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpeedDistributionAggregateByDayTypeReport [created=");
		builder.append(created);
		builder.append(", reporValues=");
		builder.append(reporValues);
		builder.append(", tagId=");
		builder.append(tagId);
		builder.append("]");
		return builder.toString();
	}

}
