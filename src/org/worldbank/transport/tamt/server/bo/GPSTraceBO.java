package org.worldbank.transport.tamt.server.bo;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.AssignStatusDAO;
import org.worldbank.transport.tamt.server.dao.GPSTraceDAO;
import org.worldbank.transport.tamt.server.dao.NonZipFileException;
import org.worldbank.transport.tamt.server.dao.RoadDAO;
import org.worldbank.transport.tamt.server.dao.ZoneDAO;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.GPSTraceException;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GPSTraceBO {

	private GPSTraceDAO gpsTraceDAO;
	private AssignStatusDAO assignStatusDAO;
	private static Logger logger = Logger.getLogger(GPSTraceBO.class);
	
	private static GPSTraceBO singleton = null;
	public static GPSTraceBO get()
	{
		if(singleton == null)
		{
			singleton = new GPSTraceBO();
		}
		return singleton;
	}
	
	public GPSTraceBO()
	{
		gpsTraceDAO = GPSTraceDAO.get();
		assignStatusDAO = AssignStatusDAO.get();
	}
	
	public GPSTrace getGPSTrace(GPSTrace gpsTrace) throws Exception
	{
		String id = gpsTrace.getId();
		return gpsTraceDAO.getGPSTraceById(id);
	}
	public ArrayList<GPSTrace> getGPSTraces(StudyRegion region) throws Exception
	{
		//TODO: validate study region name
		return gpsTraceDAO.getGPSTraces(region);
	}

	public void saveGPSTrace(GPSTrace gpsTrace, File file) throws Exception {
		
		try {
			// save the file first
			String fileId = UUID.randomUUID().toString();
			
			long startSaveFile = System.currentTimeMillis();
			gpsTraceDAO.saveFile(file, fileId);
			long endSaveFile = System.currentTimeMillis();
			logger.debug("Saving file took (ms):" + (endSaveFile - startSaveFile));
			
			// save just the gpsTrace with the associated fileId
			long startSaveGPSTrace = System.currentTimeMillis();
			gpsTrace.setFileId(fileId);
			GPSTrace savedGPSTrace = saveGPSTrace(gpsTrace);
			long endSaveGPSTrace = System.currentTimeMillis();
			logger.debug("Saving GPSTrace took (ms):" + (endSaveGPSTrace - startSaveGPSTrace));
			
			// and now we can process too (since we sped up the algorithm)
			long startProcessGPSTrace = System.currentTimeMillis();
			gpsTraceDAO.processGPSTraceById(savedGPSTrace.getId());
			long endProcessGPSTrace = System.currentTimeMillis();
			logger.debug("Processing GPSTrace took (ms):" + (endProcessGPSTrace - startProcessGPSTrace));
			
		} catch (NonZipFileException e)
		{
			throw e;// let the uploadhandler take care of it
		}
		catch (Exception e)
		{
			//TODO: throw exception that client can handle
			logger.error("saveGPSTrace error:" + e.getMessage());
			throw e;
		}
	}
	
	public GPSTrace saveGPSTrace(GPSTrace gpsTrace) throws Exception {
		//TODO: validate
		if( gpsTrace.getName().equalsIgnoreCase(""))
		{
			throw new GPSTraceException("GPS trace must have a name");
		}
		
		// fill in dates
		if( gpsTrace.getUploadDate() == null)
		{
			gpsTrace.setUploadDate(new Date());
		}
		// default process date is 1 day less than uploadDate (cannot be null for DB)
		if( gpsTrace.getProcessDate() == null)
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Date yesterday = cal.getTime();
			gpsTrace.setProcessDate(yesterday);
		}
		
		try {
			if( gpsTrace.getId().indexOf("TEMP") != -1 )
			{
				// create an id, and save it
				gpsTrace.setId( UUID.randomUUID().toString() );
				return gpsTraceDAO.saveGPSTrace(gpsTrace);
			} else {
				// use the existing id to update it
				return gpsTraceDAO.updateGPSTrace(gpsTrace);
			}			
		} catch (SQLException e)
		{
			if( e.getMessage().indexOf("duplicate key value violates unique constraint") != -1 )
			{
				throw new Exception("A GPS trace with that name already exists");
			} else {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (Exception e)
		{
			throw new Exception("An unknown error occured while trying to save a GPS trace");
		}
		
	}

	public void processGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		
		/*
		 * TODO: add any constraints for too many selected, etc.
		 * (not sure what to base this on yet)
		 * 
		 * Fetch the selected gpsTraceIds.
		 * Open the associated stored binary archive (from the fileId property).
		 * Run the ZipToTAMTPointAndDatabase routine
		 */
		try {
			gpsTraceDAO.processGPSTraces(gpsTraceIds);
		} catch (Exception e) {
			logger.error("Could not process GPS trace:" + e.getMessage());
			throw new Exception("Could not process GPS trace");
		}		
		
	}
	
	public void deleteGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		try {
			gpsTraceDAO.deleteGPSTraces(gpsTraceIds);
		} catch (SQLException e) {
			logger.error("Could not delete GPS trace:" + e.getMessage());
			throw new Exception("Could not delete GPS trace");
		}
	}

	public void assignPoints(GPSTrace gpsTrace) throws Exception {
		
		if( gpsTrace.getId() == null || gpsTrace.getId() == "" )
		{
			throw new Exception("GPS trace must have an ID to assign points");	
		}
		
		try {
			
			/*
			 * Before we do the assign points, let's record the initial
			 * status of the assignment process in the database
			 */
			AssignStatus status = new AssignStatus();
			status.setGpsTraceId(gpsTrace.getId());
			
			// fetch the updated trace, including the record count
			gpsTrace = getGPSTrace(gpsTrace);
			logger.debug("record count in trace prior to insertAssignStatus=" + gpsTrace.getRecordCount());
			
			//assignStatusBO.insertAssignStatus(status);
			/*
			 * Add a new date, get the record count from the GPS trace
			 * (matched, processed all came from http get from handler)
			 */
			status.setLastUpdated(new Date());
			status.setPointsTotal(gpsTrace.getRecordCount());
			status.setPointsMatched(0);
			status.setPointsProcessed(0);
			
			assignStatusDAO.insertAssignStatus(status);
			
			/*
			 * With the status table updated to signal the start
			 * of the process, let's run assignPoints. The DAO
			 * will kick off a stored procedure, which will update
			 * the status table.
			 */
			gpsTraceDAO.assignPoints(gpsTrace);
			
		} catch (Exception e) {
			logger.error("Could not assign points from GPS trace:" + e.getMessage());
			throw new Exception("Could not assign points from  GPS trace");
		}		
	}

	public void updateGPSTrace(GPSTrace gpsTrace) throws Exception {
		gpsTraceDAO.updateGPSTrace(gpsTrace);
	}
}
