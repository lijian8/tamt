package org.worldbank.transport.tamt.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AssignStatus implements IsSerializable {

	private int pointsTotal;
	private int pointsProcessed;
	private int pointsMatched;
	
	private String gpsTraceId;
	private String gpsTraceName;
	private Date lastUpdated;
	private boolean complete;
	
	public AssignStatus()
	{
		
	}

	public String getGpsTraceId() {
		return gpsTraceId;
	}

	public void setGpsTraceId(String gpsTraceId) {
		this.gpsTraceId = gpsTraceId;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setGpsTraceName(String gpsTraceName) {
		this.gpsTraceName = gpsTraceName;
	}

	public String getGpsTraceName() {
		return gpsTraceName;
	}

	public int getPointsTotal() {
		return pointsTotal;
	}

	public void setPointsTotal(int pointsTotal) {
		this.pointsTotal = pointsTotal;
	}

	public int getPointsProcessed() {
		return pointsProcessed;
	}

	public void setPointsProcessed(int pointsProcessed) {
		this.pointsProcessed = pointsProcessed;
	}

	public int getPointsMatched() {
		return pointsMatched;
	}

	public void setPointsMatched(int pointsMatched) {
		this.pointsMatched = pointsMatched;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssignStatus [complete=");
		builder.append(complete);
		builder.append(", gpsTraceId=");
		builder.append(gpsTraceId);
		builder.append(", gpsTraceName=");
		builder.append(gpsTraceName);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append(", pointsMatched=");
		builder.append(pointsMatched);
		builder.append(", pointsProcessed=");
		builder.append(pointsProcessed);
		builder.append(", pointsTotal=");
		builder.append(pointsTotal);
		builder.append("]");
		return builder.toString();
	}

}
