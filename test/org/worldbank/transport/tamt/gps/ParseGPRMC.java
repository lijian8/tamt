package org.worldbank.transport.tamt.gps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.worldbank.transport.tamt.shared.GPRMCRecord;
import org.worldbank.transport.tamt.shared.TAMTPoint;

public class ParseGPRMC {

	static String FILENAME = "/home/tamt/eclipse/workspace/tamt/test/org/worldbank/transport/tamt/gps/10042210.DAT";
	static double minBearing = 180;
	static double maxBearing = 180;
	static Pattern pattern = Pattern.compile(",\\s*");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		 
		System.out.println("testing...");
		String line = null;
		String line2 = null;
		int numRecords = 0;
		
		try {
			FileInputStream fin = new FileInputStream(FILENAME);
			InputStreamReader in = new InputStreamReader(fin);
			BufferedReader buff = new BufferedReader(in);
			//Scanner scanner = new Scanner();
			
			long start = System.currentTimeMillis();
			
			
			while((line = buff.readLine()) != null)
			{
				if( line.indexOf("$GPRMC") != -1)
				{
					// this assumes that GPGGA is the only other captured sentence
					// and will not work if another sentence is recorded of if the
					// sentence sequence is NOT in the correct order (RMC, GGA)
					line2 = buff.readLine();
					processLineSet(line, line2);
					numRecords++;
				}
			}
			
			long end = System.currentTimeMillis();
			long delta = end - start;
			System.out.println("milliseconds=" + delta);
			
			System.out.println("number of records=" + numRecords);
			//System.out.println("min bearing=" + minBearing);
			//System.out.println("max bearing=" + maxBearing);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		//System.out.println(p);
		/*
		if( p.getBearing() < minBearing )
		{
			minBearing = p.getBearing();
		}
		if( p.getBearing() > maxBearing )
		{
			maxBearing = p.getBearing();
		}
		*/
		
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
