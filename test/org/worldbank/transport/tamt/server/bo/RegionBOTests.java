package org.worldbank.transport.tamt.server.bo;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.worldbank.transport.tamt.shared.StudyRegion;

public class RegionBOTests {

	static Logger logger = Logger.getLogger(RegionBOTests.class);
	static RegionBO regionBO = RegionBO.get();
	
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
		
	}

	@Test
	public void test()
	{
		logger.debug("this is a test");
	}
	
	@Test
	public void validateStudyRegion()
	{
		StudyRegion r = new StudyRegion();
		r.setName("stuartmoffatt");
		logger.debug(r.getName().indexOf(","));
		assertEquals(-1, r.getName().indexOf(","));
	}
	
	@Test
	public void validateMinimumSoakIntervalEmpty()
	{
		StudyRegion studyRegion = new StudyRegion();
		studyRegion.setMinimumSoakInterval("1");
		if( !studyRegion.getMinimumSoakInterval().matches("[0-9]*") )
		{
			try {
				throw new Exception("Minimum soak interval must be an integer (in seconds)");
			} catch (Exception e) {
				logger.error(e.getMessage());
				assertNull(e);
			}
			
		}
		assertTrue(true);
	}	
	
	@Test
	public void validateUTCOffset()
	{
		StudyRegion studyRegion = new StudyRegion();
		studyRegion.setUtcOffset("12");
		if( !studyRegion.getUtcOffset().matches("^-{0,1}[0-9]*") )
		{
				try {
					throw new Exception("UTC offset must be an integer (in hours)");
				} catch (Exception e) {
					logger.error(e.getMessage());
					assertNull(e);
				}
		}
		assertTrue(true);
	}

	@Test
	public void validateStudyRegionBO()
	{
		StudyRegion r = new StudyRegion();
		r.setName("accra, ghana");
		try {
			regionBO.validateStudyRegion(r);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}