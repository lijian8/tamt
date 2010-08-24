package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.TrafficFlowReportBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class TrafficFlowReportAPI {

	private TrafficFlowReportBO bo;
	
	public TrafficFlowReportAPI()
	{
		bo = TrafficFlowReportBO.get();
	}
	
	public void createTrafficFlowReport(ArrayList<TagDetails> tagDetailsList) throws Exception
	{
		bo.createTrafficFlowReport(tagDetailsList);
	}
	
	public TrafficFlowReport getTrafficFlowReport(TagDetails tagDetails, String dayType) throws Exception
	{
		return bo.getTrafficFlowReport(tagDetails, dayType);
	}
	
	public String downloadTrafficFlowReport(String tagId) throws Exception
	{
		return bo.downloadTrafficFlowReport(tagId);
	}
	
}
