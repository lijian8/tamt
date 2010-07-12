package org.worldbank.transport.tamt.server;

import java.util.ArrayList;

import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GPSTraceServiceImpl extends RemoteServiceServlet implements GPSTraceService {

	private GPSTraceAPI api;
	
	public GPSTraceServiceImpl()
	{
		api = new GPSTraceAPI();
	}

	@Override
	public void processGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		api.processGPSTraces(gpsTraceIds);
	}
	
	@Override
	public void deleteGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		api.deleteGPSTraces(gpsTraceIds);
	}

	@Override
	public ArrayList<GPSTrace> getGPSTraces(StudyRegion region)
			throws Exception {
		return api.getGPSTraces(region);
	}

	@Override
	public GPSTrace saveGPSTrace(GPSTrace gpsTrace) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
