package org.worldbank.transport.tamt.server;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.tools.zip.AsiExtraField;
import org.worldbank.transport.tamt.client.services.AssignService;
import org.worldbank.transport.tamt.server.api.AssignStatusAPI;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssignServiceImpl extends RemoteServiceServlet implements AssignService {

	static Logger logger = Logger.getLogger(AssignServiceImpl.class);
	
	AssignmentWorker assigner;

	private AssignStatusAPI api;
	private GPSTraceAPI traceAPI;
	private AssignStatus status;
	
	
	public void init(ServletConfig config) throws ServletException {
		    super.init(config);                  // always!
		    logger.debug("init()");
			assigner = new AssignmentWorker();
		    assigner.setPriority(Thread.MIN_PRIORITY);  // be a good citizen
	}
	 
	public AssignServiceImpl()
	{
		api = new AssignStatusAPI();
		traceAPI = new GPSTraceAPI();
	}

	@Override
	public AssignStatus assignTagToPoints(AssignStatus status) throws Exception {

	    logger.debug("executing assignTagToPoints");
		logger.debug("isAlive?" + assigner.isAlive());
		if( assigner.isAlive())
		{
			
			try {
				checkStatus(status); // we should not hit assignTagToPoints on subsequent requests (should be going to checkStatus), but just in case we did....
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("Already processing:" + this.status.getGpsTraceId());
			throw new Exception("Sorry, another user is already processing a GPS trace.");
			
		} else {
			
			//this.status = status;
			
			status.setLastUpdated(new Date());
			status.setPointsTotal(0);
			status.setPointsMatched(0);
			status.setPointsProcessed(0);
			status.setComplete(false);
			
			logger.debug("Start assigner with this status:" + status);
			
			assigner = new AssignmentWorker();
			assigner.setPriority(Thread.MIN_PRIORITY);
			assigner.setStatus(status);
			assigner.start();
		}
		return status;
	}
	
	public void destroy() {

		logger.debug("executing destroy() in AssignServiceImpl");
	    //assigner.stop();
	 }

	@Override
	public AssignStatus checkStatus(AssignStatus status) throws Exception {
		
		try {
			logger.debug("executing checkStatus");
			logger.debug("isAlive?" + assigner.isAlive());
			if( assigner.isAlive())
			{
				logger.debug("assigner thread is still running, fetch status update from db");
				
				/*
				 * 
				 */
				status = api.getAssignStatus(status);
				logger.debug("GET ASSIGNED STATUS PRE DATE CHANGE=" + status);
				status.setLastUpdated(new Date());
				logger.debug("GET ASSIGNED STATUS POST DATE CHANGE=" + status);
				
				/*
				 * There can be a race condition where the completed status
				 * is TRUE, but the assigner has not yet finished its work.
				 * So, as a workaround, force the completed status to FALSE.
				 * This will force the client-side polling mechanism to
				 * check one more time, find the assigner dead, and drop
				 * through to the ELSE block below, and which point the
				 * completed status is still true, and the client reacts
				 * accordingly.
				 */
				status.setComplete(false);
				
			}
			else {
				logger.debug("assigner is complete");		
				
				//update status to complete
				status = api.getAssignStatus(status);
				status.setLastUpdated(new Date());
				status.setComplete(true);
				logger.debug("completed status=" + status);	
				
				// get the corresponding trace
				GPSTrace fetchTrace = new GPSTrace();
				fetchTrace.setId(status.getGpsTraceId());
				fetchTrace = traceAPI.getGPSTrace(fetchTrace);
				// now update the processed date and matchedpoints
				fetchTrace.setProcessDate(new Date());
				fetchTrace.setMatchedCount(status.getPointsMatched());
				fetchTrace.setProcessedCount(status.getPointsProcessed());
				fetchTrace.setProcessed(true);
				
				// and update it (not the file bits, just the meta-data)
				traceAPI.updateGPSTrace(fetchTrace);
				
				// and clean it up
				api.deleteStatus(status);
			}			
		} catch (Exception e)
		{
			logger.error("AssignService error:" + e.getMessage());
			throw new Exception("There was a problem checking the status of the GPS point assignment process.");
		}
		
		return status;
	}

	@Override
	public AssignStatus getAssignStatusInProcess() throws Exception {
		return api.getAssignStatusInProcess();
	}
}
