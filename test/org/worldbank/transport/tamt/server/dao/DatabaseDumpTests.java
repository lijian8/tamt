package org.worldbank.transport.tamt.server.dao;

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

public class DatabaseDumpTests {

	static Logger logger = Logger.getLogger(DatabaseDumpTests.class);
	
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
	public void systemLS()
	{
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec("ls -l");
			InputStream in = p.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(in);
			InputStreamReader inread = new InputStreamReader(buf);
			BufferedReader bufferedreader = new BufferedReader(inread);
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				logger.debug(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

}