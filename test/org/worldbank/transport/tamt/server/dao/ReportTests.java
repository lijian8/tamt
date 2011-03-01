package org.worldbank.transport.tamt.server.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.worldbank.transport.tamt.jndi.mock.MockIntialContextFactory;
import org.worldbank.transport.tamt.shared.SpeedDistributionRecord;

public class ReportTests {

	static TrafficFlowReportDAO trafficFlowReportDAO;
	static SpeedDistributionReportDAO speedDistributionReportDAO;
	static TripStatisticsReportDAO tripStatisticsReportDAO;
	static Logger logger = Logger.getLogger(ReportTests.class);
	
	public static void createJNDIContext() throws Exception
	{
		PGConnectionPoolDataSource ds = new PGConnectionPoolDataSource();
		ds.setServerName("localhost");
		ds.setPortNumber(5432);
		ds.setDatabaseName("tamt15");
		ds.setUser("gis");
		ds.setPassword("gis");
		ds.setDefaultAutoCommit(true);
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockIntialContextFactory.class.getName());
		InitialContext ic = new InitialContext();
		ic.bind("TAMTDataSource", ds);
	}
	
	@BeforeClass
	public static void runBeforeTests() throws Exception
	{
		createJNDIContext();
		trafficFlowReportDAO = TrafficFlowReportDAO.get();
		speedDistributionReportDAO = SpeedDistributionReportDAO.get();
		tripStatisticsReportDAO = TripStatisticsReportDAO.get();
	}


	@Test
	public void getTrafficFlowReportForRegion()
	{
		logger.debug("test getTrafficFlowReportForRegion");
		String output = null;
		try {
			output = trafficFlowReportDAO.getTrafficFlowReportForRegion();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}
	
	@Test
	public void downloadSpeedDistributionReportForRegion()
	{
		logger.debug("test downloadSpeedDistributionReportForRegion");
		String output = null;
		try {
			output = speedDistributionReportDAO.downloadSpeedDistributionReportForRegion();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}
	
	@Test
	public void downloadSpeedDistributionReportByTag()
	{
		logger.debug("test downloadSpeedDistributionReportForRegion");
		String output = null;
		try {
			// 3590f46c-6140-4555-bc73-75c7e381a843 or "4069ca90-d909-4d06-9b88-5e55d1c9ec2e"
			output = speedDistributionReportDAO.downloadSpeedDistributionReport("3590f46c-6140-4555-bc73-75c7e381a843");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	
	@Test
	public void downloadSpeedDistributionAggregateByDayTypeReportByTag()
	{
		logger.debug("test downloadSpeedDistributionAggregateByDayTypeReportByTag");
		String output = null;
		try {
			// 3590f46c-6140-4555-bc73-75c7e381a843 or "4069ca90-d909-4d06-9b88-5e55d1c9ec2e"
			output = speedDistributionReportDAO.downloadSpeedBinAggregateByDayTypeReport("4069ca90-d909-4d06-9b88-5e55d1c9ec2e");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	
	@Test
	public void downloadSpeedDistributionTrafficFlowReportForRegion()
	{
		logger.debug("test downloadSpeedDistributionTrafficFlowReportForRegion");
		String output = null;
		try {
			// null to get all tags for region
			output = speedDistributionReportDAO.downloadSpeedDistributionTrafficFlowReportForRegion();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	@Test
	public void downloadSpeedDistributionAggregateByDayTypeReportForRegion()
	{
		logger.debug("test downloadSpeedDistributionAggregateByDayTypeReportForRegion");
		String output = null;
		try {
			// null to get all tags for region
			output = speedDistributionReportDAO.downloadSpeedBinAggregateByDayTypeReport(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	
	@Test
	public void downloadSpeedDistributionAggregateByTagReport()
	{
		logger.debug("test downloadSpeedDistributionAggregateByTagReport");
		String output = null;
		try {
			// null to get all tags for region
			output = speedDistributionReportDAO.downloadSpeedBinAggregateByTagReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	
	@Test
	public void downloadTripStatisticsTripBinReport()
	{
		logger.debug("test downloadTripStatisticsTripBinReport");
		String output = null;
		try {
			output = tripStatisticsReportDAO.downloadTripStatisticsTripBinReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	
	
	@Test
	public void downloadTripStatisticsSoakBinReport()
	{
		logger.debug("test downloadTripStatisticsSoakBinReport");
		String output = null;
		try {
			output = tripStatisticsReportDAO.downloadTripStatisticsSoakBinReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug(output);
		assertNotNull(output);
	
	}	

}