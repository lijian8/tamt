package org.worldbank.transport.tamt.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.services.BackupService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BackupDatabaseServiceImpl extends RemoteServiceServlet implements BackupService {

	private static final long serialVersionUID = 894056636910016950L;
	static Logger logger = Logger.getLogger(BackupDatabaseServiceImpl.class);
	
	BackupDatabaseWorker worker;
	
	public void init(ServletConfig config) throws ServletException {
		    super.init(config);                  // always!
		    logger.debug("init()");
		    worker = new BackupDatabaseWorker();
		    worker.setPriority(Thread.MIN_PRIORITY);  // be a good citizen
	}
	 
	public BackupDatabaseServiceImpl()
	{
		
	}

	public void destroy() {
		logger.debug("executing destroy() in BackupDatabaseServiceImpl");
	    //assigner.stop();
	 }

	@Override
	public String backupDatabase() throws Exception {

		// start a new thread, so that a user can't hit
		// this twice in a row without the first one completing
		worker = new BackupDatabaseWorker();
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String ts = format.format(new Date());
		
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec("ls -l");
			InputStream in = p.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(in);
			InputStreamReader inread = new InputStreamReader(buf);
			BufferedReader bufferedreader = new BufferedReader(inread);
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				logger.debug(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "starting";
		
		
		
	}

}
