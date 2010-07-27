package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

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
}
