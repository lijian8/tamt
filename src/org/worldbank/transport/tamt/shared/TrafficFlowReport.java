package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TrafficFlowReport implements Serializable {

	String tagId;
	String dayType;
	Date created;
	
	/*
	 * 24 rows (1 for each hour of the day0
	 * 
	 * Each row has a list.
	 * 
	 * Each list has 10 items: (hour, 1 column for each vehicle type)
	 * - 0 = hour_bin
	 * - 1 = w2
	 * - 2 = w3
	 * - 3 = pc
	 * - 4 = tx
	 * - 5 = ldv
	 * - 6 = ldc
	 * - 7 = hdc
	 * - 8 = mdb
	 * - 9 = hdb
	 * 
	 * We can use a list of lists here because it is faster
	 * that another data structure, the user never has to
	 * update the data structure, and it will be easier to
	 * output bulk reports via CSV.
	 */
	ArrayList<ArrayList> dayTypeValues;
	
	public TrafficFlowReport()
	{
		
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getDayType() {
		return dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public ArrayList<ArrayList> getDayTypeValues() {
		return dayTypeValues;
	}

	public void setDayTypeValues(ArrayList<ArrayList> dayTypeValues) {
		this.dayTypeValues = dayTypeValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrafficFlowReport [created=");
		builder.append(created);
		builder.append(", dayType=");
		builder.append(dayType);
		builder.append(", dayTypeValues=");
		builder.append(dayTypeValues);
		builder.append(", tagId=");
		builder.append(tagId);
		builder.append("]");
		return builder.toString();
	}

}
