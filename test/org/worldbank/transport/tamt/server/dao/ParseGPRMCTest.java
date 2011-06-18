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
	

}