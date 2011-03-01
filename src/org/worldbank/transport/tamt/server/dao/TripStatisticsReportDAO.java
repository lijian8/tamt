package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.TripStatisticsReport;

public class TripStatisticsReportDAO extends DAO {

	static Logger logger = Logger.getLogger(TripStatisticsReportDAO.class);
	
	private static TripStatisticsReportDAO singleton = null;
	public static TripStatisticsReportDAO get() {
		if (singleton == null) {
			singleton = new TripStatisticsReportDAO();
		}
		return singleton;
	}

	private String DELIMITER = ",";
	private NumberFormat formatter = new DecimalFormat("#0.00");
	
	public TripStatisticsReportDAO()
	{
		
	}

	public void createTripStatisticsReport() throws Exception {
		
		logger.debug("Begin calculating engine soak times and trip lengths");
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT TAMT_populateEngineSoakTimeAndTripLengthForCurrentRegion()";
			logger.debug(sql);
			s.executeQuery(sql);
		} catch (SQLException e) {

			logger.error(e.getMessage());
			throw new Exception(
					"There was an error populating the engine soak time and trip length: "
							+ e.getMessage());
		}	
		logger.debug("Finished calculating engine soak times and trip lengths");
		
	}

	public TripStatisticsReport getSoakBinReport() throws Exception {
		
		TripStatisticsReport report = new TripStatisticsReport();
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "SELECT t.* FROM soakbin t, studyregion s WHERE s.iscurrentregion = true AND s.id = t.regionid ORDER BY t.binnumber";			
			logger.debug("SQL for getSoakBinReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();
				
				// skip first row (regionid)
				row.add(r.getString(2)); // binnumber
				row.add(r.getString(3)); // bincount
				
				rows.add(row);
				
			}

			report.setReportValues(rows);
			logger.debug("getSoakBinReport DAO complete");
			return report;
		
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}		
	}

	public TripStatisticsReport getTripStatisticsReport() throws Exception {
		
		TripStatisticsReport report = new TripStatisticsReport();
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "SELECT t.* FROM tripbin t, studyregion s WHERE s.iscurrentregion = true AND s.id = t.regionid ORDER BY t.binnumber";			
			logger.debug("SQL for getTripStatisticsReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();
				
				// skip first row (regionid)
				row.add(r.getString(2)); // binnumber
				row.add(r.getString(3)); // bincount
				row.add(r.getString(4)); // avgtriplength
				row.add(r.getString(5)); // avgspeed
				
				rows.add(row);
				
			}

			report.setReportValues(rows);
			logger.debug("getTripStatisticsReport DAO complete");
			return report;
		
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}		
	}

	public String downloadTripStatisticsTripBinReport() throws Exception {

		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,TRIPBIN,TRIPCOUNT,AVG(m),AVG(m/s)\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
					"s.name, t.binnumber,t.bincount,t.avgtriplength,t.avgspeed "+
					"from tripbin t, studyregion s "+
					"where s.iscurrentregion = true "+
					"ORDER BY t.binnumber";
			
			logger.debug("downloadTripStatisticsTripBinReport() sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			//formatter = new DecimalFormat("#0.00");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // binnumber
				sb.append(r.getString(3) + ","); // bincount
				sb.append(formatter.format(r.getDouble(4)) + ","); // avgtriplength
				sb.append(formatter.format(r.getDouble(5)) + "\n"); // avgspeed
			}
			
			output = sb.toString();
			
			//formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	    
	}

	public String downloadTripStatisticsSoakBinReport() throws Exception {

		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,SOAKBIN,SOAKCOUNT\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
					"s.name, t.binnumber,t.bincount "+
					"from soakbin t, studyregion s "+
					"where s.iscurrentregion = true "+
					"ORDER BY t.binnumber";
			
			logger.debug("downloadTripStatisticsSoakBinReport() sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			//formatter = new DecimalFormat("#0.00");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // binnumber
				sb.append(r.getString(3) + "\n"); // bincount
			}
			
			output = sb.toString();
			
			//formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	}

	
}