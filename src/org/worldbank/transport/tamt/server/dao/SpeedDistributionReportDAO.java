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
			logger.debug("getSpeedDistribution DAO complete");
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
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String tagId = tagDetails.getId();
			
			String sql = "SELECT * FROM speeddistributiontrafficflow WHERE tagid = '"+tagId+"' ORDER BY dayType desc, vehicletype, speedbin";
			
			logger.debug("SQL for getSpeedDistributionTrafficFlowReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();

				// skip first columns (tagid)
				row.add(r.getString(2)); // daytype
				row.add(r.getString(3)); // vehicletype
				row.add(r.getString(4)); // speedbin
				row.add(r.getString(5)); // vehiclesecondsinbin
				row.add(r.getString(6)); // vehiclemetersinbin
				row.add(r.getString(7)); // avgmeterspersecond
				row.add(r.getString(8)); // percentsecondsinbinperday
				row.add(r.getString(9)); // percentmetersinbinperday
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
			
			String sql = "SELECT * FROM speeddistributiontrafficflowvehiclespeed ORDER BY vehicletype, speedbin";
			
			logger.debug("SQL for getSpeedDistributionAggregateByTagTypeReport: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
			while (r.next()) {
				
				ArrayList<String> row = new ArrayList<String>();

				row.add(r.getString(1)); // vehicletype
				row.add(r.getString(2)); // speedbin
				row.add(r.getString(3)); // meterspersecond
				row.add(r.getString(4)); // tagmeters
				row.add(r.getString(5)); // tagseconds
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
	
}
