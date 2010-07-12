package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GPSTraceServiceAsync {

	void getGPSTraces(StudyRegion region, AsyncCallback<ArrayList<GPSTrace>> asyncCallback);
	void saveGPSTrace(GPSTrace gpsTrace, AsyncCallback<GPSTrace> asyncCallback);
	void deleteGPSTraces(ArrayList<String> gpsTraceIds, AsyncCallback<Void> asyncCallback);
	void processGPSTraces(ArrayList<String> gpsTraceIds, AsyncCallback<Void> asyncCallback);
}
