package org.worldbank.transport.tamt.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.worldbank.transport.tamt.shared.TAMTPoint;


public class ZipToTAMTPoint {

	private static String zipFilePath = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/sample.multiple.zip";
	private static ZipFile zipFile;
	private static Enumeration entries;
	static Pattern pattern = Pattern.compile(",\\s*");
	
	public static void main(String[] args) {

		String line2 = null;
		int numRecords = 0;
		
		try {
			
			long start = System.currentTimeMillis();
			
			// open zip
			zipFile = new ZipFile(zipFilePath);
			
			entries = zipFile.entries();

		      while(entries.hasMoreElements()) {
		    	
		        ZipEntry entry = (ZipEntry)entries.nextElement();
		        //System.out.println(entry.getName());
		        //System.out.println(entry.getSize());
		        
		        InputStream is = zipFile.getInputStream(entry);
		        
		        // read the inputstream
		        if( is != null )
		        {
		        	StringBuilder sb = new StringBuilder();
		        	String line;
		        	
		        	BufferedReader buff = new BufferedReader(new InputStreamReader(is));
		        	int count = 0;
		        	while(( line = buff.readLine()) != null)
		        	{
		        		// operate on line
		        		// sb.append(line).append("\n");
		        		//System.out.println(line);
		        		//count++;
		        		if( line.indexOf("$GPRMC") != -1)
						{
							// this assumes that GPGGA is the only other captured sentence
							// and will not work if another sentence is recorded of if the
							// sentence sequence is NOT in the correct order (RMC, GGA)
							line2 = buff.readLine();
							try {
								processLineSet(line, line2);
							} catch (NumberFormatException e)
							{
								System.out.println("error processing line set:" + e.getMessage());
								continue;
							}
							numRecords++;
						}
		        	}
		        	
		        }
		      }
		      System.out.println("numRecords="+numRecords);

		      zipFile.close();
		      long end = System.currentTimeMillis();
			  long delta = end - start;
			  System.out.println("milliseconds="+delta);
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("SFMSFM");
		}
		
	}
	
	public static void processLineSet(String gprmc, String gpgga)
	{
		//System.out.println("gprmc=" + gprmc);
		//System.out.println("gpgga" + gpgga);
		
		// set up new point
		TAMTPoint p = new TAMTPoint();
		
		parseGPRMC(gprmc, p);
		parseGPGGA(gpgga, p);
		
		
	}	
	
	public static void parseGPGGA(String line, TAMTPoint p)
	{
		//String[] data = line.split(",\\s*");
		String[] data = pattern.split(line);
		
		//  $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
		
		// altitude
		p.setAltitude(Double.parseDouble(data[9]));
		
		// altitude units
		p.setAltitudeUnits(data[10]);
		
	}
	
	public static void parseGPRMC(String line, TAMTPoint p)
	{
		//String[] data = line.split(",\\s*");
		String[] data = pattern.split(line);
		
		// timestamp from hhmmss, ddmmyy
		Date timestamp = parseDate(data[1], data[9]);
		//p.setTimestamp(timestamp);
		
		// latitude (ddmm.ss), latitude hemisphere (N or S)
		double latitude = parseCoord(data[3], data[4]);
		p.setLatitude(latitude);
		
		// longitude (ddmm.ss), longitude hemisphere (E or W)
		double longitude = parseCoord(data[5], data[6]);
		p.setLongitude(longitude);
		
		// bearing 
		//System.out.println("bearing=" + data[8]);
		double bearing = Double.parseDouble(data[8]);
		p.setBearing(bearing);
		
		// speed (in knots)
		double speed = Double.parseDouble(data[7]);
		p.setSpeed(speed);
		
	}	

	public static double parseCoord(String ddmmss, String hemisphere)
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
	
	public static Date parseDate(String hhmmss, String ddmmyy)
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

}
