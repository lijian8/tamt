package org.worldbank.transport.tamt.server.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.TrafficCountRecordDAO;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;

public class TrafficCountRecordBO {

	private TrafficCountRecordDAO trafficCountRecordDAO;
	private RegionDAO regionDAO;
	private static Logger logger = Logger.getLogger(TrafficCountRecordBO.class);
	
	private static TrafficCountRecordBO singleton = null;
	public static TrafficCountRecordBO get()
	{
		if(singleton == null)
		{
			singleton = new TrafficCountRecordBO();
		}
		return singleton;
	}
	
	public TrafficCountRecordBO()
	{
		trafficCountRecordDAO = trafficCountRecordDAO.get();
		regionDAO = RegionDAO.get();
	}
	
	public TrafficCountRecord getTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception
	{
		String id = trafficCountRecord.getId();
		return trafficCountRecordDAO.getTrafficCountRecordById(id);
	}
	
	public ArrayList<TrafficCountRecord> getTrafficCountRecords(StudyRegion region) throws Exception
	{
		ArrayList<TrafficCountRecord> TrafficCountRecords = new ArrayList<TrafficCountRecord>();
		// if the region is null, return the traces for the current study region
		if ( region == null )
		{
			ArrayList<StudyRegion> regions = regionDAO.getStudyRegions();
			for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
				StudyRegion studyRegion = (StudyRegion) iterator.next();
				if(studyRegion.isCurrentRegion())
				{
					region = studyRegion;
					break;
				}
			}
		}
		if( region != null )
		{
			TrafficCountRecords = trafficCountRecordDAO.getTrafficCountRecords(region);
		}		
		return TrafficCountRecords;
	}
	
	public TrafficCountRecord saveTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws Exception {
		
		// validate date
		if( trafficCountRecord.getDate() == null)
		{
			throw new TrafficCountRecordException("Traffic count record must have a date");
		}

		// validate start time
		if( trafficCountRecord.getStartTime() == null)
		{
			throw new TrafficCountRecordException("Traffic count record must have a start time");
		}
		
		// validate end time
		if( trafficCountRecord.getEndTime() == null)
		{
			throw new TrafficCountRecordException("Traffic count record must have an end time");
		}
		
		// validate region
		if( trafficCountRecord.getRegion().equals(""))
		{
			ArrayList<StudyRegion> regions = regionDAO.getStudyRegions();
			StudyRegion region = null;
			for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
				StudyRegion studyRegion = (StudyRegion) iterator.next();
				if(studyRegion.isCurrentRegion())
				{
					region = studyRegion;
					break;
				}
			}
			if( region != null )
			{
				trafficCountRecord.setRegion(region.getId());
			} else {
				throw new TrafficCountRecordException("A traffic count record cannot be saved is there is no current study region");
			}
		}
		
		// validate day type
		// TODO: should also check for WEEKDAY, SATURDAY, SUNDAY_HOLIDAY
		if( trafficCountRecord.getDayType().equalsIgnoreCase(""))
		{
			throw new TrafficCountRecordException("Traffic count record must have a day type");
		}
		
		// validate tag
		if( trafficCountRecord.getTag().equalsIgnoreCase(""))
		{
			throw new TrafficCountRecordException("Traffic count record must have a tag");
		}
		
		try {
			if( trafficCountRecord.getId().indexOf("TEMP") != -1 )
			{
				// create an id, and save it
				trafficCountRecord.setId( UUID.randomUUID().toString() );
				return trafficCountRecordDAO.saveTrafficCountRecord(trafficCountRecord);
			} else {
				// use the existing id to update it
				return trafficCountRecordDAO.updateTrafficCountRecord(trafficCountRecord);
			}			
		} catch (SQLException e)
		{
			if( e.getMessage().indexOf("duplicate key value violates unique constraint") != -1 )
			{
				throw new Exception("A traffic count record with that id already exists");
			} else {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (Exception e)
		{
			throw new Exception("An unknown error occured while trying to save a traffic count record");
		}
		
	}
	
	public void deleteTrafficCountRecords(ArrayList<String> trafficCountRecordIds) throws Exception {
		try {
			trafficCountRecordDAO.deleteTrafficCountRecords(trafficCountRecordIds);
		} catch (SQLException e) {
			logger.error("Could not delete traffic count record:" + e.getMessage());
			throw new Exception("Could not delete traffic count record");
		}
	}

	public void updateTrafficCountRecord(TrafficCountRecord TrafficCountRecord) throws Exception {
		trafficCountRecordDAO.updateTrafficCountRecord(TrafficCountRecord);
	}
}
