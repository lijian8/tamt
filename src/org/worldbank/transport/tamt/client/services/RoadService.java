package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roadService")
public interface RoadService extends RemoteService {

	ArrayList<RoadDetails> getRoadDetails(StudyRegion region) throws Exception;
	RoadDetails saveRoadDetails(RoadDetails roadDetails) throws Exception;
	void deleteRoadDetails(ArrayList<String> tagDetailIds) throws Exception;
}
