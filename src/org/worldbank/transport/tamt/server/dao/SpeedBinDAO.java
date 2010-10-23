package org.worldbank.transport.tamt.server.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.SpeedDistributionRecord;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class SpeedBinDAO extends DAO {

	private static final double CLOSEST_DISTRIBUTION_PERCENT_THRESHOLD = 0.15;
	static Logger logger = Logger.getLogger(SpeedBinDAO.class);
	private RegionDAO regionDao;
	private TagDAO tagDao;
	
	private HashMap<String, Double> trafficCountCache = new HashMap<String, Double>();
	private HashMap<String, ArrayList<Double>> percentValuesCache = new HashMap<String, ArrayList<Double>>();
	
	private static SpeedBinDAO singleton = null;
	public static SpeedBinDAO get() {
		if (singleton == null) {
			singleton = new SpeedBinDAO();
		}
		return singleton;
	}

	public SpeedBinDAO() {
		regionDao = RegionDAO.get();
		tagDao = TagDAO.get();
	}

	public void populateSpeedDistribution() throws Exception {

		// get default study region
		ArrayList<StudyRegion> regions = regionDao.getStudyRegions();
		StudyRegion currentStudyRegion = null;
		for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
			StudyRegion studyRegion = (StudyRegion) iterator.next();
			if( studyRegion.isCurrentRegion())
			{
				currentStudyRegion = studyRegion;
				break;
			}
		}
	
		// get tags for this study region
		ArrayList<TagDetails> tagDetailsList = tagDao.getTagDetails(currentStudyRegion);
	
		ArrayList<String> dayTypes =  new ArrayList<String>();
		dayTypes.add(TrafficCountRecord.DAYTYPE_WEEKDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SATURDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		
		int hourBin = 0;
		int speedBin = 0;
		
		int lastSpeedBin = getLastSpeedBin();
		logger.debug("lastSpeedBin=" + lastSpeedBin);
		
		for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) iterator.next();
			
			// we have an ID
			String tagId = tagDetails.getId();
			
			// loop on day types
			for (Iterator dayTypeIterator = dayTypes.iterator(); dayTypeIterator.hasNext();) {
				
				String dayType = (String) dayTypeIterator.next();
				
				// loop on hour (0..23)
				for (int i = 0; i < 24; i++) {
					hourBin = i;
				
					// insert the records
					for (int j = 0; j <= lastSpeedBin; j++) {
						speedBin = j;
						insertSpeedDistributionRecord(tagId, dayType, hourBin, speedBin);
					}
					
					// and now update the percentage values
					updateSpeedDistributionPercentageValues(tagId, dayType, hourBin);
					
				}
			}
			
		}
		
	}

	public void insertSpeedDistributionRecord(String tagId, String dayType,
			int hourBin, int speedBin) throws Exception {
		
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// pass off to stored procedure
			String sql = "SELECT * FROM " +
						" TAMT_insertSpeedDistributionRecord(" +
						"'"+tagId+"', " +
						"'"+dayType+"', " +
						hourBin + ", " +
						speedBin +
						")";
			logger.debug("SQL for insertSpeedDistributionRecord: " + sql);
			
			ResultSet r = s.executeQuery(sql); // ignored
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
	}

	public void updateSpeedDistributionPercentageValues(String tagId,
			String dayType, int hourBin) throws Exception {
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// SELECT * FROM TAMT_updateSpeedDistributionPercentageValues('3590f46c-6140-4555-bc73-75c7e381a843', 'WEEKDAY', 8);
			
			// pass off to stored procedure
			String sql = "SELECT * FROM " +
						" TAMT_updateSpeedDistributionPercentageValues(" +
						"'"+tagId+"', " +
						"'"+dayType+"', " +
						+ hourBin + 
						")";
			logger.debug("SQL for updateSpeedDistributionPercentageValues: " + sql);
			
			ResultSet r = s.executeQuery(sql); // ignored
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}

	public int getLastSpeedBin() throws Exception {
		int lastSpeedBin = 0;
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// get the WEEKDAY counts for this tag
			String sql = "SELECT speedbinnumber FROM gpspoints " +
						// the last speed bin number is global for a study region
						// so filtering by tag,daytype,hourbin is not necessary
						// TODO: add a WHERE clause for regionId when 
						// multiple regions feature is complete.
						" ORDER BY speedbinnumber DESC LIMIT 1";
						
			logger.debug("SQL for getLastSpeedBin: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			while (r.next()) {
				lastSpeedBin  = r.getInt(1);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		return lastSpeedBin;
	}

	public double getTotalFlow(String tagId, String dayType, int hourBin) throws Exception {
		double totalFlow = 0.0;
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// get the WEEKDAY counts for this tag
			String sql = "SELECT totalflow FROM trafficflowreport " +
						"WHERE tagid = '"+tagId+"'" +
						" AND daytype = '"+dayType+"'" +
						" AND date_part('hour', hour_bin) = " + hourBin;
			logger.debug("SQL for totalflow: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			while (r.next()) {
				totalFlow  = r.getDouble(1);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		return totalFlow;
	}
	
	public boolean hasObservedData(String tagId, String dayType, int hourBin) throws Exception
	{
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// get the total time in bin 
			String sql = "SELECT SUM(secondsinbin) as sumseconds FROM speeddistribution " +
						"WHERE tagid = '"+tagId+"'" +
						" AND daytype = '"+dayType+"'" +
						" AND hourBin = " + hourBin;
			logger.debug("SQL for sum secondsinbin: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			r.next();
			Double sumSeconds = r.getDouble("sumseconds");
			logger.debug("sumSeconds=" + sumSeconds);
			boolean hasObservedData = false;
			if( sumSeconds != null && sumSeconds > 0)
			{
				hasObservedData = true;
			}
			return hasObservedData;
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}	
	
	public void triggerSpeedDistributionInterpolation() throws Exception
	{
		
		// TODO: truncate speeddistobserved prior to populating it
		// first, populate the speeddistobserved table
		populateSpeedDistObserved();
		
		// now fill in the gaps
		interpolateSpeedDistribution();
		
	}

	public void interpolateSpeedDistribution() throws Exception {
	
		// get unobserved speed distribution records
		ArrayList<SpeedDistributionRecord> unobservedRecords = getUnobservedSpeedDistributionRecords();
		
		for (Iterator iterator = unobservedRecords.iterator(); iterator.hasNext();) {
			SpeedDistributionRecord unobserved = (SpeedDistributionRecord) iterator
					.next();
			
			SpeedDistributionRecord closestRecord = updateFromClosestDistribution(unobserved);
			logger.debug("closestRecord=" + closestRecord);
			//TODO: can remove return parameter from updateFromClosestDistribution
			
		}
		
	}
	
	public SpeedDistributionRecord updateFromClosestDistribution(SpeedDistributionRecord query) throws Exception
	{
		SpeedDistributionRecord match = null;
		
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String tagId = query.getTagId();
			String dayType = query.getDayType();
			String hourBin = query.getHourBin(); // make sure to send into SP as int
			double totalFlow = query.getTotalFlow();
			String sql = "SELECT * FROM " +
					"TAMT_updateFromClosestDistribution('"+tagId+"','"+dayType+"', "+hourBin+", " +
					totalFlow+", "+CLOSEST_DISTRIBUTION_PERCENT_THRESHOLD+") " +
					"AS foo(tagid text, daytype text, hourbin int, " +
					"isobserved boolean, totaflow double precision, " +
					"diff double precision)";
			logger.debug("SQL for TAMT_updateFromClosestDistribution: " + sql);
			
			/*
			 * The TAMT_getClosestDistribution function always
			 * returns the closest row by daytype+threshold
			 * or closest row without daytype+threshold. So this
			 * should always be exactly one row (no more, no less).
			 */
			ResultSet r = s.executeQuery(sql);
			while(r.next())
			{
				match = new SpeedDistributionRecord();
				match.setTagId(r.getString(1));
				match.setDayType(r.getString(2));
				match.setHourBin(r.getString(3));
				match.setObserved(r.getBoolean(4));
				match.setTotalFlow(r.getDouble(5));
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		return match;
	}

	private ArrayList<SpeedDistributionRecord> getUnobservedSpeedDistributionRecords() throws Exception {
		
		// Don't worry about a tag/region filter on this query because this table is truncated
		// prior to populating it, so it should only ever have data relating to one study region
		ArrayList<SpeedDistributionRecord> unobserved = new ArrayList<SpeedDistributionRecord>();
		
		try {
			
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// get the total time in bin 
			String sql = "SELECT tagId, dayType, hourBin, isObserved, totalFlow FROM speeddistobserved";
			logger.debug("SQL for sum secondsinbin: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			while(r.next())
			{

				boolean isObserved = r.getBoolean("isObserved");
				
				if( !isObserved )
				{
					SpeedDistributionRecord speedDistributionRecord = new SpeedDistributionRecord();
					speedDistributionRecord.setTagId(r.getString("tagId"));
					speedDistributionRecord.setDayType(r.getString("dayType"));
					speedDistributionRecord.setHourBin(r.getString("hourBin"));
					speedDistributionRecord.setObserved(isObserved);
					
					// add it to the list of unobserved
					unobserved.add(speedDistributionRecord);
				}
				
			}
			
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}		
		
		
		return unobserved;
		
	}

	public void populateSpeedDistObserved() throws Exception {
		
		// get default study region
		ArrayList<StudyRegion> regions = regionDao.getStudyRegions();
		StudyRegion currentStudyRegion = null;
		for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
			StudyRegion studyRegion = (StudyRegion) iterator.next();
			if( studyRegion.isCurrentRegion())
			{
				currentStudyRegion = studyRegion;
				break;
			}
		}
	
		// get tags for this study region
		ArrayList<TagDetails> tagDetailsList = tagDao.getTagDetails(currentStudyRegion);
	
		ArrayList<String> dayTypes =  new ArrayList<String>();
		dayTypes.add(TrafficCountRecord.DAYTYPE_WEEKDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SATURDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		
		int hourBin = 0;
		int speedBin = 0;
		
		for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) iterator.next();
			
			// we have an ID
			String tagId = tagDetails.getId();
			
			// loop on day types
			for (Iterator dayTypeIterator = dayTypes.iterator(); dayTypeIterator.hasNext();) {
				
				String dayType = (String) dayTypeIterator.next();
				
				// loop on hour (0..23)
				for (int i = 0; i < 24; i++) {
					hourBin = i;
				
					boolean hasObserved = hasObservedData(tagId, dayType, hourBin);
					Double totalFlow = null;
					double tFlow = getTotalFlow(tagId, dayType, hourBin);
					totalFlow = new Double(tFlow);
					
					// create the record
					createObservedGPSDataRecord(tagId, dayType, hourBin, hasObserved, totalFlow);
					
				}
			}
			
		}
	}

	protected void createObservedGPSDataRecord(String tagId, String dayType,
			int hourBin, boolean hasObserved, Double totalFlow) throws Exception {
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// get the total time in bin 
			String sql = "INSERT INTO speeddistobserved(tagid, daytype, hourbin, isobserved, totalflow) " +
					"VALUES (" +
						"'"+tagId+"'," +
						"'"+dayType+"'," +
						hourBin + "," +
						hasObserved + "," +
						totalFlow + ")";
			logger.debug("SQL for insert speeddistobserved: " + sql);
			s.executeUpdate(sql);
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
	}
	
	public void combineSpeedDistributionTrafficFlow() throws Exception
	{
		// get default study region
		ArrayList<StudyRegion> regions = regionDao.getStudyRegions();
		StudyRegion currentStudyRegion = null;
		for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
			StudyRegion studyRegion = (StudyRegion) iterator.next();
			if( studyRegion.isCurrentRegion())
			{
				currentStudyRegion = studyRegion;
				break;
			}
		}
	
		// get tags for this study region
		ArrayList<TagDetails> tagDetailsList = tagDao.getTagDetails(currentStudyRegion);
	
		ArrayList<String> dayTypes =  new ArrayList<String>();
		dayTypes.add(TrafficCountRecord.DAYTYPE_WEEKDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SATURDAY);
		dayTypes.add(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		
		// these vehicle types need to match the column names for
		// vehicle types in the traffic flow report
		ArrayList<String> vehicleTypes =  new ArrayList<String>();
		vehicleTypes.add("w2");
		vehicleTypes.add("w3");
		vehicleTypes.add("pc");
		vehicleTypes.add("tx");
		vehicleTypes.add("ldv");
		vehicleTypes.add("ldc");
		vehicleTypes.add("hdc");
		vehicleTypes.add("mdb");
		vehicleTypes.add("hdb");
		
		int hourBin = 0;
		int speedBin = 0;
		
		int lastSpeedBin = getLastSpeedBin();
		logger.debug("lastSpeedBin=" + lastSpeedBin);
		
		// loop on tags
		for (Iterator tagIterator = tagDetailsList.iterator(); tagIterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) tagIterator.next();
			
			String tagId = tagDetails.getId();
			
			// loop on day types
			for (Iterator dayIterator = dayTypes.iterator(); dayIterator
					.hasNext();) {
				String dayType = (String) dayIterator.next();
				
				// loop on vehicle types
				// TODO: move vehicle type to outer loop (past tag) for consistency
				for (Iterator iterator = vehicleTypes.iterator(); iterator
						.hasNext();) {
					String vehicleType = (String) iterator.next();
					
					// loop on speed bins
			        for (int i = 0; i <= lastSpeedBin; i++) {
						
						speedBin = i;
					
						// initialize cumulative variables
						double vehicleSecondsPerDay = 0.0;
				        double vehicleMetersPerDay = 0.0;
				        
						// loop on hour
						for (int j = 0; j < 24; j++) {
					
							hourBin = j;
							
					          // traffic count from traffic flow report (e.g. w2 traffic count per hour = 3)
							  // *** THIS IS SLOWING THINGS DOWN CONSIDERABLY -- DOES NOT NEED speedBin, BUT KEEPS GETTING CALLED WITHIN THE SPEED BIN LOOP
					          double trafficCount = getTrafficCount( tagId, dayType, hourBin, vehicleType);
							  
							
					          // fractional time in bin and average speed in bin from speed distribution table
					          // (e.g. [0.9355095541401274, 0.1436349283740799]
					          
					          // *** THIS IS SLOWING THINGS DOWN CONSIDERABLY -- DOES NOT NEED vehicleType, BUT KEEPS GETTING CALLED WITHIN THE FULL BIN LOOP
					          ArrayList<Double> percentValues = getPercentValuesInBin( tagId, dayType, hourBin, speedBin);
					          double percentSecondsInBin = percentValues.get(0); // percentSecondsInBin for an hourbin = 1
					          double percentMetersInBin = percentValues.get(1);
					     
					          // calculate vehicle seconds per hour and add to vehicle seconds per day
					          // Note: even though vehicleSecondsPerDay is derived from fractionalTimeInBin,
					          // we are not naming this variable fractionalVehicleSecondsPerDay because that
					          // variable name is reserved for the sum and fraction calculation performed later
					          double vehicleSecondsPerHour = trafficCount * percentSecondsInBin; // ** this is not ACTUAL seconds in bin!
					          vehicleSecondsPerDay = vehicleSecondsPerDay + vehicleSecondsPerHour;

					          // calculate vehicleMetersPerDay
					          double vehicleMetersPerHour = trafficCount * percentMetersInBin;
					          vehicleMetersPerDay = vehicleMetersPerDay + vehicleMetersPerHour;							
							
						} // end hour loop
						
						logger.debug("vehicleSecondsPerDay=" + vehicleSecondsPerDay);
						logger.debug("vehicleMetersPerDay=" + vehicleMetersPerDay);
						
					    // calculate speed
						double weightedAverageSpeed = 0.0;
						// if both variables are not zero, calculate average speed, otherwise avg speed is 0.0 too
						if( vehicleSecondsPerDay != 0.0 && vehicleMetersPerDay != 0.0)
						{
							weightedAverageSpeed = vehicleMetersPerDay / vehicleSecondsPerDay;
						}
					    
					    // insert cumulative variables in new table
					    insertSpeedDistTrafficFlowRecord( tagId, dayType, vehicleType, speedBin, 
					                          vehicleSecondsPerDay, vehicleMetersPerDay, weightedAverageSpeed);
						
					} // end speed bin loop
					
			        
			        // update percentvehiclesecondsperday, percentvehiclesmetersperday for every row
			        ArrayList<Double> sumValues = getSumVehicleValuesPerDay( tagId, dayType, vehicleType );
			        double sumVehicleSecondsPerDay = sumValues.get(0);
			        double sumVehicleMetersPerDay = sumValues.get(1);
			        setSumValuesForVehicleType( tagId, dayType, vehicleType, sumVehicleSecondsPerDay, sumVehicleMetersPerDay);
			        
			        
				} // end vehicle type loop
				
			} // end day type loop
			
		} // end tag loop
		
	}

	protected void setSumValuesForVehicleType( String tagId, String dayType, String vehicleType, double sumVehicleSecondsPerDay, 
			double sumVehicleMetersPerDay) throws Exception {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE speeddistributiontrafficflow " +
					"SET " +
					"percentvehiclesecondsperday = (vehiclesecondsperday / "+sumVehicleSecondsPerDay+"), " +
					"percentvehiclemetersperday = (vehiclemetersperday / "+sumVehicleMetersPerDay+") " +
					"WHERE tagid = '"+tagId+"' " +
					"AND daytype = '"+dayType+"' " +
					"AND vehicletype = '" + vehicleType + "'";
			logger.debug("SQL for : " + sql);
			s.executeUpdate(sql);
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}

	}

	private ArrayList<Double> getSumVehicleValuesPerDay(String tagId, String dayType, String vehicleType) throws Exception {

		ArrayList<Double> sumValues = new ArrayList<Double>();
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT SUM(vehicleSecondsPerDay), SUM(vehicleMetersPerDay) FROM speeddistributiontrafficflow " +
							"WHERE tagid = '"+tagId+"' " +
							"AND daytype = '"+dayType+"' " +
							"AND vehicletype = '" + vehicleType + "'";
			logger.debug("SQL for : " + sql);
			ResultSet r = s.executeQuery(sql);
			while(r.next())
			{
				double sumVehicleSecondsPerDay = Double.valueOf( r.getDouble(1) );
				double sumVehicleMetersPerDay = Double.valueOf( r.getDouble(2) );
				sumValues.add(sumVehicleSecondsPerDay);
				sumValues.add(sumVehicleMetersPerDay);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		return sumValues;
	}

	private double getSumVehicleMetersPerDay(String tagId, String dayType, String vehicleType) throws Exception {

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "";
			logger.debug("SQL for : " + sql);
			s.executeUpdate(sql);
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		return 0;
	}
	
	protected void insertSpeedDistTrafficFlowRecord(String tagId, String dayType,
			String vehicleType, int speedBin, double vehicleSecondsPerDay,
			double vehicleMetersPerDay, double weightedAverageSpeed) throws Exception {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO speeddistributiontrafficflow (tagid, " +
					"daytype, vehicletype, speedbin, vehiclesecondsperday, " +
					"vehiclemetersperday, weightedaveragespeed) " +
					"VALUES (" +
					"'"+tagId+"', " +
					"'"+dayType+"', " +
					"'"+vehicleType+"', " +
					""+speedBin+", " +
					""+vehicleSecondsPerDay+", " +
					""+vehicleMetersPerDay+", " +
					""+weightedAverageSpeed+")"; // not inserting percent* values yet
			logger.debug("SQL for : " + sql);
			s.executeUpdate(sql);
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
	}

	protected ArrayList<Double> getPercentValuesInBin(String tagId, String dayType,
			int hourBin, int speedBin) throws Exception {
		Double percentSecondsInBin = null;
		Double percentMetersInBin = null;
		ArrayList<Double> percentValues = new ArrayList<Double>();
		
		/*
		 * New!!
		 * 
		 * Use a hashmap caching strategy to increase performance here.
		 * 
		 * The key for the hash is "tagId-dayType-hourBin-speedBin"
		 * The value for the hash is the percentValues array
		 * 
		 * Steps:
		 * 1) Look in the percentValuesCache for the key
		 * 2) If we have a key, extract the percentValues value and return it
		 * 3) If we don't have the key, query the database, store the value, and return it
		 * 
		 */
		//TODO: we could build this key further out of the loop and not waste memory
		String key = tagId + "-" + dayType + "-" + hourBin + "-" + speedBin;
		percentValues = percentValuesCache.get(key);
		logger.debug("percentValues from cache=" + percentValues);
		if( percentValues != null)
		{
			logger.debug("Using percentValues from cache");
			return percentValues;
		}	
		
		logger.debug("percentValues not in cache for this key; going to database");
		percentValues = new ArrayList<Double>(); // since we may have nulled it, reset it to a new array list
		// TODO: we will reach into the speed distribution table to extract fractionalTimeInBin based on tag, dayType, vehicleType, hourBin, and speedBin.
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT percentSecondsInBin, percentMetersInBin FROM speeddistribution " +
					"WHERE tagid = '"+tagId+"' " +
					"AND daytype = '"+dayType+"' " +
					"AND hourbin = " + hourBin + " " +
					"AND speedbin = " + speedBin;
			logger.debug("SQL for : " + sql);
			
			//should only get 1 result
			ResultSet r = s.executeQuery(sql);
			while(r.next())
			{
				percentSecondsInBin = Double.valueOf( r.getDouble(1) );
				percentValues.add(percentSecondsInBin);
				
				percentMetersInBin = Double.valueOf( r.getDouble(2) );
				percentValues.add(percentMetersInBin);
				
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
		// insert the map
		percentValuesCache.put(key, percentValues);
		return percentValues;
	}

	protected double getTrafficCount(String tagId, String dayType, int hourBin,
			String vehicleType) throws Exception {
		
		Double trafficCount = null;
		
		/*
		 * New!!
		 * 
		 * Use a hashmap caching strategy to increase performance here.
		 * 
		 * The key for the hash is "tagId-dayType-hourBin-vehicleType"
		 * The value for the hash is trafficCount
		 * 
		 * Steps:
		 * 1) Look in the trafficCountCache for the key
		 * 2) If we have a key, extract the trafficCount value and return it
		 * 3) If we don't have the key, query the database, store the value, and return it
		 * 
		 */
		//TODO: we could build this key further out of the loop and not waste memory
		String key = tagId + "-" + dayType + "-" + hourBin + "-" + vehicleType;
		trafficCount = trafficCountCache.get(key);
		if( trafficCount != null)
		{
			return trafficCount;
		}
		
		// if we don't have a trafficCount from the cache, then query the database for it
		
		// TODO: we will reach into the traffic flow report table to extract this information based on tag, daytype, vehicle type and hour bin
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT "+vehicleType+" FROM trafficflowreport " +
					"WHERE tagid = '"+tagId+"' " +
					"AND daytype = '"+dayType+"' " +
					"AND date_part('hour', hour_bin) = " + hourBin;
			logger.debug("SQL for : " + sql);
			
			//should only get 1 result
			ResultSet r = s.executeQuery(sql);
			while(r.next())
			{
				trafficCount = Double.valueOf( r.getDouble(1) );
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}
		
		// and insert it into the cache
		trafficCountCache.put(key, trafficCount);
		
		return trafficCount;
	}
	
	/**
	 * Comes after combineSpeedDistributionTrafficFlow
	 * @throws Exception 
	 */
	public void removeDayTypeFromSpeedDistributionTrafficFlow() throws Exception
	{
		// get default study region
		ArrayList<StudyRegion> regions = regionDao.getStudyRegions();
		StudyRegion currentStudyRegion = null;
		for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
			StudyRegion studyRegion = (StudyRegion) iterator.next();
			if( studyRegion.isCurrentRegion())
			{
				currentStudyRegion = studyRegion;
				break;
			}
		}
	
		DayTypePerYearOption dayTypePerYearOption = getDayTypePerYearOption(currentStudyRegion);
		
		// mutlily
		
		
		
  // OPTION 1
  // foreach row in `speeddistributiontrafficflow` (Table C in my notes)
  //       vehicleSecondsPerBinPerYear = C.fractionalVehicleSecondsPerDay * equivalentDaysPerYear
  //       vehicleMetersPerBinPerYear = C.fractionalVehicleMetersPerDay * equivalentDaysPerYear
  //       insert a row in the new Table D: 
  //                    C.tagId, C.vehicleType, C.speedBin
  //                    vehicleSecondsPerBinPerYear, vehicleMetersPerBinPerYear

  // OPTION 2
  // foreach row in `speeddistributiontrafficflow` (Table C in my notes)

  //       vSBY_WEEKDAYS = C.vehicleSecondsPerDay[WEEKDAY] * numWeekdays
  //       vSBY_SATURDAYS = C.vehicleSecondsPerDay[SATURDAY] * numSaturdays
  //       vSBY_SUNDAYHOLIDAYS = C.vehicleSecondsPerDay[SUNDAY_HOLIDAY] * numSundayHolidays
  //       vehicleSecondsPerBinPerYear = vSBY_WEEKDAYS + vSBY_SATURDAYS + vSBY_SUNDAYHOLIDAYS

  //       vMBY_WEEKDAYS = C.vehicleMetersPerDay[WEEKDAY] * numWeekdays
  //       vMBY_SATURDAYS = C.vehicleMetersPerDay[SATURDAY] * numSaturdays
  //       vMBY_SUNDAYHOLIDAYS = C.vehicleMetersPerDay[SUNDAY_HOLIDAY] * numSundayHolidays
  //       vehicleMetersPerBinPerYear = vMBY_WEEKDAYS + vMBY_SATURDAYS + vMBY_SUNDAYHOLIDAYS

  //       insert a row in the new Table D: 
  //                    C.tagId, C.vehicleType, C.speedBin
  //                    vehicleSecondsPerBinPerYear, vehicleMetersPerBinPerYear
					
		
	}

	private void calculateVehicleVariablesPerBinPerYear(
			ArrayList<Integer> multipliers) {
		
		/*
		 * If multiplier only has 1 item, it is equivalent days per year
		 * (aka option1weekday). If it is has multiple items, we need
		 * to handle the multiplication for option2
		 */
		int numMultipliers = multipliers.size();
		
		String sql = "";
		
		if( numMultipliers == 1)
		{
			sql = "INSERT";
		} else {
			sql = "";
		}
		
		// execute sql
		
		
		
		
	}

	private DayTypePerYearOption getDayTypePerYearOption(
			StudyRegion currentStudyRegion) throws Exception {
		return regionDao.getDayTypePerYearOption(currentStudyRegion.getId());
	}
}
