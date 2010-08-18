package org.worldbank.transport.tamt.server.api;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.TrafficCountRecordBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class TrafficCountRecordAPI {

	private TrafficCountRecordBO trafficCountRecordBO;
	
	public TrafficCountRecordAPI()
	{
		trafficCountRecordBO = trafficCountRecordBO.get();
	}
	
	public TrafficCountReport getTrafficCountReport(TagDetails tagDetails) throws Exception
	{
		return trafficCountRecordBO.getTrafficCountReport(tagDetails);
	}
	
	public ArrayList<TrafficCountRecord> getTrafficCountRecords(StudyRegion region) throws Exception
	{
		return trafficCountRecordBO.getTrafficCountRecords(region);
	}
	
	public TrafficCountRecord saveTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception
	{
		return trafficCountRecordBO.saveTrafficCountRecord(trafficCountRecord);
	}
	
	public void deleteTrafficCountRecords(ArrayList<String> trafficCountRecordIds) throws Exception {
		trafficCountRecordBO.deleteTrafficCountRecords(trafficCountRecordIds);
	}


	public TrafficCountRecord getTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception {
		return trafficCountRecordBO.getTrafficCountRecord(trafficCountRecord);
	}

	public void updateTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception {
		trafficCountRecordBO.updateTrafficCountRecord(trafficCountRecord);
	}
}
