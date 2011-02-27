package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.SpeedDistributionReportService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.TrafficCountRecordAPI;
import org.worldbank.transport.tamt.server.api.SpeedDistributionReportAPI;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SpeedDistributionReportServiceImpl extends RemoteServiceServlet implements SpeedDistributionReportService {

	static Logger logger = Logger.getLogger(SpeedDistributionReportServiceImpl.class);
	
	private SpeedDistributionReportAPI api;
	
	public SpeedDistributionReportServiceImpl()
	{
		api = new SpeedDistributionReportAPI();
	}

	@Override
	public SpeedDistributionReport getSpeedDistributionReport(TagDetails tagDetails)
			throws Exception {
		logger.debug("getSpeedDistribution servlet start");
		return api.getSpeedDistributionReport(tagDetails);
	}

	@Override
	public void createSpeedDistributionReport()
			throws Exception {
		api.createSpeedDistributionReport();
	}
	
	@Override
	public void createSpeedDistributionTrafficFlowReport()
			throws Exception {
		api.createSpeedDistributionTrafficFlowReport();
	}

	@Override
	public void createSpeedDistributionAggregateByDayTypeReport()
			throws Exception {
		api.createSpeedDistributionAggregateByDayTypeReport();
	}

	@Override
	public void createSpeedDistributionAggregateByTagReport()
			throws Exception {
		api.createSpeedDistributionAggregateByTagReport();
	}
	
	@Override
	public SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(
			TagDetails tagDetails) throws Exception {
		return api.getSpeedDistributionTrafficFlowReport(tagDetails);
	}
	
	@Override
	public SpeedDistributionAggregateByDayTypeReport getSpeedDistributionAggregateByDayTypeReport(
			TagDetails tagDetails) throws Exception {
		return api.getSpeedDistributionAggregateByDayTypeReport(tagDetails);
	}	
	
	@Override
	public SpeedDistributionAggregateByTagReport getSpeedDistributionAggregateByTagReport() throws Exception {
		return api.getSpeedDistributionAggregateByTagReport();
	}	
}
