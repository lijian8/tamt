package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class SpeedDistributionRecord implements Serializable {
	
	private String tagId;
	private String dayType;
	private String hourBin;
	private boolean observed;
	private double totalFlow;
	
	public SpeedDistributionRecord() {
		
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

	public String getHourBin() {
		return hourBin;
	}

	public void setHourBin(String hourBin) {
		this.hourBin = hourBin;
	}

	public boolean isObserved() {
		return observed;
	}

	public void setObserved(boolean observed) {
		this.observed = observed;
	}

	public double getTotalFlow() {
		return totalFlow;
	}

	public void setTotalFlow(double totalFlow) {
		this.totalFlow = totalFlow;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpeedDistributionRecord [dayType=");
		builder.append(dayType);
		builder.append(", hourBin=");
		builder.append(hourBin);
		builder.append(", observed=");
		builder.append(observed);
		builder.append(", tagId=");
		builder.append(tagId);
		builder.append(", totalFlow=");
		builder.append(totalFlow);
		builder.append("]");
		return builder.toString();
	}

	
}