package org.worldbank.transport.tamt.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.bo.AssignStatusBO;
import org.worldbank.transport.tamt.server.bo.GPSTraceBO;
import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.server.dao.AssignStatusDAO;
import org.worldbank.transport.tamt.server.dao.GPSTraceDAO;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.RoadDAO;
import org.worldbank.transport.tamt.server.dao.TagDAO;
import org.worldbank.transport.tamt.server.dao.ZoneDAO;

public class SingletonInitializer extends HttpServlet {
	
	private static final long serialVersionUID = 8997530558046781604L;
	static final Logger logger = Logger.getLogger(SingletonInitializer.class);
	 
	  public void init() throws ServletException {
	    
		  try {
			  
			  // data access objects
			  logger.debug("Initializing DAO singletons");
			  AssignStatusDAO.get();
			  GPSTraceDAO.get();
			  RegionDAO.get();
			  RoadDAO.get();
			  TagDAO.get();
			  ZoneDAO.get();
			  
			  // business rule objects
			  logger.debug("Initializing BO singletons");
			  AssignStatusBO.get();
			  GPSTraceBO.get();
			  RegionBO.get();
			  RoadBO.get();
			  TagBO.get();
			  ZoneBO.get();
			  
			  
			  
			  
		  } catch (Exception e)
		  {
			  logger.error("Could not initialize DAO singletons: " + e.getMessage());
		  }
		
		
	  }
}
