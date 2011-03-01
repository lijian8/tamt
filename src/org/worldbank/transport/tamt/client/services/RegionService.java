package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("regionService")
public interface RegionService extends RemoteService {

	ArrayList<StudyRegion> getStudyRegions() throws Exception;
	StudyRegion saveStudyRegion(StudyRegion studyRegion) throws Exception;
	void deleteStudyRegions(ArrayList<String> studyRegionIds) throws Exception;
	void copyStudyRegion(StudyRegion studyRegion) throws Exception;
	
	// study region related entities
	DayTypePerYearOption getDayTypePerYearOption(String studyRegionId) throws Exception;
	DayTypePerYearOption saveDayTypePerYearOption(DayTypePerYearOption option) throws Exception;
	DefaultFlow getDefaultFlow(DefaultFlow defaultFlow) throws Exception;
	DefaultFlow saveDefaultFlow(DefaultFlow defaultFlow) throws Exception;
	void deleteDefaultFlow(DefaultFlow defaultFlow) throws Exception;
	
}
