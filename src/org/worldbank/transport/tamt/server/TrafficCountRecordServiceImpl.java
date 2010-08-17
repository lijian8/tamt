package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TrafficCountRecordServiceImpl extends RemoteServiceServlet implements TrafficCountRecordService {

	static Logger logger = Logger.getLogger(TrafficCountRecordServiceImpl.class);
	
	public TrafficCountRecordServiceImpl()
	{

	}

	@Override
	public void deleteTrafficCountRecords(
			ArrayList<String> trafficCountRecordIds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<TrafficCountRecord> getTrafficCountRecords(
			StudyRegion region) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrafficCountRecord saveTrafficCountRecord(
			TrafficCountRecord trafficCountRecord) throws Exception {
		// TODO Auto-generated method stub
		// fake it: change the ID and return it
		trafficCountRecord.setId( UUID.uuid() );
		trafficCountRecord.setTag("stuart");
		trafficCountRecord.setDate(new Date());
		trafficCountRecord.setDayType(TrafficCountRecord.DAYTYPE_SATURDAY);
		logger.error(trafficCountRecord);
		return trafficCountRecord;
	}
	
	
}
