package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficFlowReportService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.TrafficCountRecordAPI;
import org.worldbank.transport.tamt.server.api.TrafficFlowReportAPI;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TrafficFlowReportServiceImpl extends RemoteServiceServlet implements TrafficFlowReportService {

	static Logger logger = Logger.getLogger(TrafficFlowReportServiceImpl.class);
	
	private TrafficFlowReportAPI api;
	
	public TrafficFlowReportServiceImpl()
	{
		api = new TrafficFlowReportAPI();
	}

	@Override
	public TrafficFlowReport getTrafficFlowReport(TagDetails tagDetails, String dayType)
			throws Exception {
		return api.getTrafficFlowReport(tagDetails, dayType);
	}
	
	@Override
	public void createTrafficFlowReport(StudyRegion region) throws Exception
	{
		api.createTrafficFlowReport(region);
	}
}
