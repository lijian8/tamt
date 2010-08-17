package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TrafficCountRecordServiceAsync {

	void getTrafficCountRecords(StudyRegion region, AsyncCallback<ArrayList<TrafficCountRecord>> asyncCallback);
	void saveTrafficCountRecord(TrafficCountRecord trafficCountRecord, AsyncCallback<TrafficCountRecord> asyncCallback);
	void deleteTrafficCountRecords(ArrayList<String> gpsTraceIds, AsyncCallback<Void> asyncCallback);
}
