package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("trafficCountService")
public interface TrafficCountRecordService extends RemoteService {

	ArrayList<TrafficCountRecord> getTrafficCountRecords(StudyRegion region) throws Exception;
	TrafficCountRecord saveTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception;
	void deleteTrafficCountRecords(ArrayList<String> trafficCountRecordIds) throws Exception;
	TrafficCountReport getTrafficCountReport(TagDetails tagDetails) throws Exception;
}
