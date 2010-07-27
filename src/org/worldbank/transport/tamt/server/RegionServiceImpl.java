package org.worldbank.transport.tamt.server;

import java.util.ArrayList;

import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.server.api.RegionAPI;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RegionServiceImpl extends RemoteServiceServlet implements RegionService {

	private static final long serialVersionUID = 4857359527900551907L;
	private RegionAPI regionAPI;
	
	public RegionServiceImpl()
	{
		regionAPI = new RegionAPI();
	}

	@Override
	public void deleteStudyRegions(ArrayList<String> studyRegionIds)
			throws Exception {
		regionAPI.deleteStudyRegions(studyRegionIds);
	}

	@Override
	public ArrayList<StudyRegion> getStudyRegions() throws Exception {
		return regionAPI.getStudyRegions();
	}

	@Override
	public StudyRegion saveStudyRegion(StudyRegion studyRegion)
			throws Exception {
		return regionAPI.saveStudyRegion(studyRegion);
	}	
	
}
