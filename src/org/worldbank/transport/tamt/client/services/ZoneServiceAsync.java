package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ZoneServiceAsync {

	void getZoneDetails(StudyRegion region, AsyncCallback<ArrayList<ZoneDetails>> asyncCallback);
	void saveZoneDetails(ZoneDetails zoneDetails, AsyncCallback<ZoneDetails> asyncCallback);
	void deleteZoneDetails(ArrayList<String> zoneDetailIds, AsyncCallback<Void> asyncCallback);
}
