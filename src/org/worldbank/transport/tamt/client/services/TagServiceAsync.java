package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadLengthReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagServiceAsync {

	void getTagDetails(StudyRegion region, AsyncCallback<ArrayList<TagDetails>> asyncCallback);
	void saveTagDetails(TagDetails tagDetails, AsyncCallback<TagDetails> asyncCallback);
	void deleteTagDetails(ArrayList<String> tagDetailIds, AsyncCallback<Void> asyncCallback);

	void createRoadLengthReport(AsyncCallback<Void> callback);
	void getRoadLengthReport(AsyncCallback<RoadLengthReport> callback);
	
}
