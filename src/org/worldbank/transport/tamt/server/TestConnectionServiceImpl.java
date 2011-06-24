package org.worldbank.transport.tamt.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.client.services.BackupService;
import org.worldbank.transport.tamt.client.services.TestConnectionService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TestConnectionServiceImpl extends RemoteServiceServlet implements TestConnectionService {

	private static final long serialVersionUID = 4374932318909875215L;
	static Logger logger = Logger.getLogger(TestConnectionServiceImpl.class);
	 
	public TestConnectionServiceImpl()
	{
		
	}

	@Override
	public String testConnection() throws Exception {
		/*
		 * Test an internet connection to a remote server,
		 * like, say: code.google.com/p/tamt. If we get
		 * an answer, return an "OK" string. If not,
		 * return a "NOCONNECTION" string. Let Main.java
		 * handle the display message to the user.
		 */
		logger.debug("Testing internet connection from TAMT VirtualBox to outside world");
		String connection = "OK";
		final URL url = new URL("http://code.google.com/p/tamt");
		try 
		{
			final URLConnection conn = url.openConnection();
			conn.connect();
		} catch (IOException e)
		{
			connection = "NOCONNECTION";
			logger.error("Could not connect:" + e.getMessage());
		}

		return connection;
	}
	
	
}
