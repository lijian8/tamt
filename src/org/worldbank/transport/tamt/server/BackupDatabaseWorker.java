package org.worldbank.transport.tamt.server;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;

public class BackupDatabaseWorker extends Thread {
	
	static Logger logger = Logger.getLogger(BackupDatabaseWorker.class);
	
	public void run()
	{
		logger.debug("executing run() in BackupDatabaseWorker");
		long start = System.currentTimeMillis();
		
		try {
			// do something
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		long end = System.currentTimeMillis();
		long delta = end - start;
		logger.debug("Database backup completed in (ms):" + delta);		
	}

}
