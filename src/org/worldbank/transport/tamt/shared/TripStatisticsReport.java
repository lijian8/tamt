package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TripStatisticsReport implements Serializable {

	String tagId;
	Date created;
	
	ArrayList<ArrayList> reportValues;
	
	public TripStatisticsReport()
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

	public ArrayList<ArrayList> getReportValues() {
		return reportValues;
	}

	public void setReportValues(ArrayList<ArrayList> reporValues) {
		this.reportValues = reporValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TripStatisticsReport [created=");
		builder.append(created);
		builder.append(", reportValues=");
		builder.append(reportValues);
		builder.append(", tagId=");
		builder.append(tagId);
		builder.append("]");
		return builder.toString();
	}
	
}
