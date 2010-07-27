package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.AssignStatus;

public class AssignStatusDAO extends DAO {

	static Logger logger = Logger.getLogger(AssignStatusDAO.class);
	
	private static AssignStatusDAO singleton = null;
	public static AssignStatusDAO get()
	{
		if(singleton == null)
		{
			singleton = new AssignStatusDAO();
		}
		return singleton;		
	}
	
	public AssignStatusDAO()
	{
		
	}
	
	// We should only every get 1 that is not completed. 
	// If there are more, then something bad happened
	public AssignStatus getAssignStatusInProcess() throws Exception
	{
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT s.gpstraceid,s.total,s.processed,s.matched," +
					"s.updated,s.completed, g.name FROM assignstatus s " +
					"JOIN gpstraces g ON (s.gpstraceid = g.id)";
			logger.debug("getAssignStatusInProcess SQL:" + sql);
			ResultSet r = s.executeQuery(sql);
			while( r.next() ) { 
				// this is reversed: a 'true' value in the status column 
				// indicates it is completed processing
				// so we are looking for a false value (indicating 
				// processing was started but not completed)
				boolean completed = r.getBoolean(6);
				logger.debug("completed=" + completed);
				if( !completed )
				{
					AssignStatus status = new AssignStatus();
					status.setGpsTraceId(r.getString(1));
					status.setPointsTotal(2);
					status.setPointsProcessed(3);
					status.setPointsMatched(4);
					status.setLastUpdated( r.getDate(5));
					status.setComplete(r.getBoolean(6));
					status.setGpsTraceName(r.getString(7));
					return status;
				}
			}
			connection.close(); // returns connection to pool
			
		} catch (SQLException e)
		{
			logger.error("Cannot fetch assign status in process from database: " + e.getMessage());
			throw new Exception("There was an error retrieving the assign status currently being processing.");
		}
		return null;
	}
	
	public AssignStatus getAssignStatus(AssignStatus status) throws Exception
	{
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT * FROM \"assignstatus\" where gpstraceid = '"+status.getGpsTraceId()+"'";
			logger.debug("SQL for getAssignStatus = " + sql);
			ResultSet r = s.executeQuery(sql);
			while( r.next() ) { 
				// id, gpstraceid, total, processed, matched, updated, status
				status.setPointsTotal(r.getInt(3));
				status.setPointsProcessed(r.getInt(4));
				status.setPointsMatched(r.getInt(5));
				status.setLastUpdated( r.getDate(6)); // date or timestamp?
				status.setComplete(r.getBoolean(7));
			}
			logger.debug("returned getAssignStatus=" + status);
			connection.close(); // returns connection to pool
			
		} catch (SQLException e)
		{
			logger.error("Could not fetch assign status from database: " + e.getMessage());
			throw new Exception("There was an error retrieving the status from the database.");
		}  catch (Exception e)
		{
			logger.error("Could not fetch assign status from database: " + e.getMessage());
			throw new Exception("An unknown error occured.");
		}
		return status;
	}

	public void insertAssignStatus(AssignStatus status) throws Exception {
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO \"assignstatus\" VALUES(" +
					"(SELECT nextval('assignstatus_id_seq')), " +
					"'"+status.getGpsTraceId()+"', " +
					status.getPointsTotal() + ", " +
					status.getPointsProcessed() + ", " +
					status.getPointsMatched() + ", " +
					"'"+status.getLastUpdated() +"', " +
					false +
					")";
			
			logger.debug(sql);
			boolean r = s.execute(sql);
			logger.debug("result of insert=" + r);
			connection.close(); // returns connection to pool
			
			// for debug only
			AssignStatus fetchedStatus = getAssignStatus(status);
			logger.debug("IMMEDIATE READ AFTER WRITE FOR INSERT status=" + fetchedStatus);
			
		} catch (SQLException e)
		{
			logger.error("Could not insert assign status: " + e.getMessage());
			throw new Exception("There was an error inserting the status to the database.");
		}		
	}
	
	public void updateAssignStatus(AssignStatus status) throws Exception {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE \"assignstatus\" SET " +
					"processed = "+status.getPointsProcessed()+", " +
					"matched = "+status.getPointsMatched()+", " +
					"updated = '"+status.getLastUpdated()+"', " +
					"completed = " + status.isComplete() + " " + 
					"WHERE gpstraceid = '"+status.getGpsTraceId()+"'";
		
			logger.debug("UPDATE SQL=" + sql);
			int r = s.executeUpdate(sql);
			logger.debug("result of update=" + r);
			connection.close(); // returns connection to pool
			
			// for debug only
			AssignStatus fetchedStatus = getAssignStatus(status);
			logger.debug("IMMEDIATE READ AFTER WRITE FOR UPDATE status=" + fetchedStatus);
			
		} catch (SQLException e)
		{
			logger.error("Could not update assign status: " + e.getMessage());
			throw new Exception("There was an error updating the status in the database.");
		}		
	}

	public void deleteStatus(AssignStatus status) throws Exception {
		try {
			// DELETE FROM assignstatus WHERE gpstraceid = gid;
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "DELETE FROM \"assignstatus\" WHERE " +
					"gpstraceid = '"+status.getGpsTraceId()+"'";
			logger.debug(sql);
			int r = s.executeUpdate(sql);
			logger.debug("result of delete=" + r);
			connection.close(); // returns connection to pool
			
		} catch (SQLException e)
		{
			logger.error("Could not update assign status: " + e.getMessage());
			throw new Exception("There was an error updating the status in the database.");
		}	
	}
	 
}
