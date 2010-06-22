package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

public abstract class DAO {

	protected Connection connection;
	static Logger logger = Logger.getLogger(DAO.class);
	
	
	protected void init()
	{
		try 
		{
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/tamt";
			connection = DriverManager.getConnection(url, "gis", "gis"); 
			// ((org.postgresql.PGConnection)connection).addDataType("geometry","org.postgis.PGgeometry");
			//((org.postgresql.Connection)conn).addDataType("box3d","org.postgis.PGbox3d");
			   
		} catch (Exception e)
		{
			logger.debug(e.getMessage());
		}
	}
}
