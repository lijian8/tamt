package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RoadServiceAsync {

	void getRoadDetails(StudyRegion region, AsyncCallback<ArrayList<RoadDetails>> asyncCallback);
	void saveRoadDetails(RoadDetails roadDetails, AsyncCallback<RoadDetails> asyncCallback);
	void deleteRoadDetails(ArrayList<String> roadDetailIds, AsyncCallback<Void> asyncCallback);
}
