package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface TrafficFlowReportServiceAsync {

	void getTrafficFlowReport(TagDetails tagDetails, String dayType,
			AsyncCallback<TrafficFlowReport> callback);

	void createTrafficFlowReport(ArrayList<TagDetails> tagDetailsList,
			AsyncCallback<Void> callback);

}
