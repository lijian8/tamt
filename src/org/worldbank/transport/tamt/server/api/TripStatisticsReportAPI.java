package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.SpeedDistributionReportBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.TripStatisticsReportBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.TripStatisticsReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class TripStatisticsReportAPI {

	private TripStatisticsReportBO bo;
	
	public TripStatisticsReportAPI()
	{
		bo = TripStatisticsReportBO.get();
	}

	public void createTripStatisticsReport() throws Exception {
		bo.createTripStatisticsReport();
	}

	public TripStatisticsReport getSoakBinReport() throws Exception {
		return bo.getSoakBinReport();
	}
	
	public TripStatisticsReport getTripStatisticsReport() throws Exception {
		return bo.getTripStatisticsReport();
	}

	public String downloadTripStatisticsTripBinReport() throws Exception {
		return bo.downloadTripStatisticsTripBinReport();
	}

	public String downloadTripStatisticsSoakBinReport() throws Exception {
		return bo.downloadTripStatisticsSoakBinReport();
	}	
	
}
