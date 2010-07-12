package org.worldbank.transport.tamt.server.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TAMTPoint;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GPSTraceDAO extends DAO {
	
	static Logger logger = Logger.getLogger(GPSTraceDAO.class);
	protected Pattern pattern = Pattern.compile(",\\s*");
	protected final String DELIMITER = "\t";
	
	public GPSTraceDAO()
	{
		init();
	}
	
	public ArrayList<GPSTrace> getGPSTraces(StudyRegion region) throws Exception
	{
		ArrayList<GPSTrace> gpsTraceList = new ArrayList<GPSTrace>();
		try {
			Statement s = connection.createStatement();
			String sql = "select id, name, description, region, fileId, uploadDate, processDate, recordCount from \"gpsTraces\" where region = '"+region.getName()+"' ORDER BY name";
			ResultSet r = s.executeQuery(sql);
			while( r.next() ) { 
			      /* 
			      * Retrieve the geometry as an object then cast it to the geometry type. 
			      * Print things out. 
			      */ 
				  String id = r.getString(1);
			      String name = r.getString(2);
			      String description = r.getString(3);
			      String regionName = r.getString(4);
			      String fileId = r.getString(5);
			      Date uploadDate = r.getTimestamp(6);
			      Date processDate = r.getTimestamp(7);
			      int recordCount = r.getInt(8);
			      
			      GPSTrace gpsTrace = new GPSTrace();
			      gpsTrace.setId(id);
			      gpsTrace.setName(name);
			      gpsTrace.setDescription(description);
			      gpsTrace.setRegion(region); // came in as param, no need to reset
			      gpsTrace.setFileId(fileId);
			      gpsTrace.setUploadDate(uploadDate);
			      gpsTrace.setProcessDate(processDate);
			      gpsTrace.setRecordCount(recordCount);
			      
			     /*
			      * If process date is after upload date, we know this 
			      * is processed. (Saving trace in BO sets process date to
			      * yesterday on upload). Default for processed flag is false.
			      */
			      if( processDate.after(uploadDate)) {
			    	  gpsTrace.setProcessed(true);
			      }
			      
			      gpsTraceList.add(gpsTrace);
			} 
    
		} 
	    catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: " + e.getMessage());
		} 
	    catch (Exception e) {
	    	logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
		return gpsTraceList;
		
	}
	
	public GPSTrace getGPSTraceById(String id) throws Exception
	{
		GPSTrace gpsTrace = new GPSTrace();
		try {
			Statement s = connection.createStatement();
			String sql = "select id, name, description, region, fileId, uploadDate, processDate, recordCount from \"gpsTraces\" where id = '"+id+"' ORDER BY name";
			ResultSet r = s.executeQuery(sql);
			while( r.next() ) { 
			      /* 
			      * Retrieve the geometry as an object then cast it to the geometry type. 
			      * Print things out. 
			      */ 
				  String name = r.getString(2);
			      String description = r.getString(3);
			      String regionName = r.getString(4);
			      String fileId = r.getString(5);
			      Date uploadDate = r.getTimestamp(6);
			      Date processDate = r.getTimestamp(7);
			      int recordCount = r.getInt(8);
			      
			      gpsTrace.setId(id);
			      gpsTrace.setName(name);
			      gpsTrace.setDescription(description);
			      
			      StudyRegion region = new StudyRegion();
			      region.setName(regionName);
			      
			      gpsTrace.setRegion(region);
			      gpsTrace.setFileId(fileId);
			      gpsTrace.setUploadDate(uploadDate);
			      gpsTrace.setProcessDate(processDate);
			      
			     /*
			      * If process date is after upload date, we know this 
			      * is processed. (Saving trace in BO sets process date to
			      * yesterday on upload). Default for processed flag is false.
			      */
			      if( processDate.after(uploadDate)) {
			    	  gpsTrace.setProcessed(true);
			      }
			      
			      gpsTrace.setRecordCount(recordCount);
			      
			} 
    
		} 
	    catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: " + e.getMessage());
		} 
	    catch (Exception e) {
	    	logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
		return gpsTrace;
		
	}

	public void saveFile(File file, String fileId) throws Exception {
		try {
			
			Timestamp created = new Timestamp( (new java.util.Date()).getTime() );
			logger.debug("created=" + created.toString());
			
			FileInputStream fis = new FileInputStream(file);
			
			/*
			 * setBinaryStream is not yet implemented in JDBC4 with postgres drivers,
			 * so we need to convert the file to a byte[] 
			 */
			byte[] b = new byte[(int) file.length()];
			fis.read(b);
			
			// id, bytes, created
			PreparedStatement ps = connection.prepareStatement("INSERT INTO \"gpsFiles\" VALUES (?, ?, ?)");
			ps.setString(1, fileId);
			ps.setBytes(2, b);
			ps.setTimestamp(3, created);
			ps.executeUpdate();
			
		} catch (SQLException e)
		{
			logger.error(e.getMessage());
			throw e;
		} catch (IOException e)
		{
			logger.error(e.getMessage());
			throw new Exception("Could not convert GPS file to binary database format");
		}
	}
	
	public GPSTrace saveGPSTrace(GPSTrace gpsTrace) throws SQLException {

		try {
			Statement s = connection.createStatement();
			String sql = "INSERT INTO \"gpsTraces\" (id, name, description, region, fileId, uploadDate, processDate, recordCount) " +
					"VALUES ('"+gpsTrace.getId()+"', " +
					"'"+gpsTrace.getName()+"'," +
					"'"+gpsTrace.getDescription()+"'," +
					"'default', " +
					"'"+gpsTrace.getFileId()+"', " +
					"'"+gpsTrace.getUploadDate()+"', " +
					"'"+gpsTrace.getProcessDate()+"'," +
					"'"+gpsTrace.getRecordCount()+"' " +
					//"'"+zoneDetails.getZoneType()+"'," +
					//"GeometryFromText('"+geometry.toText()+"', 4326)" +
					")";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    
	    return gpsTrace;
	    
	}
	
	public GPSTrace updateGPSTrace(GPSTrace gpsTrace) throws SQLException {
		try {
			Statement s = connection.createStatement();
			// TODO: extend the model to include regionName string or region StudyRegion as property of GPSTrace
			// for now we just use 'default'
			String sql = "UPDATE \"gpsTraces\" SET " +
					" name = '"+gpsTrace.getName()+"'," +
					" description = '"+gpsTrace.getDescription()+"'," +
					" region = 'default'," +
					" fileId = '"+gpsTrace.getFileId()+"', " +
					" uploadDate = '"+gpsTrace.getUploadDate()+"', " +
					" processDate = '"+gpsTrace.getProcessDate()+"', " +
					" recordCount = '"+gpsTrace.getRecordCount()+"' " +
					"WHERE id = '"+gpsTrace.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} 
	    
	    return gpsTrace;
	}	

	public void processGPSTraces(ArrayList<String> gpsTraceIds) throws Exception {
		for (Iterator iterator = gpsTraceIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			processGPSTraceById(id);
		}
	}
	
	public void processGPSTraceById(String id) throws Exception
	{
		try {
			logger.debug("processing traces from id="+id);
			
			// fetch the GPSTrace by id
			GPSTrace gpsTrace = getGPSTraceById(id);
			logger.debug("gpsTrace=" + gpsTrace);
			
			// use the fileId from the trace to get the archived bytes
			processTraceContents(gpsTrace);
			
		}
		catch (Exception e) {
			
			logger.error(e.getMessage());
			
			// we need to delete the trace if we can't process it
			deleteGPSTraceById(id);
			
			// then throw the error
			throw e;
		}
	}

	
	private void processTraceContents(GPSTrace gpsTrace) throws Exception {
		
		/*
		 * 1. Query for the byte array of id=zipId in gpsFiles
		 * 2. Create a ZipFile from the byte array
		 * 3. Process the entries in the ZipFile
		 * 		- format the lines for each ZipEntry like processLineSet
		 * 		- write them out to the filesystem /tmp
		 * 		- use the SQL COPY command to bring them all back is as TAMTPoints
		 * 		  with the associated zipId
		 * Throw whatever exceptions are needed
		 */
		String traceId = gpsTrace.getId();
		String zipId = gpsTrace.getFileId();
		
		// 1. Query for the byte array
		byte[] bytes = null;
		try {

			Statement s = connection.createStatement();
			
			String sql = "SELECT gpsfile FROM \"gpsFiles\" WHERE id = '"+zipId+"'";
			ResultSet r = s.executeQuery(sql);
			while( r.next() ) {
				bytes = r.getBytes(1);
			}
			
		} catch (SQLException e)
		{
			logger.error("Could not get bytes for zipId: " + e.getMessage());
			throw new Exception("Could not retrieve file contents from database");
		}
		
		// test the byte array
		if( bytes == null)
		{
			throw new Exception("File contents were empty");
		} else {
			logger.debug("we have a byte array with length=" + bytes.length);
		}
		
		// now that we have bytes, write it temporarily to filesystem with a timestamp
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String ts = format.format(new Date());
		
		String tmpName = "/tmp/tamt-tmp-" + zipId+"-"+ts+".zip";
		File tmpFile = new File(tmpName);
		FileOutputStream fos = new FileOutputStream(tmpFile);
		fos.write(bytes);
		fos.flush();
		fos.close();
		
		// and read it again as a zipfile
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(tmpName);
		} catch (IOException e)
		{
			throw new Exception("The file was blank or could not be opened as a ZIP archive");
		}
		Enumeration entries = zipFile.entries();
		
		// loop on the zip entries and write all the contents in the TAMTPoint schema format to a single file
		String copyFileName = "/tmp/tamt-tmp-" + zipId+"-"+ts+".copy";
		FileWriter fstream = new FileWriter(copyFileName,true);
        BufferedWriter copy = new BufferedWriter(fstream);
    
        // a place holder for the GGA line
		String line2;
		int numRecords = 0;
		
		while(entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)entries.nextElement();
			InputStream is = zipFile.getInputStream(entry);
			if( is != null )
	        {
	        	String line;
	        	
	        	BufferedReader buff = new BufferedReader(new InputStreamReader(is));
	        	while(( line = buff.readLine()) != null)
	        	{
	        		if( line.indexOf("$GPRMC") != -1)
					{
						// this assumes that GPGGA is the only other captured sentence
						// and will not work if another sentence is recorded of if the
						// sentence sequence is NOT in the correct order (RMC, GGA)
						line2 = buff.readLine();
						try {
							//processLineSet(line, line2);
							
							/*
							 * Process the 2 lines to create a TAMTPoint
							 */
							TAMTPoint p = new TAMTPoint();
							p.setId( UUID.randomUUID().toString() );
							
							/*
							 * Extract the data from the GPRMC sentence (ie, line)
							 */
							String[] data = pattern.split(line);
							
							// timestamp from hhmmss, ddmmyy
							Date timestamp = parseDate(data[1], data[9]);
							p.setTimestamp(timestamp);
							
							// latitude (ddmm.ss), latitude hemisphere (N or S)
							double latitude = parseCoord(data[3], data[4]);
							p.setLatitude(latitude);
							
							// longitude (ddmm.ss), longitude hemisphere (E or W)
							double longitude = parseCoord(data[5], data[6]);
							p.setLongitude(longitude);
							
							// bearing 
							double bearing = Double.parseDouble(data[8]);
							p.setBearing(bearing);
							
							// speed (in knots)
							double speed = Double.parseDouble(data[7]);
							p.setSpeed(speed);
							
							/*
							 * Extract the data from the GPGGA sentence (ie, line2)
							 */
							String[] data2 = pattern.split(line2);
							
							// altitude
							p.setAltitude(Double.parseDouble(data2[9]));
							
							// altitude units
							p.setAltitudeUnits(data2[10]);
							
							// if we have not failed up to this point
							// (as indicated by fetching the timestamp -- kind of a hack)
							// then we can append the point to the COPY file
							if( p.getTimestamp() != null)
							{
								// append to a file in /tmp/
								StringBuffer sb = new StringBuffer();
								
								// id, gpsTraceId, lat, lng, bearing, speed, altitude, created
								sb.append(p.getId());
								sb.append(DELIMITER);
								sb.append(traceId); // the parent id of the GPSTrace containing the zip from which this point came
								sb.append(DELIMITER);
								sb.append(p.getLatitude());
								sb.append(DELIMITER);
								sb.append(p.getLongitude());
								sb.append(DELIMITER);
								sb.append(p.getBearing());
								sb.append(DELIMITER);
								sb.append(p.getSpeed());
								sb.append(DELIMITER);
								sb.append(p.getAltitude());
								sb.append(DELIMITER);
								sb.append(p.getTimestamp());
								sb.append("\n");
								
								try {
									copy.write(sb.toString());
									numRecords++;
								} catch (IOException e) {
									logger.error("Error writing COPY file: " + e.getMessage());
									throw new Exception("Error transfering point data to temporary file");
								}
							}
							
						} catch (NumberFormatException e)
						{
							// skip the line entry if there were problems with number conversions
							logger.error("error processing line set:" + e.getMessage());
							continue;
						} catch (SQLException e) {
							// skip the line entry if there were sql errors
							logger.error("error adding point to database:" + e.getMessage());
							continue;
						}
						
					}
	        	}
	        	
	        }
			
	        
		}

		logger.debug("numRecords="+numRecords);
		if( numRecords == 0)
		{
			throw new NonGPSArchiveException("No GPS records were found in the ZIP archive");
		}
		
		// finished reading the zipfile
        zipFile.close();
        
	      
        // finish writing the COPY file
        copy.close();
        
        // now, use postgresql COPY to move data into DB
        // with greater speed than individual INSERT statements per record
        // TODO: I wonder if this is slower now because of the fk in gpsPoints?
        // TODO: do I really need an FK if I am not ever doing a join? no FK would make deleting easier too, I think
        try {
			Statement s = connection.createStatement();
			String sql = "COPY \"gpsPoints\" FROM '"+copyFileName+"'";
			s.executeUpdate(sql);
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw new Exception("There was an error copying the point data to the database: " + e.getMessage());
		} 	
	    
	    gpsTrace.setProcessDate(new Date());
	    gpsTrace.setRecordCount(numRecords);
	    updateGPSTrace(gpsTrace);

	    // clean up the temporary files
	    tmpFile.delete(); // we had a handle on this one, so just delete it
	    File copyFile = new File(copyFileName); // we didn't habe a deletable handle, so recreate it here
	    copyFile.delete();
	    
	}	
	
	private double parseCoord(String ddmmss, String hemisphere)
	{
		double coord = 0;
		
		// degrees
		double degrees = Double.parseDouble(ddmmss.substring(0, 2));
		
		// minutes (with any leftover decimal seconds)
		double minutes = Double.parseDouble(ddmmss.substring(2, ddmmss.length()));
		
		// calc coord
		coord = degrees + ((minutes*60)/3600);
		
		// if hemisphere is W or S, then make coord negative
		if( hemisphere.equals("W") || hemisphere.equals("S"))
		{
			// coord = Math.abs(coord) * -1;
			coord = coord * -1;
		}
		
		return coord;
	}
	
	private Date parseDate(String hhmmss, String ddmmyy)
	{
		// first two chars of hhmmss are hours
		int hours = Integer.parseInt( hhmmss.substring(0, 2) );
		
		// next two chars of hhmmss are minutes
		int minutes = Integer.parseInt( hhmmss.substring(2, 4));
		
		// next two chars of hhmmss are seconds
		int seconds = Integer.parseInt( hhmmss.substring(4, 6) );
		
		// first two chars of ddmmyy are day
		int date = Integer.parseInt( ddmmyy.substring(0, 2) );
		
		// next two chars of ddmmyy are month
		int month = Integer.parseInt( ddmmyy.substring(2, 4) );
		
		// next two chars of ddmmyy are year
		int year = 100 + Integer.parseInt( ddmmyy.substring(4, 6)); // add 100 to 1900 to get into 21st century

		// create date
		Date d = new Date(year, month, date, hours, minutes, seconds);
		
		return d;
	}	

	public void deleteGPSTraces(ArrayList<String> gpsTraceIds) throws SQLException {
		for (Iterator iterator = gpsTraceIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			deleteGPSTraceById(id);
		}
	}
	

	public void deleteGPSTraceById(String id) throws SQLException
	{
		try {
			
			Statement s = connection.createStatement();
			
			// first, we need to fetch the gpsTrace (by id) to get the associated fileId
			String sql = "SELECT fileId, name FROM \"gpsTraces\" WHERE id = '"+id+"'";
			String fileId = null;
			logger.debug("fetch fileId sql=" + sql);
			ResultSet r = s.executeQuery(sql);
			// should only get 1 in the result set
			while( r.next() ) { 
				fileId = r.getString(1);
				logger.debug("deleting gps trace name=" + r.getString(2));
			}
			
			//TODO: Handle error if fileId is still null here --- either
			// something went wrong with the upload or somehow the fileId was deleted
			// from this record
			
			// next, delete the rows in gpsPoints that match the id
			sql = "DELETE FROM \"gpsPoints\" WHERE gpstraceid = '"+id+"'";
			logger.debug("delete gps points for trace id=" + sql);
			s.execute(sql); 
			
			// now, delete the trace
			sql = "DELETE FROM \"gpsTraces\" WHERE id = '"+id+"'";
			logger.debug("delete gpstrace sql=" + sql);
			s.execute(sql); 
			
			// And delete the associated item from gpsFiles.
			// Note: gpsTrace needs to be deleted first because of the foreign key
			// but we need a nice way to recover if the delete of gpsFile
			// does not delete
			sql = "DELETE FROM \"gpsFiles\" WHERE id = '"+fileId+"'";
			logger.debug("delete gpsfiles sql=" + sql);
			s.execute(sql); 
			
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}		
	}


}
