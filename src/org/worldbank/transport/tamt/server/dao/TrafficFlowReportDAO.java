package org.worldbank.transport.tamt.server.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

public class TrafficFlowReportDAO extends DAO {

	static Logger logger = Logger.getLogger(TrafficFlowReportDAO.class);
	
	private static TrafficFlowReportDAO singleton = null;
	public static TrafficFlowReportDAO get() {
		if (singleton == null) {
			singleton = new TrafficFlowReportDAO();
		}
		return singleton;
	}

	private String DELIMITER = ",";
	private NumberFormat formatter = new DecimalFormat("#0.00");
	
	public TrafficFlowReportDAO()
	{
	
	}

	public ArrayList<ArrayList> getTrafficFlowReportData(TagDetails tagDetails, String dayType) throws Exception
	{

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String tagId = tagDetails.getId();
			
			String sql = "SELECT * FROM TAMT_trafficFlowReport(" +
					"'"+tagId+"', '"+dayType+"')" +
					"AS " +
					"foo (" +
					"\"hour\" integer," +
					"w2avg numeric," +
					"w3avg numeric," +
					"pcavg numeric," +
					"txavg numeric," +
					"ldvavg numeric," +
					"ldcavg numeric," +
					"hdcavg numeric," +
					"mdbavg numeric," +
					"hdbavg numeric)";
			
			logger.debug("SQL for getTrafficFlowReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
			
			while (r.next()) {
				
				// get the rows (there should always be 24, even if some are empty)
				// just keep them as lists for now so we can operate on them more easily
				// to determine "before-or-after" and "in-between" blanks
				// http://code.google.com/p/tamt/wiki/TrafficFlowReports#Before_and_After_Blanks
				ArrayList<String> row = new ArrayList<String>();
				row.add(r.getString(1)); // hour
				row.add(r.getString(2)); // w2avg
				row.add(r.getString(3)); // w3avg
				row.add(r.getString(4)); // pcavg
				row.add(r.getString(5)); // txavg
				row.add(r.getString(6)); // ldvavg
				row.add(r.getString(7)); // ldcavg
				row.add(r.getString(8)); // hdcavg
				row.add(r.getString(9)); // mdbavg
				row.add(r.getString(10)); // hdbavg
				
				rows.add(row);
				
			}

			return rows;
		
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}

	public void deleteTrafficFlowReports(TagDetails tagDetails, String dayType) throws Exception
	{
		/*
		 * We should delete any previous data for this tagid so
		 * that we don't end up with duplicate records per tag
		 */
		try {
			
			try {
				Connection connection = getConnection();
				Statement s = connection.createStatement();
				String sql = "DELETE FROM trafficflowreport " +
						"WHERE " +
						"	tagid = '" + tagDetails.getId() + "' " +
						"AND" +
						"	daytype = '"+dayType+"'";
				logger.debug(sql);
				s.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				throw new Exception(
						"There was an error deleting previous traffic flow reports for tag("+tagDetails.getName()+"): "
								+ e.getMessage());
			}

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		}
	}
	
	public void saveTraffiFlowReportRawData(ArrayList<ArrayList> rawData,
			TagDetails tagDetails, String dayType) throws Exception {
		
			
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date created = new Date();
		String ts = format.format(created);
		String tmpName = "/tmp/tamt-trafficflowreport-" + tagDetails.getId() + "-" + dayType + "-" + ts + ".txt";
		FileWriter fstream = new FileWriter(tmpName, true);
		BufferedWriter copy = new BufferedWriter(fstream);

		/*
		 * Now loop over the raw data and write each line, making sure
		 * to fetch a new sequence for each pid;
		 * 
		 */
		StringBuffer sb = new StringBuffer();
		String tagId = tagDetails.getId();
		String regionId = tagDetails.getRegion().getId();
		for (Iterator iterator = rawData.iterator(); iterator.hasNext();) {
			ArrayList rowData = (ArrayList) iterator.next();
			// Line format should be:
			// pid,regionid,tagid,created,hour_bin,w2,w3,pc,tx,ldv,ldc,hdc,mdb,hdb\n
			int pid = getNextTrafficFlowReportSequenceValue();
			sb.append(pid);
			sb.append(DELIMITER);
			sb.append(regionId);
			sb.append(DELIMITER);
			sb.append(tagId);
			sb.append(DELIMITER);
			sb.append(dayType);
			sb.append(DELIMITER);
			sb.append(created);
			sb.append(DELIMITER);
			sb.append(rowData.get(0));
			sb.append(DELIMITER);
			sb.append(rowData.get(1));
			sb.append(DELIMITER);
			sb.append(rowData.get(2));
			sb.append(DELIMITER);
			sb.append(rowData.get(3));
			sb.append(DELIMITER);
			sb.append(rowData.get(4));
			sb.append(DELIMITER);
			sb.append(rowData.get(5));
			sb.append(DELIMITER);
			sb.append(rowData.get(6));
			sb.append(DELIMITER);
			sb.append(rowData.get(7));
			sb.append(DELIMITER);
			sb.append(rowData.get(8));
			sb.append(DELIMITER);
			sb.append(rowData.get(9));
			sb.append("\n");
		}
		copy.write(sb.toString());
		copy.close();
		
		// now COPY the data into the table en masse
		try {
			
			try {
				Connection connection = getConnection();
				Statement s = connection.createStatement();
				String sql = "COPY trafficflowreport FROM '" + tmpName + "' WITH DELIMITER ','";
				logger.debug(sql);
				s.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				throw new Exception(
						"There was an error copying the traffic flow report to the database: "
								+ e.getMessage());
			}

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		}
	    
	    // Not deleting right now because the delete is happening before
	    // the copy is completed...these will disappear when the VM is rebooted anyway
	    //File copyFile = new File(tmpName);
	    //copyFile.delete();
	    
	}
	
	private int getNextTrafficFlowReportSequenceValue() throws Exception {
		int seqVal = 1;
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT nextval('trafficflowreport_pid_seq')";
			ResultSet r = s.executeQuery(sql);
			while (r.next()) {
				seqVal = r.getInt(1);
			}
			connection.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception(
					"There was an error fetching the next traffic flow report sequence: "
							+ e.getMessage());
		}
		return seqVal;
	}

