package org.worldbank.transport.tamt.server;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;

public class AssignmentWorker extends Thread {
	
	static Logger logger = Logger.getLogger(AssignmentWorker.class);
	private AssignStatus status;
	private GPSTraceAPI api = new GPSTraceAPI();
	
	public void setStatus(AssignStatus status)
	{
		this.status = status;
	}
	
	public void run()
	{
		// TODO Auto-generated method stub
		logger.debug("executing run() in AssignmentWorker");
		logger.debug("here is where we fire up the api and the thread with the status=" + this.status);
		long start = System.currentTimeMillis();
		
		
		try {
			GPSTrace gpsTrace = new GPSTrace();
			gpsTrace.setId(status.getGpsTraceId());
			api.assignPoints(gpsTrace);	
		} catch (Exception e) {
			logger.error(e.getMessage());
			// TODO: what happens if we error out in this thread?
			// doesn't look like there is any way to tell the UI,
			// without going back to the database to update the status table
		}
		
		/*
		try {
			this.sleep(20000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/
		
		long end = System.currentTimeMillis();
		long delta = end - start;
		logger.debug("Assignment analysis for ("+status.getGpsTraceId()+") completed in (ms):" + delta);		
	}

}
