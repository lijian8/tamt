package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class RegionAPI {

	private RegionBO bo;
	
	public RegionAPI()
	{
		bo = RegionBO.get();
	}

	public void deleteStudyRegions(ArrayList<String> studyRegionIds) throws Exception {
		bo.deleteStudyRegions(studyRegionIds);
	}

	public ArrayList<StudyRegion> getStudyRegions() throws Exception {
		return bo.getStudyRegions();
	}

	public StudyRegion saveStudyRegion(StudyRegion studyRegion) throws Exception {
		return bo.saveStudyRegion(studyRegion);
	}

	public DayTypePerYearOption getDayTypePerYearOption(String studyRegionId) throws Exception {
		return bo.getDayTypePerYearOption(studyRegionId);
	}

	public DayTypePerYearOption saveDayTypePerYearOption(
			DayTypePerYearOption option) throws Exception {
		return bo.saveDayTypePerYearOption(option);
	}
	
}
