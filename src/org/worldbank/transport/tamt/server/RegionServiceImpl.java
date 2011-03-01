package org.worldbank.transport.tamt.server;

import java.util.ArrayList;

import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.server.api.RegionAPI;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
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

	@Override
	public DayTypePerYearOption getDayTypePerYearOption(String studyRegionId)
			throws Exception {
		return regionAPI.getDayTypePerYearOption(studyRegionId);
	}

	@Override
	public DayTypePerYearOption saveDayTypePerYearOption(
			DayTypePerYearOption option) throws Exception {
		return regionAPI.saveDayTypePerYearOption(option);
	}

	@Override
	public DefaultFlow getDefaultFlow(DefaultFlow defaultFlow) throws Exception {
		return regionAPI.getDefaultFlow(defaultFlow);
	}

	@Override
	public DefaultFlow saveDefaultFlow(DefaultFlow defaultFlow)
			throws Exception {
		return regionAPI.saveDefaultFlow(defaultFlow);
	}

	@Override
	public void deleteDefaultFlow(DefaultFlow defaultFlow) throws Exception {
		regionAPI.deleteDefaultFlow(defaultFlow);
	}

	@Override
	public void copyStudyRegion(StudyRegion studyRegion) throws Exception {
		regionAPI.copyStudyRegion(studyRegion);
	}	
	
}
