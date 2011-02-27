package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.SpeedDistributionReportService;
import org.worldbank.transport.tamt.client.services.TripStatisticsReportService;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.api.TrafficCountRecordAPI;
import org.worldbank.transport.tamt.server.api.SpeedDistributionReportAPI;
import org.worldbank.transport.tamt.server.api.TripStatisticsReportAPI;
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
import org.worldbank.transport.tamt.shared.TripStatisticsReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TripStatisticsReportServiceImpl extends RemoteServiceServlet implements TripStatisticsReportService {

	static Logger logger = Logger.getLogger(TripStatisticsReportServiceImpl.class);

	private TripStatisticsReportAPI api;
	
	public TripStatisticsReportServiceImpl()
	{
		api = new TripStatisticsReportAPI();
	}
	
	@Override
	public void createTripStatisticsReport() throws Exception {
		api.createTripStatisticsReport();
	}

	@Override
	public TripStatisticsReport getSoakBinReport() throws Exception {
		return api.getSoakBinReport();
	}
	
	@Override
	public TripStatisticsReport getTripStatisticsReport() throws Exception {
		return api.getTripStatisticsReport();
	}
	
	
}
