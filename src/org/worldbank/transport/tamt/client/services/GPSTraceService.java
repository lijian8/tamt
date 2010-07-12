package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gpsTraceService")
public interface GPSTraceService extends RemoteService {

	ArrayList<GPSTrace> getGPSTraces(StudyRegion region) throws Exception;
	GPSTrace saveGPSTrace(GPSTrace gpsTrace) throws Exception;
	void deleteGPSTraces(ArrayList<String> gpsTraceIds) throws Exception;
	void processGPSTraces(ArrayList<String> gpsTraceIds) throws Exception;
}
