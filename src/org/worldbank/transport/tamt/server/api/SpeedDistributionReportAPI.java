package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.SpeedDistributionReportBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class SpeedDistributionReportAPI {

	private SpeedDistributionReportBO bo;
	
	public SpeedDistributionReportAPI()
	{
		bo = SpeedDistributionReportBO.get();
	}
	
	public void createSpeedDistributionReport() throws Exception
	{
		bo.createSpeedDistributionReport();
	}

	public void createSpeedDistributionTrafficFlowReport() throws Exception
	{
		bo.createSpeedDistributionTrafficFlowReport();
	}
	
	public SpeedDistributionReport getSpeedDistributionReport(TagDetails tagDetails) throws Exception
	{
		return bo.getSpeedDistributionReport(tagDetails);
	}
	
	public String downloadSpeedDistributionReport(String tagId) throws Exception
	{
		return bo.downloadSpeedDistributionReport(tagId);
	}

	public SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(
			TagDetails tagDetails) throws Exception {
		return bo.getSpeedDistributionTrafficFlowReport(tagDetails);
	}
	
}
