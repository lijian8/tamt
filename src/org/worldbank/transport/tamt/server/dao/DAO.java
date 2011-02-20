package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;

abstract class DAO {

	private static Logger logger = Logger.getLogger(DAO.class);
	protected Connection connection = null;
	
	protected Connection getConnection()
	{
		//logger.debug("Get a database connection");
		/*
		 * Reuse non-null connections. See if this
		 * improves performance setting up DB access
		 * in TAMT_insertSpeedDistributionRecord stored procedure
		 * found in SpeedBinDAO.insertSpeedDistributionRecord
		 * 
		 * Not sure if it will cause thread issues from the
		 * web front end.
		 */
		if( connection == null)
		{
			//logger.debug("Connection is null, get one from the pool");
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
		} else {
			
			try {
				if( connection.isClosed())
				{
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
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//logger.debug("Fetched connection=" + connection.toString());
		return connection;
	}
}
