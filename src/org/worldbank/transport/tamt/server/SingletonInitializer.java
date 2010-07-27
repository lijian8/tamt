package org.worldbank.transport.tamt.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.bo.AssignStatusBO;
import org.worldbank.transport.tamt.server.bo.GPSTraceBO;
import org.worldbank.transport.tamt.server.dao.AssignStatusDAO;
import org.worldbank.transport.tamt.server.dao.GPSTraceDAO;

public class SingletonInitializer extends HttpServlet {
	
	private static final long serialVersionUID = 8997530558046781604L;
	static final Logger logger = Logger.getLogger(SingletonInitializer.class);
	 
	  public void init() throws ServletException {
	    
		  try {
			  // business rule objects
			  logger.debug("Initializing BO singletons");
			  AssignStatusBO.get();
			  GPSTraceBO.get();
			  
			  // data access objects
			  logger.debug("Initializing DAO singletons");
			  AssignStatusDAO.get();
			  GPSTraceDAO.get();
			  
		  } catch (Exception e)
		  {
			  logger.error("Could not initialize DAO singletons: " + e.getMessage());
		  }
		
		
	  }
}
