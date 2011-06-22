package org.worldbank.transport.tamt.server.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.worldbank.transport.tamt.jndi.mock.MockIntialContextFactory;
import org.worldbank.transport.tamt.shared.SpeedDistributionRecord;

public class ParseGPRMCTest {

	static Logger logger = Logger.getLogger(ParseGPRMCTest.class);
	static String FILENAME = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/nmea-test.DAT";
	static String ACTUALDATALOGGER = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/Datalog_out.nma";
	static String DCDATA = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/dc-area.txt";
	static String ACCRADATA = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/accra.txt";
	
	@Test
	public void testRegex()
	{
		
		logger.debug("parse GPRMC lines only");
		String patternGPRMC = "^\\$GPRMC.*";
		String output = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(FILENAME));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.matches(patternGPRMC))
				{
					logger.debug(line);	
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDataLogger()
	{
		
		logger.debug("parse GPRMC lines only from John's data logger file");
		String patternGPRMC = "^\\$GPRMC.*";
		String output = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(ACTUALDATALOGGER));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.matches(patternGPRMC))
				{
					logger.debug(line);	
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void parseWesternHemisphereData()
	{
		/*
		 * Get the DAO
		 */
		GPSTraceDAO gpsTraceDAO = new GPSTraceDAO();
		
		String patternGPRMC = "^\\$GPRMC.*";
		String output = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(DCDATA));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.matches(patternGPRMC))
				{
					logger.debug(line);	
					// removed
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void parseEasternHemisphereData()
	{
		/*
		 * Get the DAO
		 */
		GPSTraceDAO gpsTraceDAO = new GPSTraceDAO();
		
		String patternGPRMC = "^\\$GPRMC.*";
		String output = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(ACCRADATA));
			String line = null;
			while ((line = input.readLine()) != null) {
				if( line.matches(patternGPRMC))
				{
					logger.debug(line);	
					// removed
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	@Test
	public void parseDDMMSS()
	{
		// 3857.744480 = 38d, 57.744480
		// String p = "$(.*)(\\d{2})(\\d{2})\\.(.*)";
		// String p = "//";
		// String p = "^\\$GPRMC.*";
		Pattern pattern = Pattern.compile("(.*)(\\d{2})(\\d{2})\\.(.*)$");
		
		
		String l1 = "3857.744480";
		String l2 = "0534.7160";
		String l3 = "07706.010620";
		try {
			double c1 = parseCoordRegex(l1, pattern);
			logger.debug(c1);
			double c2 = parseCoordRegex(l2, pattern);
			logger.debug(c2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private double parseCoordRegex(String s, Pattern p) throws Exception
	{
		double coord = 0.0;
		logger.debug("s=" + s);
		Matcher m = p.matcher(s);
		if( m.matches())
		{
			double degrees = Double.parseDouble(m.group(2));
			logger.debug("degrees=" + degrees);
			String min = m.group(3) + "." + m.group(4);
			logger.debug("min=" + min);
			double minutes = Double.parseDouble(min);
			coord = degrees + ((minutes * 60) / 3600);
		} else {
			throw new Exception("There was an error parsing the coordinate");
		}
		return coord;
	}
	
	private void reportMatches(String s, Matcher m)
	{
		logger.debug("string=" + s);
		logger.debug("matches? " + m.matches());
		logger.debug("groups=" + m.groupCount());
		logger.debug("m1=" + m.group(1));
		logger.debug("m2=" + m.group(2));
		logger.debug("m3=" + m.group(3));
		logger.debug("m4=" + m.group(4));		
	}

}