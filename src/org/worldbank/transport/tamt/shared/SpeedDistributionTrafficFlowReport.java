package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class SpeedDistributionTrafficFlowReport implements Serializable {

	String tagId;
	Date created;
	
	/*
	 * Each row has a list.
	 * 
	 * Each list has 8 items:
	 * - 0 = daytype
	 * - 1 = vehicletype
	 * - 2 = speedbin
	 * - 3 = vehiclesecondsperday
	 * - 4 = vehiclemetersperday
	 * - 5 = weightedaveragespeed
	 * - 6 = % veh seconds/day
	 * - 7 = % veh meters/day
	 * - 8 = % veh s/year
	 * - 9 = % veh m/year
	 * 
	 * We can use a list of lists here because it is faster
	 * that another data structure, the user never has to
	 * update the data structure, and it will be easier to
	 * output bulk reports via CSV.
	 */
	ArrayList<ArrayList> reporValues;
	
	public SpeedDistributionTrafficFlowReport()
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
		builder.append("SpeedDistributionReport [created=");
		builder.append(created);
		builder.append(", reporValues=");
		builder.append(reporValues);
		builder.append(", tagId=");
		builder.append(tagId);
		builder.append("]");
		return builder.toString();
	}



}