	public TrafficFlowReport getTrafficFlowReport(TagDetails tagDetails, String dayType) throws Exception {
		
		TrafficFlowReport report = null;
		String tagId = tagDetails.getId();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT  " +
					"created, " +
					"hour_bin, " +
					"w2, " + 
					"w3, " + 
					"pc, " + 
					"tx, " + 
					"ldv, " + 
					"ldc, " + 
					"hdc, " + 
					"mdb, " + 
					"hdb " + 
					"FROM trafficflowreport " +
					"WHERE tagid = '"+tagId+"' " +
					"AND daytype = '"+dayType+"'";
			logger.debug("getTrafficFlowReport sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			report = new TrafficFlowReport();
			report.setTagId(tagId);
			report.setDayType(dayType);
			
			ArrayList<ArrayList> dayTypeValues = new ArrayList<ArrayList>();
			
			while( r.next() ) { 
				
				ArrayList<String> values = new ArrayList<String>();
				report.setCreated(r.getDate(1)); // set multiple times, oh well.
				
				values.add(r.getString(2)); // hour_bin
				
				values.add(formatter.format(r.getDouble(3)));
				values.add(formatter.format(r.getDouble(4)));
				values.add(formatter.format(r.getDouble(5)));
				values.add(formatter.format(r.getDouble(6)));
				values.add(formatter.format(r.getDouble(7)));
				values.add(formatter.format(r.getDouble(8)));
				values.add(formatter.format(r.getDouble(9)));
				values.add(formatter.format(r.getDouble(10)));
				values.add(formatter.format(r.getDouble(11)));
			
				dayTypeValues.add(values);
			}
			
			report.setDayTypeValues(dayTypeValues);
			
			connection.close(); // returns connection to the pool

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 		
	    //logger.debug("report=" + report);
		return report;
		
	}	
	
}
