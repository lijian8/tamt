package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SpeedDistributionReportServiceAsync {

	void getSpeedDistributionReport(TagDetails tagDetails,
			AsyncCallback<SpeedDistributionReport> callback);
	void createSpeedDistributionReport(AsyncCallback<Void> callback);
	
	void getSpeedDistributionTrafficFlowReport(TagDetails tagDetails,
			AsyncCallback<SpeedDistributionTrafficFlowReport> asyncCallback);
	void createSpeedDistributionTrafficFlowReport(AsyncCallback<Void> callback);
	void createSpeedDistributionAggregateByDayTypeReport(
			AsyncCallback<Void> callback);
	void getSpeedDistributionAggregateByDayTypeReport(TagDetails tagDetails,
			AsyncCallback<SpeedDistributionAggregateByDayTypeReport> callback);
	
	void getSpeedDistributionAggregateByTagReport(
			AsyncCallback<SpeedDistributionAggregateByTagReport> callback);
	void createSpeedDistributionAggregateByTagReport(
			AsyncCallback<Void> callback);


}
