package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("trafficFlowReportService")
public interface TrafficFlowReportService extends RemoteService {

	TrafficFlowReport getTrafficFlowReport(TagDetails tagDetails, String dayType) throws Exception;
	void createTrafficFlowReport(ArrayList<TagDetails> tagDetailsList)
			throws Exception;
}
