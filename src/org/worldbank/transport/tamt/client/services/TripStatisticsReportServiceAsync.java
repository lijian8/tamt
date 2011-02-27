package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;
import org.worldbank.transport.tamt.shared.TripStatisticsReport;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface TripStatisticsReportServiceAsync {

	void createTripStatisticsReport(AsyncCallback<Void> callback);
	void getTripStatisticsReport(AsyncCallback<TripStatisticsReport> callback);
	void getSoakBinReport(AsyncCallback<TripStatisticsReport> callback);

}
