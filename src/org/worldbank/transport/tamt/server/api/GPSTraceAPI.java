package org.worldbank.transport.tamt.server.api;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.GPSTraceBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class GPSTraceAPI {

	private GPSTraceBO gpsTraceBO;
	
	public GPSTraceAPI()
	{
		gpsTraceBO = new GPSTraceBO();
	}
	
	public ArrayList<GPSTrace> getGPSTraces(StudyRegion region) throws Exception
	{
		return gpsTraceBO.getGPSTrace(region);
	}
	
	public GPSTrace saveGPSTrace(GPSTrace gpsTrace) throws Exception
	{
		return gpsTraceBO.saveGPSTrace(gpsTrace);
	}
	
	public void saveGPSTrace(GPSTrace gpsTrace, File file) throws Exception
	{
		gpsTraceBO.saveGPSTrace(gpsTrace, file);
	}

	public void processGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		gpsTraceBO.processGPSTraces(gpsTraceIds);
	}
	
	public void deleteGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		gpsTraceBO.deleteGPSTraces(gpsTraceIds);
	}
}
