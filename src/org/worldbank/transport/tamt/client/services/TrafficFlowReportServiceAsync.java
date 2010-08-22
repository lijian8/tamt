package org.worldbank.transport.tamt.client.services;

import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface TrafficFlowReportServiceAsync {

	void createTrafficFlowReport(StudyRegion region,
			AsyncCallback<Void> callback);

	void getTrafficFlowReport(TagDetails tagDetails, String dayType,
			AsyncCallback<TrafficFlowReport> callback);

}
