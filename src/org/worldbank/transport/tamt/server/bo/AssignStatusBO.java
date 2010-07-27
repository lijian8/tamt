package org.worldbank.transport.tamt.server.bo;

import java.util.Date;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.AssignStatusDAO;
import org.worldbank.transport.tamt.server.dao.GPSTraceDAO;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;

public class AssignStatusBO {

	private AssignStatusDAO dao;
	private GPSTraceDAO traceDAO;
	private static Logger logger = Logger.getLogger(AssignStatusBO.class);
	
	private static AssignStatusBO singleton = null;
	public static AssignStatusBO get()
	{
		if(singleton == null)
		{
			singleton = new AssignStatusBO();
		}
		return singleton;		
	}
	
	public AssignStatusBO()
	{
		dao = AssignStatusDAO.get();
		traceDAO = GPSTraceDAO.get();
	}
	
	public AssignStatus getAssignStatusInProcess() throws Exception
	{
		return dao.getAssignStatusInProcess();
	}
	
	public AssignStatus getAssignStatus(AssignStatus status) throws Exception
	{
		//TODO: make sure we have an ID
		return dao.getAssignStatus(status);
	}

	public void updateAssignStatus(AssignStatus status) throws Exception
	{

		/*
		 * Get the record count from the GPS trace
		 * (matched, processed, updated, completed all came from http get from handler)
		 */
		/*
		String id = status.getGpsTraceId();
		GPSTrace trace = new GPSTrace();
		trace = traceDAO.getGPSTraceById(id);
		status.setPointsProcessed(trace.getRecordCount());
		*/
		
		//TODO: make sure matched and processed are integers
		dao.updateAssignStatus(status);
	}
	
	public void insertAssignStatus(AssignStatus status) throws Exception
	{
		/*
		 * Add a new date, get the record count from the GPS trace
		 * (matched, processed all came from http get from handler)
		 */
		
		String id = status.getGpsTraceId();
		GPSTrace trace = new GPSTrace();
		trace.setId(id);
		//trace = traceBO.getGPSTrace(trace);
		
		status.setLastUpdated(new Date());
		status.setPointsTotal(trace.getRecordCount());
		status.setPointsMatched(0);
		status.setPointsProcessed(0);
		
		dao.insertAssignStatus(status);
	}

	public void deleteStatus(AssignStatus status) throws Exception {
		dao.deleteStatus(status);
	}
	
}
