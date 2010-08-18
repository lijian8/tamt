package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RegionServiceAsync {

	void getStudyRegions(AsyncCallback<ArrayList<StudyRegion>> asyncCallback);
	void saveStudyRegion(StudyRegion studyRegion, AsyncCallback<StudyRegion> asyncCallback);
	void deleteStudyRegions(ArrayList<String> studyRegionIds, AsyncCallback<Void> asyncCallback);
	void getDayTypePerYearOption(String studyRegionId, AsyncCallback<DayTypePerYearOption> callback);
	void saveDayTypePerYearOption(DayTypePerYearOption option,
			AsyncCallback<DayTypePerYearOption> callback);
}
