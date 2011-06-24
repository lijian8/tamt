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
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;

public class SpeedDistributionReportDAO extends DAO {

	static Logger logger = Logger.getLogger(SpeedDistributionReportDAO.class);
	
	private SpeedBinDAO speedBinDAO;
	
	private static SpeedDistributionReportDAO singleton = null;
	public static SpeedDistributionReportDAO get() {
		if (singleton == null) {
			singleton = new SpeedDistributionReportDAO();
		}
		return singleton;
	}

	private String DELIMITER = ",";
	private NumberFormat formatter = new DecimalFormat("#0.00");
	
	public SpeedDistributionReportDAO()
	{
		speedBinDAO = SpeedBinDAO.get();
	}

	public SpeedDistributionReport getSpeedDistributionReport(TagDetails tagDetails) throws Exception
	{

		SpeedDistributionReport report = new SpeedDistributionReport();
		report.setTagId(tagDetails.getId());
		report.setCreated(new Date());
		
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String tagId = tagDetails.getId();
			
			String sql = "SELECT * FROM speeddistribution WHERE tagid = '"+tagId+"' ORDER BY dayType desc, hourbin, speedbin";
			
			logger.debug("SQL for getSpeedDistributionReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();
				
				// skip first two columns (oid, tagid)
				row.add(r.getString(3)); // daytype
				row.add(r.getString(4)); // hourbin
				row.add(r.getString(5)); // speedbin
				row.add(r.getString(6)); // secondsinbin
				row.add(r.getString(7)); // metersinbin
				row.add(r.getString(8)); // avgmeterspersecond
				row.add(r.getString(9)); // percentsecondsinbin
				row.add(r.getString(10)); // percentmetersinbin
				
				rows.add(row);
				
			}

			report.setReporValues(rows);
			logger.debug("getSpeedDistribution DAO complete: rowcount=" + rows.size());
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

	public void createSpeedDistributionReport() throws Exception {
		/*
		 * Fire the stored procedure which populates
		 * the speed distribution table
		 */
		try {
			speedBinDAO.populateSpeedDistribution();
			speedBinDAO.populateSpeedDistObserved();
			speedBinDAO.interpolateSpeedDistribution();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}	
	
	public void createSpeedDistributionTrafficFlowReport() throws Exception {
		try {
			speedBinDAO.combineSpeedDistributionTrafficFlow();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}
	
	public void createSpeedDistributionAggregateByDayTypeReport() throws Exception {
		try {
			speedBinDAO.removeDayTypeFromSpeedDistributionTrafficFlow();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}	

	public void createSpeedDistributionAggregateByTagReport() throws Exception {
		try {
			speedBinDAO.removeTagFromSpeedDistributionTrafficFlowTagVehicleSpeed();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}
	
	public SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(
			TagDetails tagDetails) throws Exception {

		SpeedDistributionTrafficFlowReport report = new SpeedDistributionTrafficFlowReport();
		report.setTagId(tagDetails.getId());
		report.setCreated(new Date());
		
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String tagId = tagDetails.getId();
			
			String sql = "SELECT * FROM speeddistributiontrafficflow WHERE tagid = '"+tagId+"' ORDER BY dayType desc, vehicletype, speedbin";
			
			logger.debug("SQL for getSpeedDistributionTrafficFlowReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();

				// skip first column (tagid)
				row.add(r.getString(2)); // daytype
				row.add(r.getString(3)); // vehicletype
				row.add(r.getString(4)); // speedbin
				
				// skip the last two year-based columns (s and m)
				// and insert vkt here. Get as a double, then round to 2 decimal places
				Double vktDouble = r.getDouble(12);
				String vkt = "";
				if( vktDouble != null)
				{
					vkt = formatter.format(vktDouble.doubleValue());
				}
				row.add(vkt);
				
				row.add(r.getString(5)); // vehiclesecondsperday
				row.add(r.getString(6)); // vehiclemetersperday
				row.add(r.getString(7)); // weightedaveragespeed
				row.add(r.getString(8)); // %s/d
				row.add(r.getString(9)); // %m/d
				rows.add(row);
				
			}

			logger.debug("row count=" + rows.size());
			report.setReporValues(rows);
			logger.debug("getSpeedDistributionTrafficFlowReport DAO complete");
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

	public SpeedDistributionAggregateByTagReport getSpeedDistributionAggregateByTagReport() throws Exception {

		SpeedDistributionAggregateByTagReport report = new SpeedDistributionAggregateByTagReport();
		report.setCreated(new Date());
		
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// need to query only the current study region
			String sql = "SELECT * FROM speeddistributiontrafficflowvehiclespeed " +
					" WHERE regionid = (SELECT id FROM studyregion WHERE iscurrentregion = TRUE) " +
					"ORDER BY vehicletype, speedbin";
			
			logger.debug("SQL for getSpeedDistributionAggregateByTagTypeReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();

				/*
				  vehicletype text,
				  speedbin integer,
				  meterspersecond double precision,
				  tagmeters double precision,
				  tagseconds double precision,
				  regionid text,
				  taghours double precision,
				  tagkilometers double precision,
				  tagkph double precision
				 */
				// skip the first column (regionid)
				row.add(r.getString(1)); // vehicletype
				row.add(r.getString(2)); // speedbin
				// DON'T NEED TO SHOW THESE IN THE UI
				//row.add(r.getString(3)); // meterspersecond
				//row.add(r.getString(4)); // tagmeters
				//row.add(r.getString(5)); // tagseconds
				//row.add(r.getString(6)); // regionid
				row.add(r.getString(7)); // taghours
				row.add(r.getString(8)); // tagkilometers
				row.add(r.getString(9)); // tagkph
				rows.add(row);
				
			}

			logger.debug("row count=" + rows.size());
			report.setReporValues(rows);
			logger.debug("getSpeedDistributionAggregateByTagTypeReport DAO complete");
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
	
	public SpeedDistributionAggregateByDayTypeReport getSpeedDistributionAggregateByDayTypeReport(
			TagDetails tagDetails) throws Exception {

		SpeedDistributionAggregateByDayTypeReport report = new SpeedDistributionAggregateByDayTypeReport();
		report.setTagId(tagDetails.getId());
		report.setCreated(new Date());
		
		ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String tagId = tagDetails.getId();
			
			String sql = "SELECT * FROM speeddistributiontrafficflowtagvehiclespeed WHERE tagid = '"+tagId+"' ORDER BY vehicletype, speedbin";
			
			logger.debug("SQL for getSpeedDistributionAggregateByDayTypeReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();

				row.add(r.getString(2)); // vehicletype
				row.add(r.getString(3)); // speedbin
				row.add(r.getString(4)); // percentvehiclesecondsperyear
				row.add(r.getString(5)); // percentvehiclemetersperyear
				row.add(r.getString(6)); // averagemeterspersecond
				rows.add(row);
				
			}

			logger.debug("row count=" + rows.size());
			report.setReporValues(rows);
			logger.debug("getSpeedDistributionAggregateByDayTypeReport DAO complete");
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

	public String downloadSpeedDistributionReportForRegion() throws Exception {
		
		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,TAG,DAYTYPE,HOURBIN,SPEEDBIN,SECONDSINBIN,METERSINBIN,AVGMETERSPERSECOND,PERCENTSECONDSINBIN,PERCENTMETERSINBIN\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
			"s.name as region, "+
			"t.name as tag, "+
			"sd.daytype,sd.hourbin,sd.speedbin,sd.secondsinbin,sd.metersinbin, "+
			"sd.avgmeterspersecond,sd.percentsecondsinbin,sd.percentmetersinbin	"+
			"from speeddistribution sd,tagdetails t,studyregion s "+
			"where sd.tagid = t.id and t.region = s.id " +
			"AND s.iscurrentregion=true "+
			"order by s.name, t.name, sd.daytype desc, sd.hourbin, sd.speedbin ";
			
			logger.debug("downloadSpeedDistributionReportForRegion sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			formatter = new DecimalFormat("#0.0000");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // tag
				sb.append(r.getString(3) + ","); // daytype
				sb.append(r.getString(4) + ","); // hour
				sb.append(r.getString(5) + ","); // speedbin
				sb.append(formatter.format(r.getDouble(6)) + ","); // secondsinbin
				sb.append(formatter.format(r.getDouble(7)) + ","); // metersinbin
				sb.append(formatter.format(r.getDouble(8)) + ","); // avgmeterspersecond
				sb.append(formatter.format(r.getDouble(9)) + ","); // percentsecondsinbin
				sb.append(formatter.format(r.getDouble(10)) + "\n"); // percentmetersinbin
			}
			
			output = sb.toString();
			
			formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	}	

	public String downloadSpeedDistributionReport(String tagId) throws Exception {

		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,TAG,DAYTYPE,HOURBIN,SPEEDBIN,SECONDSINBIN,METERSINBIN,AVGMETERSPERSECOND,PERCENTSECONDSINBIN,PERCENTMETERSINBIN\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
			"s.name as region, "+
			"t.name as tag, "+
			"sd.daytype,sd.hourbin,sd.speedbin,sd.secondsinbin,sd.metersinbin, "+
			"sd.avgmeterspersecond,sd.percentsecondsinbin,sd.percentmetersinbin	"+
			"from speeddistribution sd,tagdetails t,studyregion s "+
			"where sd.tagid = t.id and t.region = s.id " +
			"AND sd.tagid = '"+tagId+"'"+
			"order by s.name, t.name, sd.daytype desc, sd.hourbin, sd.speedbin ";
			
			logger.debug("downloadSpeedDistributionReport(tagId) sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			formatter = new DecimalFormat("#0.0000");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // tag
				sb.append(r.getString(3) + ","); // daytype
				sb.append(r.getString(4) + ","); // hour
				sb.append(r.getString(5) + ","); // speedbin
				sb.append(formatter.format(r.getDouble(6)) + ","); // secondsinbin
				sb.append(formatter.format(r.getDouble(7)) + ","); // metersinbin
				sb.append(formatter.format(r.getDouble(8)) + ","); // avgmeterspersecond
				sb.append(formatter.format(r.getDouble(9)) + ","); // percentsecondsinbin
				sb.append(formatter.format(r.getDouble(10)) + "\n"); // percentmetersinbin
			}
			
			output = sb.toString();
			
			formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	}

	public String downloadSpeedDistributionTrafficFlowReport(String tagId) throws Exception {

		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,TAG,DAYTYPE,VEHICLETYPE,SPEEDBIN,VKT,(s)/d,(m)/d,AVERAGE m/s,%(s)/d,%(m)/d\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
			"s.name as region,"+
			"t.name as tag,"+
			"sd.daytype,sd.vehicletype,sd.speedbin," +
			"round(sd.vkt::numeric, 2)," + // need to insert VKT here
			"sd.vehiclesecondsperday,sd.vehiclemetersperday,"+
			"sd.weightedaveragespeed,sd.percentvehiclesecondsperday,sd.percentvehiclemetersperday "+
			"from speeddistributiontrafficflow sd,tagdetails t,studyregion s "+
			"where sd.tagid = t.id and t.region = s.id " +
			"AND s.iscurrentregion=true ";
			
			if(tagId != null)
			{
				sql += "AND sd.tagid = '"+tagId+"'";
			}
			
			sql += "order by s.name, t.name, sd.daytype desc, sd.vehicletype, sd.speedbin";
			
			logger.debug("downloadSpeedDistributionReport(tagId) sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			formatter = new DecimalFormat("#0.0000");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // tag
				sb.append(r.getString(3) + ","); // daytype
				sb.append(r.getString(4) + ","); // vehicle
				sb.append(r.getString(5) + ","); // speedbin
				sb.append(r.getString(6) + ","); // vkt
				sb.append(formatter.format(r.getDouble(7)) + ","); // vehiclesecondsperday
				sb.append(formatter.format(r.getDouble(8)) + ","); // vehiclemetersperday
				sb.append(formatter.format(r.getDouble(9)) + ","); // weightedaveragespeed
				sb.append(formatter.format(r.getDouble(10)) + ","); // percentvehiclesecondsperday
				sb.append(formatter.format(r.getDouble(11)) + "\n"); // percentvehiclemetersperday
			}
			
			output = sb.toString();
			
			formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	    
	}

	public String downloadSpeedDistributionTrafficFlowReportForRegion() throws Exception {
		return downloadSpeedDistributionTrafficFlowReport(null);
	}

	public String downloadSpeedBinAggregateByDayTypeReport(String tagId) throws Exception {

		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,TAG,VEHICLETYPE,SPEEDBIN,%(s)/y,%(m)/d,AVERAGE m/s\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "select "+
			"s.name as region,"+
			"t.name as tag,"+
			"sd.vehicletype,sd.speedbin,sd.percentvehiclesecondsperyear,sd.percentvehiclemetersperyear, sd.averagemeterspersecond "+
			"from speeddistributiontrafficflowtagvehiclespeed sd, tagdetails t, studyregion s "+
			"where sd.tagid = t.id and t.region = s.id " +
			"AND s.iscurrentregion=true ";
			
			if(tagId != null)
			{
				sql += "AND sd.tagid = '"+tagId+"'";
			}
			
			sql += "order by s.name, t.name, sd.vehicletype, sd.speedbin";
			
			logger.debug("downloadSpeedBinAggregateByDayTypeReport(tagId) sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			formatter = new DecimalFormat("#0.0000");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // tag
				sb.append(r.getString(3) + ","); // vehicle
				sb.append(r.getString(4) + ","); // speedbin
				sb.append(formatter.format(r.getDouble(5)) + ","); // percentvehiclesecondsperyear
				sb.append(formatter.format(r.getDouble(6)) + ","); // percentvehiclemetersperyear
				sb.append(formatter.format(r.getDouble(7)) + "\n"); // averagemeterspersecond
			}
			
			output = sb.toString();
			
			formatter = new DecimalFormat("#0.00");
			
			connection.close(); // returns connection to the pool

		} 
	    catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
			
		} 	
	    return output;
	}

	public String downloadSpeedBinAggregateByDayTypeReportForRegion() throws Exception {
		return downloadSpeedBinAggregateByDayTypeReport(null);
	}

	public String downloadSpeedBinAggregateByTagReport() throws Exception {
		
		String output = "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("REGION,VEHICLETYPE,SPEEDBIN,TAG(km),TAG(hr),TAG(kph)\n");
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "SELECT s.name,sd.vehicletype,sd.speedbin,sd.tagkilometers,sd.taghours,sd.tagkph "+
			"FROM speeddistributiontrafficflowvehiclespeed sd,studyregion s "+
			"WHERE sd.regionid = s.id "+
			"AND s.iscurrentregion=true ORDER BY vehicletype, speedbin";
			
			logger.debug("downloadSpeedBinAggregateByTagReport() sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			
			//formatter = new DecimalFormat("#0.00");
			
			while( r.next() ) { 
				
				sb.append(r.getString(1) + ","); // region
				sb.append(r.getString(2) + ","); // vehicle
				sb.append(r.getString(3) + ","); // speedbin
				sb.append(formatter.format(r.getDouble(4)) + ","); // tagkilometers
				sb.append(formatter.format(r.getDouble(5)) + ","); // taghours
				sb.append(formatter.format(r.getDouble(6)) + "\n"); // tagkph
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
