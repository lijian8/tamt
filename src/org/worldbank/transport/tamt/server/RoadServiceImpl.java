package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.client.services.RoadService;
import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.server.api.RoadAPI;
import org.worldbank.transport.tamt.server.api.TagAPI;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RoadServiceImpl extends RemoteServiceServlet implements RoadService {

	private RoadAPI roadAPI;
	
	public RoadServiceImpl()
	{
		roadAPI = new RoadAPI();
	}

	public ArrayList<RoadDetails> getRoadDetails(StudyRegion region) throws Exception {
		return roadAPI.getRoadDetails(region);
	}

	public RoadDetails saveRoadDetails(RoadDetails roadDetails) throws Exception {
		return roadAPI.saveRoadDetails(roadDetails);
	}

	public void deleteRoadDetails(ArrayList<String> roadDetailIds) throws Exception {
		roadAPI.deleteRoadDetails(roadDetailIds);
	}
}
