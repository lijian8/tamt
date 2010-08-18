package org.worldbank.transport.tamt.shared;

import java.io.Serializable;

public class DayTypePerYearOption implements Serializable {

	String id;
	String regionId;
	String activeOption;
	String option1weekday;
	String option2weekday;
	String option2saturday;
	String option2sundayHoliday;
	
	public DayTypePerYearOption()
	{
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getOption1weekday() {
		return option1weekday;
	}

	public void setOption1weekday(String option1weekday) {
		this.option1weekday = option1weekday;
	}

	public String getOption2weekday() {
		return option2weekday;
	}

	public void setOption2weekday(String option2weekday) {
		this.option2weekday = option2weekday;
	}

	public String getOption2saturday() {
		return option2saturday;
	}

	public void setOption2saturday(String option2saturday) {
		this.option2saturday = option2saturday;
	}

	public String getOption2sundayHoliday() {
		return option2sundayHoliday;
	}

	public void setOption2sundayHoliday(String option2sundayHoliday) {
		this.option2sundayHoliday = option2sundayHoliday;
	}

	public String getActiveOption() {
		return activeOption;
	}

	public void setActiveOption(String activeOption) {
		this.activeOption = activeOption;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DayTypePerYearOption [activeOption=");
		builder.append(activeOption);
		builder.append(", id=");
		builder.append(id);
		builder.append(", option1weekday=");
		builder.append(option1weekday);
		builder.append(", option2saturday=");
		builder.append(option2saturday);
		builder.append(", option2sundayHoliday=");
		builder.append(option2sundayHoliday);
		builder.append(", option2weekday=");
		builder.append(option2weekday);
		builder.append(", regionId=");
		builder.append(regionId);
		builder.append("]");
		return builder.toString();
	}
}
