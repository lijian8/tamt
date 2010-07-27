package org.worldbank.transport.tamt.server;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;

public class TAMTConnectionPoolInitializer extends HttpServlet {
	
	private static final long serialVersionUID = 4130817346108255693L;
	static final Logger logger = Logger.getLogger(TAMTConnectionPoolInitializer.class);
	 
	  public void init() throws ServletException {
	    
		logger.debug("Initializing database connection source with JNDI");
		PGConnectionPoolDataSource source = new PGConnectionPoolDataSource();
		source.setServerName("localhost");
		source.setPortNumber(5432);
		source.setDatabaseName("tamt15");
		source.setUser("gis");
		source.setPassword("gis");
		source.setDefaultAutoCommit(true);
		
		try {
			new InitialContext().rebind("TAMTDataSource", source);
		} catch (NamingException e) {
			logger.error("Could not initialize database connection source:" + e.getMessage());
		}
		
	  }
}
