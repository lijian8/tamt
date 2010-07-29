package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

public class RoadAPI {

	private RoadBO roadBO;
	
	public RoadAPI()
	{
		roadBO = RoadBO.get();
	}
	
	public ArrayList<RoadDetails> getRoadDetails(StudyRegion region) throws Exception
	{
		return roadBO.getRoadDetails(region);
	}
	
	public RoadDetails saveRoadDetails(RoadDetails roadDetails) throws Exception
	{
		return roadBO.saveRoadDetails(roadDetails);
	}

	public void deleteRoadDetails(ArrayList<String> roadDetailIds) throws Exception {
		roadBO.deleteRoadDetails(roadDetailIds);
	}
}
