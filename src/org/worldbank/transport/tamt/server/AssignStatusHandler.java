package org.worldbank.transport.tamt.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.api.AssignStatusAPI;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.shared.AssignStatus;


public class AssignStatusHandler extends HttpServlet {

	private static final long serialVersionUID = -1264176580378111849L;
	static Logger logger = Logger.getLogger(AssignStatusHandler.class);
	private AssignStatusAPI api = new AssignStatusAPI();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
			//super.doGet(req, resp);
			String gpsTraceId = req.getParameter("g");
			String matched = req.getParameter("m");
			String processed = req.getParameter("p");
			String completed = req.getParameter("c");
			
			
			AssignStatus status = new AssignStatus();
			status.setGpsTraceId(gpsTraceId);
			status.setPointsMatched(Integer.parseInt(matched));
			status.setPointsProcessed(Integer.parseInt(processed));
			status.setLastUpdated(new Date());
			
			if( completed.equalsIgnoreCase("true"))
			{
				status.setComplete(true);
			}
			logger.debug("AssignStatusHandler status from parameters=" + status);
			
			try {
				api.updateAssignStatus(status);
			} catch (Exception e) {
				// TODO handle error back to client? this is called
				// from the plpythonu stored procedure on a fire and forget...
				logger.error(e.getMessage());
			}
		
		}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
			
		}
	
}
