package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;

abstract class DAO {

	private static Logger logger = Logger.getLogger(DAO.class);
	
	protected Connection getConnection()
	{
		logger.debug("Fetching a connection from the pool");
		Connection connection = null;
		try {
			PGConnectionPoolDataSource source = (PGConnectionPoolDataSource)new InitialContext().lookup("TAMTDataSource");
		    connection = source.getConnection();
		} catch(SQLException e) {
		    // log error
			logger.debug("SQLException: " + e.getMessage());
		} catch(NamingException e) {
		    // DataSource wasn't found in JNDI
			logger.debug("JNDI naming exception: " + e.getMessage());
		} finally {
		    //if(connection != null) {
		    //    try {connection.close();}catch(SQLException e) {}
		    //}
		}
		logger.debug("Fetched connection=" + connection.toString());
		return connection;
		   
	}
}
