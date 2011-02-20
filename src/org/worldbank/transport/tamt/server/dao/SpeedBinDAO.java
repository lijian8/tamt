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
			//logger.debug("SQL for insertSpeedDistributionRecord: " + sql);
			
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
			//logger.debug("SQL for updateSpeedDistributionPercentageValues: " + sql);
			
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
			//logger.debug("SQL for totalflow: " + sql);
			
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
			//logger.debug("SQL for sum secondsinbin: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			r.next();
			Double sumSeconds = r.getDouble("sumseconds");
			//logger.debug("sumSeconds=" + sumSeconds);
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
	
		logger.debug("interpolateSpeedDistribution");
		// get unobserved speed distribution records
		ArrayList<SpeedDistributionRecord> unobservedRecords = getUnobservedSpeedDistributionRecords();
		
		for (Iterator iterator = unobservedRecords.iterator(); iterator.hasNext();) {
			SpeedDistributionRecord unobserved = (SpeedDistributionRecord) iterator
					.next();
			
			SpeedDistributionRecord closestRecord = updateFromClosestDistribution(unobserved);
			//logger.debug("closestRecord=" + closestRecord);
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
			//logger.debug("SQL for TAMT_updateFromClosestDistribution: " + sql);
			
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
		
		logger.debug("populateSpeedDistObserved");
		
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
			//logger.debug("SQL for insert speeddistobserved: " + sql);
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
	
	public void combineSpeedDistributionTrafficFlowIMPROVED() throws Exception
	{
		/*
		 * This method is trying to get around the performance issues of the
		 * current implementation, which has some methods that are being called
		 * too much (due to the nature of the nested loops over tags, daytypes,
		 * vehicle types, speed bins, and hour bins. The trick is we need to
		 * use some outer variables in some inner calculations.
		 * 
		 */
		
		// get region
		// get tags
		// get daytypes
		// get lastSpeedBin
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
		logger.debug("getLastSpeedBin=" + lastSpeedBin);
		
		/*
		 * For trafficCount....
		 * Loop over:
		 * - tags
		 * 		- daytypes
		 * 			- vehicle types
		 * 				- (skip speedbin)
		 * 					- hourbin
		 * 
		 * And keep the trafficCount value ready for later...(hash?)
		 * How many items in hash?: tags * daytypes * vehicletypes * hourbins = number of tags * 648
		 * I think in this case we do it in postgres (maybe temp table?), instead of java
		 */
		
		/*
		 * this is a throwaway, but is here in order not to change the return 
		 * value of getTrafficCount, but not allocate extra memory in the loop.
		 */
		double trafficCount = 0.0; 

		for (Iterator tagIterator = tagDetailsList.iterator(); tagIterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) tagIterator.next();
			String tagId = tagDetails.getId();
			
			for (Iterator dayIterator = dayTypes.iterator(); dayIterator.hasNext();) {
				String dayType = (String) dayIterator.next();	
				
				for (Iterator iterator = vehicleTypes.iterator(); iterator.hasNext();) {
					String vehicleType = (String) iterator.next();
					
					for (int j = 0; j < 24; j++) {
						hourBin = j;
						
				        // traffic count from traffic flow report (e.g. w2 traffic count per hour = 3)
						
						// the getTrafficCount stores and updates trafficCountCache to improve performance.
						// String key = tagId + "-" + dayType + "-" + hourBin + "-" + vehicleType;
						trafficCount = getTrafficCount( tagId, dayType, hourBin, vehicleType);
						
					}
				}
			
			}
		}
							
		/*
		 * For percentValuesInBin...
		 * Loop over:
		 * - tags
		 * 		- daytypes
		 * 			- (skip vehicle type) ***** though this may change if GPS data is categorized by vehicle type ****
		 * 				- speedBin
		 * 					- hourBin
		 * and keep the percentValuesInBin ready for later... (hash?)
		 */
		
		/*
		 * this is a throwaway, but is here in order not to change the return 
		 * value of percentValues, but not allocate extra memory in the loop. 
		 */
		ArrayList<Double> percentValues = new ArrayList<Double>();
		for (Iterator tagIterator = tagDetailsList.iterator(); tagIterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) tagIterator.next();
			String tagId = tagDetails.getId();
			
			for (Iterator dayIterator = dayTypes.iterator(); dayIterator.hasNext();) {
				String dayType = (String) dayIterator.next();	
				
				for (int i = 0; i <= lastSpeedBin; i++) {
					
					speedBin = i;;
					
					for (int j = 0; j < 24; j++) {
						hourBin = j;
						
						percentValues = getPercentValuesInBin( tagId, dayType, hourBin, speedBin);
						
					}
				}
			
			}
		}		
	
		/*
		 * Now loop through all of the factors and yank out the cached values as appropriate...
		 */

		/*
		 * 
		 * 			LOOP ON TAGS
		 * 
		 */
		for (Iterator tagIterator = tagDetailsList.iterator(); tagIterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) tagIterator.next();
			
			String tagId = tagDetails.getId();
			
			/*
			 * 
			 * 			LOOP ON DAY TYPES
			 * 
			 */
			for (Iterator dayIterator = dayTypes.iterator(); dayIterator
					.hasNext();) {
				String dayType = (String) dayIterator.next();
				
				/*
				 * 
				 * 			LOOP ON VEHICLE TYPES
				 * 
				 */
				// TODO: move vehicle type to outer loop (past tag) for consistency
				for (Iterator iterator = vehicleTypes.iterator(); iterator
						.hasNext();) {
					String vehicleType = (String) iterator.next();
					
					/*
					 * 
					 * 			LOOP ON SPEED BINS
					 * 
					 */
					for (int i = 0; i <= lastSpeedBin; i++) {
						
						speedBin = i;
					
						// initialize cumulative variables
						double vehicleSecondsPerDay = 0.0;
				        double vehicleMetersPerDay = 0.0;
				        
				        /*
						 * 
						 * 			LOOP ON HOUR BINS
						 * 
						 */
						for (int j = 0; j < 24; j++) {
					
							hourBin = j;
							
					          // traffic count from traffic flow report (e.g. w2 traffic count per hour = 3)
							  trafficCount = getTrafficCount( tagId, dayType, hourBin, vehicleType);
							  
							  // percentSecondsInBin and percentMetersInBin
							  percentValues = getPercentValuesInBin( tagId, dayType, hourBin, speedBin);
					          double percentSecondsInBin = percentValues.get(0);
					          double percentMetersInBin = percentValues.get(1);
					     
					          // calculate vehicle seconds per hour and add to vehicle seconds per day
					          double vehicleSecondsPerHour = trafficCount * percentSecondsInBin;
					          vehicleSecondsPerDay = vehicleSecondsPerDay + vehicleSecondsPerHour;

					          // calculate vehicleMetersPerDay
					          double vehicleMetersPerHour = trafficCount * percentMetersInBin;
					          vehicleMetersPerDay = vehicleMetersPerDay + vehicleMetersPerHour;							
							
						} // end hour loop
						
						//logger.debug("vehicleSecondsPerDay=" + vehicleSecondsPerDay);
						//logger.debug("vehicleMetersPerDay=" + vehicleMetersPerDay);
						
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
		
		/*
		 * 
		 * 			LOOP ON TAGS
		 * 
		 */
		for (Iterator tagIterator = tagDetailsList.iterator(); tagIterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) tagIterator.next();
			
			String tagId = tagDetails.getId();
			
			/*
			 * 
			 * 			LOOP ON DAY TYPES
			 * 
			 */
			for (Iterator dayIterator = dayTypes.iterator(); dayIterator
					.hasNext();) {
				String dayType = (String) dayIterator.next();
				
				/*
				 * 
				 * 			LOOP ON VEHICLE TYPES
				 * 
				 */
				// TODO: move vehicle type to outer loop (past tag) for consistency
				for (Iterator iterator = vehicleTypes.iterator(); iterator
						.hasNext();) {
					String vehicleType = (String) iterator.next();
					
					/*
					 * 
					 * 			LOOP ON SPEED BINS
					 * 
					 */
					for (int i = 0; i <= lastSpeedBin; i++) {
						
						speedBin = i;
					
						// initialize cumulative variables
						double vehicleSecondsPerDay = 0.0;
				        double vehicleMetersPerDay = 0.0;
				        
				        /*
						 * 
						 * 			LOOP ON HOUR BINS
						 * 
						 */
						for (int j = 0; j < 24; j++) {
					
							hourBin = j;
							
					          // traffic count from traffic flow report (e.g. w2 traffic count per hour = 3)
							  double trafficCount = getTrafficCount( tagId, dayType, hourBin, vehicleType);
							  
							
					          // fractional time in bin and average speed in bin from speed distribution table
					          // (e.g. [0.9355095541401274, 0.1436349283740799]
					          
					          // *** THIS IS SLOWING THINGS DOWN CONSIDERABLY -- DOES NOT NEED vehicleType, BUT KEEPS GETTING CALLED WITHIN THE FULL BIN LOOP
					          /*
					           * Error may be we want the actual time in bin, not percentSecondsInBin
					           * ie totalSecondsInBin = sum of products of seconds in bin for each hour
					           */
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
					          vehicleMetersPerDay = vehicleMetersPerDay + vehicleMetersPerHour;	// this is correct!	
							
						} // end hour loop
						
						// these values are for one vehicle type
						//logger.debug("vehicleSecondsPerDay=" + vehicleSecondsPerDay);
						//logger.debug("vehicleMetersPerDay=" + vehicleMetersPerDay);
						
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
					
			        /*
			         * Always calculate speed BEFORE normalizing using time and distance.
			         * Then, after normalizing we calculate "percent" distance from normalized seconds * average speed
			         * This rule is true for all data reductions (except the very first and OPTION2 where have to sum meters)
			         */
					
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
					"percentvehiclemetersperday = (vehiclesecondsperday / "+sumVehicleSecondsPerDay+") * weightedaveragespeed " +
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
			//logger.debug("SQL for : " + sql);
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
			//logger.debug("SQL for : " + sql);
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
		//logger.debug("percentValues from cache=" + percentValues);
		if( percentValues != null)
		{
			//logger.debug("Using percentValues from cache");
			return percentValues;
		}	
		
		//logger.debug("percentValues not in cache for this key; going to database");
		percentValues = new ArrayList<Double>(); // since we may have nulled it, reset it to a new array list
		// TODO: we will reach into the speed distribution table to extract fractionalTimeInBin based on tag, dayType, vehicleType, hourBin, and speedBin.
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			// TODO: change the fields to secondsInBin, metersInBin
			String sql = "SELECT percentSecondsInBin, percentMetersInBin FROM speeddistribution " +
					"WHERE tagid = '"+tagId+"' " +
					"AND daytype = '"+dayType+"' " +
					"AND hourbin = " + hourBin + " " +
					"AND speedbin = " + speedBin;
			//logger.debug("SQL for : " + sql);
			
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
			//logger.debug("SQL for : " + sql);
			
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
		ArrayList<String> multipliers = new ArrayList<String>();
		
		/*
		 * We will pass this off to stored procedure which expects:
		 * 		activeoption text, 
				option1weekday integer, 
				option2weekday integer,
				option2saturday integer,
				option2sundayholiday integer
			
			which means we have to fill in the blanks
		 */
		String activeoption = dayTypePerYearOption.getActiveOption();
		String option1weekday = null;
		String option2weekday = null;
		String option2saturday = null;
		String option2sundayholiday = null;
		
		// handle daytypeperyear option
		if( activeoption.equals("1") )
		{
			option1weekday = dayTypePerYearOption.getOption1weekday();
			option2weekday = null;
			option2saturday = null;
			option2sundayholiday = null;
			
		} else {
			option1weekday = null;
			option2weekday = dayTypePerYearOption.getOption2weekday();
			option2saturday = dayTypePerYearOption.getOption2saturday();
			option2sundayholiday = dayTypePerYearOption.getOption2sundayHoliday();
		}
		
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();

			String sql = "";
			sql = "SELECT * FROM " +
			" TAMT_reduceDayTypeFromSpeedDistributionTrafficFlow(" +
			"'"+activeoption+"', " +
			option1weekday + ", " +
			option2weekday + ", " +
			option2saturday + ", " +
			option2sundayholiday +
			")";
			
			logger.debug("SQL for insertSpeedDistributionRecord: " + sql);
			
			ResultSet r = s.executeQuery(sql);
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}			

	}

	/**
	 * This creates Table E in the pseudo-code:
	 * http://code.google.com/p/tamt/wiki/RemovingTagFromSpeedDistributionTrafficFlow
	 * 
	 * We will call this table: speeddistributiontrafficflowvehiclespeed
	 * (Note that it is like the previous table, 
	 * speeddistributiontrafficflowtagvehiclespeed, but without the tag
	 * 
	 */
	public void removeTagFromSpeedDistributionTrafficFlowTagVehicleSpeed() {
		// TODO Auto-generated method stub
		
	}

	private DayTypePerYearOption getDayTypePerYearOption(
			StudyRegion currentStudyRegion) throws Exception {
		return regionDao.getDayTypePerYearOption(currentStudyRegion.getId());
	}


}
