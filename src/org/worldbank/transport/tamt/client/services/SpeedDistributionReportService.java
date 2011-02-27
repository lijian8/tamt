package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("speedDistributionReportService")
public interface SpeedDistributionReportService extends RemoteService {

	void createSpeedDistributionReport() throws Exception;
	void createSpeedDistributionTrafficFlowReport() throws Exception;
	void createSpeedDistributionAggregateByDayTypeReport() throws Exception;
	void createSpeedDistributionAggregateByTagReport() throws Exception;
	
	SpeedDistributionReport getSpeedDistributionReport(TagDetails tagDetails) throws Exception;
	SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(TagDetails tagDetails) throws Exception;	
	SpeedDistributionAggregateByDayTypeReport getSpeedDistributionAggregateByDayTypeReport(TagDetails tagDetails) throws Exception;
	SpeedDistributionAggregateByTagReport getSpeedDistributionAggregateByTagReport() throws Exception;
	
}
