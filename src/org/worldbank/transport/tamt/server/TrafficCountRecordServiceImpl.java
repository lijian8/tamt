package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.TrafficCountRecordAPI;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TrafficCountRecordServiceImpl extends RemoteServiceServlet implements TrafficCountRecordService {

	static Logger logger = Logger.getLogger(TrafficCountRecordServiceImpl.class);
	
	private TrafficCountRecordAPI api;
	
	public TrafficCountRecordServiceImpl()
	{
		api = new TrafficCountRecordAPI();
	}

	@Override
	public void deleteTrafficCountRecords(
			ArrayList<String> trafficCountRecordIds) throws Exception {
		
		api.deleteTrafficCountRecords(trafficCountRecordIds);
		
	}

	@Override
	public ArrayList<TrafficCountRecord> getTrafficCountRecords(
			StudyRegion region) throws Exception {
		return api.getTrafficCountRecords(region);
	}

	@Override
	public TrafficCountRecord saveTrafficCountRecord(
			TrafficCountRecord trafficCountRecord) throws Exception {
		return api.saveTrafficCountRecord(trafficCountRecord);
	}
	
	
}
