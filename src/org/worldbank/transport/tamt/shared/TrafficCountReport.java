package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class TrafficCountReport implements Serializable {

	
	// ONLY USED FOR SHORT VERSION OF STORED PROCEDURE,
	// WHICH IS CURRENTLY IN USE
	private ArrayList<Integer> weekdayTotals;
	private ArrayList<Integer> saturdayTotals;
	private ArrayList<Integer> sundayHolidayTotals;
	
	// ONLY USED FOR LONG VERSION OF STORED PROCEDURE,
	// WHICH IS **NOT** CURRENTLY IN USE
	private ArrayList<TrafficCountRecord> weekdayCounts;
	private ArrayList<TrafficCountRecord> saturdayCounts;
	private ArrayList<TrafficCountRecord> sundayHolidayCounts;
	
	public TrafficCountReport()
	{
		
	}

	public ArrayList<TrafficCountRecord> getWeekdayCounts() {
		return weekdayCounts;
	}

	public void setWeekdayCounts(ArrayList<TrafficCountRecord> weekdayCounts) {
		this.weekdayCounts = weekdayCounts;
	}

	public ArrayList<TrafficCountRecord> getSaturdayCounts() {
		return saturdayCounts;
	}

	public void setSaturdayCounts(ArrayList<TrafficCountRecord> saturdayCounts) {
		this.saturdayCounts = saturdayCounts;
	}

	public ArrayList<TrafficCountRecord> getSundayHolidayCounts() {
		return sundayHolidayCounts;
	}

	public void setSundayHolidayCounts(ArrayList<TrafficCountRecord> sundayHolidayCounts) {
		this.sundayHolidayCounts = sundayHolidayCounts;
	}

	public ArrayList<Integer> getWeekdayTotals() {
		return weekdayTotals;
	}

	public void setWeekdayTotals(ArrayList<Integer> weekdayTotals) {
		this.weekdayTotals = weekdayTotals;
	}

	public ArrayList<Integer> getSaturdayTotals() {
		return saturdayTotals;
	}

	public void setSaturdayTotals(ArrayList<Integer> saturdayTotals) {
		this.saturdayTotals = saturdayTotals;
	}

	public ArrayList<Integer> getSundayHolidayTotals() {
		return sundayHolidayTotals;
	}

	public void setSundayHolidayTotals(ArrayList<Integer> sundayHolidayTotals) {
		this.sundayHolidayTotals = sundayHolidayTotals;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrafficCountReport [saturdayCounts=");
		builder.append(saturdayCounts);
		builder.append(", saturdayTotals=");
		builder.append(saturdayTotals);
		builder.append(", sundayHolidayCounts=");
		builder.append(sundayHolidayCounts);
		builder.append(", sundayHolidayTotals=");
		builder.append(sundayHolidayTotals);
		builder.append(", weekdayCounts=");
		builder.append(weekdayCounts);
		builder.append(", weekdayTotals=");
		builder.append(weekdayTotals);
		builder.append("]");
		return builder.toString();
	}

}
