package org.worldbank.transport.tamt.server.bo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.TagDAO;
import org.worldbank.transport.tamt.server.dao.TrafficFlowReportDAO;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class TrafficFlowReportBO {

	static Logger logger = Logger.getLogger(TrafficFlowReportBO.class);
	
	private TrafficFlowReportDAO dao;

	private RegionDAO regionDao;
	private TagDAO tagDao;
	
	private static TrafficFlowReportBO singleton = null;
	public static TrafficFlowReportBO get()
	{
		if(singleton == null)
		{
			singleton = new TrafficFlowReportBO();
		}
		return singleton;		
	}
	
	public TrafficFlowReportBO()
	{
		dao = TrafficFlowReportDAO.get();
		regionDao = RegionDAO.get();
		tagDao = TagDAO.get();
	}
	
	
	public void createTrafficFlowReport(ArrayList<TagDetails> tagDetailsList) throws Exception
	{
		// for each tag, generate the traffic flow report
		logger.debug("createTrafficFlowReport: "+tagDetailsList);

		for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
			
			TagDetails tagDetails = (TagDetails) iterator.next();
			
			logger.debug("create traffic flow reports for tag("+tagDetails.getId()+")");

			// one for each day type
			String dayType = TrafficCountRecord.DAYTYPE_WEEKDAY;
			createTrafficFlowReport(tagDetails, dayType);
			
			dayType = TrafficCountRecord.DAYTYPE_SATURDAY;
			createTrafficFlowReport(tagDetails, dayType);
			
			dayType = TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY;
			createTrafficFlowReport(tagDetails, dayType);
			
		}
		
		logger.debug("done createTrafficFlowReport for all tags for all daytypes");
		
	}
	
	private void createTrafficFlowReport(TagDetails tagDetails, String dayType) throws Exception
	{
		// delete any previous reports for this tag and daytype
		dao.deleteTrafficFlowReports(tagDetails, dayType);
		
		// generate the report (alot of BO work here)
		ArrayList<ArrayList> dayTypeReport = generateTrafficFlowReport(tagDetails, dayType);
		
		// and then save it
		saveTrafficFlowReportRawData(dayTypeReport, tagDetails, dayType);
	}
	
	private void saveTrafficFlowReportRawData(ArrayList<ArrayList> rawData, 
				TagDetails tagDetails, String dayType) throws Exception
	{
		dao.saveTraffiFlowReportRawData(rawData, tagDetails, dayType);
	}
	
	public TrafficFlowReport getTrafficFlowReport(TagDetails tagDetails, String dayType) throws Exception
	{
		return dao.getTrafficFlowReport(tagDetails, dayType);
	}
	
	
	public ArrayList<ArrayList> generateTrafficFlowReport(
			TagDetails tagDetails,
			String dayType) throws Exception
	{
		
		
		// to generate a traffic flow report, we must have a default
		// flow for this tag
		DefaultFlow query = new DefaultFlow();
		query.setTagDetails(tagDetails);
		DefaultFlow defaultFlow = regionDao.getDefaultFlow(query);
		if( defaultFlow == null)
		{
			/*
			 * We used to an error here:
			 * throw new Exception("Cannot generate report. There is no default flow \nfor the '"+tagDetails.getName()+"' tag");
			 * 
			 * But, according the jarogers, we should have a default flow of 0 for
			 * all values if there is no default flow entity for this tag.
			 * 
			 */
			defaultFlow = new DefaultFlow();
			defaultFlow.setTagDetails(tagDetails);
			
			defaultFlow.setW2Weekday("0");
			defaultFlow.setW2Saturday("0");
			defaultFlow.setW2SundayHoliday("0");
			
			defaultFlow.setW3Weekday("0");
			defaultFlow.setW3Saturday("0");
			defaultFlow.setW3SundayHoliday("0");
			
			defaultFlow.setPcWeekday("0");
			defaultFlow.setPcSaturday("0");
			defaultFlow.setPcSundayHoliday("0");
			
			defaultFlow.setTxWeekday("0");
			defaultFlow.setTxSaturday("0");
			defaultFlow.setTxSundayHoliday("0");
			
			defaultFlow.setLdcWeekday("0");
			defaultFlow.setLdcSaturday("0");
			defaultFlow.setLdcSundayHoliday("0");
			
			defaultFlow.setLdvWeekday("0");
			defaultFlow.setLdvSaturday("0");
			defaultFlow.setLdvSundayHoliday("0");
			
			defaultFlow.setHdcWeekday("0");
			defaultFlow.setHdcSaturday("0");
			defaultFlow.setHdcSundayHoliday("0");
			
			defaultFlow.setMdbWeekday("0");
			defaultFlow.setMdbSaturday("0");
			defaultFlow.setMdbSundayHoliday("0");
			
			defaultFlow.setHdbWeekday("0");
			defaultFlow.setHdbSaturday("0");
			defaultFlow.setHdbSundayHoliday("0");
			
			
		}
		
		ArrayList<ArrayList> rawData = dao.getTrafficFlowReportData(tagDetails, dayType);
		
		// create the hasValuesList from the rawData
		int hour = 0;
		ArrayList<Boolean> hasValuesList = new ArrayList<Boolean>();
		for (Iterator iterator = rawData.iterator(); iterator.hasNext();) {
			ArrayList row = (ArrayList) iterator.next();
			boolean hasValues = hasValues(row);
			hour++;
			hasValuesList.add(hasValues);
		}
		
		ArrayList<String> defaultFlowValues = new ArrayList<String>();
		
		if( dayType.equalsIgnoreCase(TrafficCountRecord.DAYTYPE_WEEKDAY))
		{
			// extract vehicle flow values from default flow for weekday only
			defaultFlowValues.add(defaultFlow.getW2Weekday());
			defaultFlowValues.add(defaultFlow.getW3Weekday());
			defaultFlowValues.add(defaultFlow.getPcWeekday());
			defaultFlowValues.add(defaultFlow.getTxWeekday());
			defaultFlowValues.add(defaultFlow.getLdvWeekday());
			defaultFlowValues.add(defaultFlow.getLdcWeekday());
			defaultFlowValues.add(defaultFlow.getHdcWeekday());
			defaultFlowValues.add(defaultFlow.getMdbWeekday());
			defaultFlowValues.add(defaultFlow.getHdbWeekday());
			
		} else if (dayType.equalsIgnoreCase(TrafficCountRecord.DAYTYPE_SATURDAY)) {
			// extract vehicle flow values from default flow for saturday only
			defaultFlowValues.add(defaultFlow.getW2Saturday());
			defaultFlowValues.add(defaultFlow.getW3Saturday());
			defaultFlowValues.add(defaultFlow.getPcSaturday());
			defaultFlowValues.add(defaultFlow.getTxSaturday());
			defaultFlowValues.add(defaultFlow.getLdvSaturday());
			defaultFlowValues.add(defaultFlow.getLdcSaturday());
			defaultFlowValues.add(defaultFlow.getHdcSaturday());
			defaultFlowValues.add(defaultFlow.getMdbSaturday());
			defaultFlowValues.add(defaultFlow.getHdbSaturday());
			
		} else {
			// extract vehicle flow values from default flow for sunday/holiday only
			defaultFlowValues.add(defaultFlow.getW2SundayHoliday());
			defaultFlowValues.add(defaultFlow.getW3SundayHoliday());
			defaultFlowValues.add(defaultFlow.getPcSundayHoliday());
			defaultFlowValues.add(defaultFlow.getTxSundayHoliday());
			defaultFlowValues.add(defaultFlow.getLdvSundayHoliday());
			defaultFlowValues.add(defaultFlow.getLdcSundayHoliday());
			defaultFlowValues.add(defaultFlow.getHdcSundayHoliday());
			defaultFlowValues.add(defaultFlow.getMdbSundayHoliday());
			defaultFlowValues.add(defaultFlow.getHdbSundayHoliday());
		}
		
		/*
		 * Find out where the BEFOREs and AFTERs start
		 */
		int firstHasValueIndex = getFirstHasValueIndex(hasValuesList);
		int lastHasValueIndex = getLastHasValueIndex(hasValuesList);
		
		/*
		 * Special Case: if firstHasValueIndex == 0 and lastHasValueIndex == 23
		 * then we never enter into either loop below, and the default values
		 * are never set for the whole list:
		 * 
		 * first loop: from 0 to 0 is never
		 * second loop: from 23+1 to < 24 is never
		 * (actually what if 23 had a value, but 0-22 were null? need to test
		 *
		 * If neither loop1 or loop2 runs, we still have ALL nulls
		 * 
		 * Question: does this case get handled in the while loop below?
		 * Answer: No, and messes a bunch of things up because we are expecting
		 * to do calculations on before- and after-values that are supposed
		 * to be there. Thus we must have a special case here.
		 */
		if( firstHasValueIndex == 0 && lastHasValueIndex == 23 )
		{
			for (int i = 0; i < lastHasValueIndex + 1; i++) {
				ArrayList<String> currentList = rawData.get(i);
				for (int j = 0; j < defaultFlowValues.size(); j++) {
					// take j and put it in j+1 of currentList
					String v = defaultFlowValues.get(j);
					
					// we can have a default flow value of null, but
					// we can't have a null in the data when we go to
					// do average calculations.
					if( v == null )
					{
						v = "0"; 
					}
					currentList.set(j+1, v);
				}
				// update the hasValuesList, because we now have values at i
				hasValuesList.set(i, true);
			}
			
		} else {
			// now, for all the BEFORE and AFTERs, sub in the defaultFlowValues
			for (int i = 0; i < firstHasValueIndex; i++) {
				ArrayList<String> currentList = rawData.get(i);
				for (int j = 0; j < defaultFlowValues.size(); j++) {
					// take j and put it in j+1 of currentList
					String v = defaultFlowValues.get(j);
					// we can have a default flow value of null, but
					// we can't have a null in the data when we go to
					// do average calculations.
					if( v == null )
					{
						v = "0"; 
					}
					currentList.set(j+1, v);
				}
				// update the hasValuesList, because we now have values at i
				hasValuesList.set(i, true);
			}		
			for (int i = lastHasValueIndex + 1; i < 24; i++) {
				ArrayList<String> currentList = rawData.get(i);
				for (int j = 0; j < defaultFlowValues.size(); j++) {
					// take j and put it in j+1 of currentList
					String v = defaultFlowValues.get(j);
					// we can have a default flow value of null, but
					// we can't have a null in the data when we go to
					// do average calculations.
					if( v == null )
					{
						v = "0"; 
					}
					currentList.set(j+1, v);
				}
				// update the hasValuesList, because we now have values at i
				hasValuesList.set(i, true);
			}
		}
		
		
		/*
		 * Now we need to operate on the in-betweens.
		 */
		//logger.debug("hasValuesList hasNull:" + hasNull(hasValuesList));
		
		/*
		 * This check turns up false if we have rawData that has onely ONE
		 * row of non-NULL data. (ie, before and afters, but the in-between
		 * is the same row)
		 * 
		 * This is the case if I assign only one count to HWY002
		 * and have only 1 cell in the default flow configured
		 * 
		 * [[0, 1, null, null, null, null, null, null, null, null], 
		 * [1, 1, null, null, null, null, null, null, null, null], 
		 * [2, 1, null, null, null, null, null, null, null, null], 
		 * [3, 1, null, null, null, null, null, null, null, null], 
		 * [4, 1, null, null, null, null, null, null, null, null], 
		 * [5, 1, null, null, null, null, null, null, null, null], 
		 * [6, 1, null, null, null, null, null, null, null, null], 
		 * [7, 1, null, null, null, null, null, null, null, null], 
		 * [8, 1, null, null, null, null, null, null, null, null], 
		 * [9, 3.00, 6.00, 9.00, 12.00, 15.00, 18.00, 21.00, 24.00, 27.00], 
		 * [10, 1, null, null, null, null, null, null, null, null], 
		 * [11, 1, null, null, null, null, null, null, null, null], 
		 * [12, 1, null, null, null, null, null, null, null, null], 
		 * [13, 1, null, null, null, null, null, null, null, null], 
		 * [14, 1, null, null, null, null, null, null, null, null], 
		 * [15, 1, null, null, null, null, null, null, null, null], 
		 * [16, 1, null, null, null, null, null, null, null, null], 
		 * [17, 1, null, null, null, null, null, null, null, null], 
		 * [18, 1, null, null, null, null, null, null, null, null], 
		 * [19, 1, null, null, null, null, null, null, null, null], 
		 * [20, 1, null, null, null, null, null, null, null, null], 
		 * [21, 1, null, null, null, null, null, null, null, null], 
		 * [22, 1, null, null, null, null, null, null, null, null], 
		 * [23, 1, null, null, null, null, null, null, null, null]]
		 */
		boolean hasNull = hasNull(hasValuesList);
		while( hasNull )
		{
			/*
			 * Get the start and end positions of the next null range
			 */
			ArrayList<Integer> range = getInBetweenRange(hasValuesList);
			//logger.debug("range=" + range);
			int startIndex = range.get(0);
			int endIndex = range.get(1);
			
			ArrayList<String> preRange = rawData.get(startIndex-1);
			//logger.debug("preRange=" + preRange);
			
			ArrayList<String> postRange = rawData.get(endIndex+1);
			//logger.debug("postRange=" + postRange);
			
			ArrayList<String> averages = calculateAverage(preRange, postRange);
			//logger.debug("averages=" + averages);
			
			//TODO: if start and end are equal, then just replace the row at t
			// that position (and check the hasNull(list again),
			// otherwise, loop over the range, which will eventual get it
			if( startIndex == endIndex )
			{
				ArrayList<String> currentList = rawData.get(startIndex);
				for (int j = 0; j < averages.size(); j++) {
					// take j and put it in j+1 of currentList
					String v = averages.get(j);
					currentList.set(j+1, v);
				}
				hasValuesList.set(startIndex, true); // update has values list
				hasNull = hasNull(hasValuesList);
				//logger.debug("currentList=" + currentList);
				
			} else {
			
				for (int i = startIndex; i < endIndex; i++) {
					ArrayList<String> currentList = rawData.get(i);
					for (int j = 0; j < averages.size(); j++) {
						// take j and put it in j+1 of currentList
						String v = averages.get(j);
						currentList.set(j+1, v);
					}
					hasValuesList.set(i, true); // update has values list
					hasNull = hasNull(hasValuesList);
					//logger.debug("currentList=" + currentList);
				}
				
			}
			//logger.debug("hasNull=" + hasNull);
		}
		
		logger.debug("rawData=" + rawData);

		return rawData;
	}
	
	private ArrayList<String> calculateAverage(ArrayList<String> a, ArrayList<String> b)
	{
		ArrayList<String> averages = new ArrayList<String>();
		// calc averages, skip the first column because it is the hour
		for (int i = 1; i < a.size(); i++) {
			String aVal = a.get(i);
			String bVal = b.get(i);
			double aD = Double.parseDouble(aVal);
			double bD = Double.parseDouble(bVal);
			double avgD = (aD + bD) / 2;
			NumberFormat formatter = new DecimalFormat("#0.00");
			String avgString = formatter.format(avgD);
			averages.add(avgString);
		}
		return averages;
	}
	
	private ArrayList<Integer> getInBetweenRange(ArrayList<Boolean> hasValuesList)
	{
		ArrayList<Integer> range = new ArrayList<Integer>();
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		
		boolean foundFirstFalse = false;
		boolean foundNextTrue = false;
		loopvalues:
		for (int i = 0; i < hasValuesList.size(); i++) {
			boolean b = hasValuesList.get(i);
			if( !foundFirstFalse )
			{
				if( !b )
				{
					foundFirstFalse = true;
					range.add(i);
					continue loopvalues;
				}
			} else {
				// we have the first false, so look for next true
				if( b )
				{
					foundNextTrue = true;
					range.add(i-1);
					break loopvalues;
				}
			}
		}
		return range;
	}

	private int getLastFalseIndex(ArrayList<Boolean> hasValuesList) {
		int index = 0;
		for (int i=hasValuesList.size()-1; i > 0; i--) {
			logger.debug("i=" + i);
			if( !hasValuesList.get(i) )
			{
				index = i;
				break;
			}
		}
		return index;
	}
	
	private int getLastHasValueIndex(ArrayList<Boolean> hasValuesList) {
		int lastHasValueIndex = hasValuesList.size()-1; // start at end of list
		for (int i=hasValuesList.size()-1; i > 0; i--) {
			if( hasValuesList.get(i) )
			{
				lastHasValueIndex = i;
				break;
			}
		}
		return lastHasValueIndex;
	}

	private int getFirstFalseIndex(ArrayList<Boolean> hasValuesList) {
		int index = 0;
		for (int i = 0; i < hasValuesList.size(); i++) {
			if( !hasValuesList.get(i) )
			{
				index = i;
				break;
			}
		}
		return index;
	}
	
	private int getFirstHasValueIndex(ArrayList<Boolean> hasValuesList) {
		int firstHasValueIndex = 0;
		for (int i = 0; i < hasValuesList.size(); i++) {
			if( hasValuesList.get(i) )
			{
				firstHasValueIndex = i;
				break;
			}
		}
		return firstHasValueIndex;
	}

	private boolean hasNull(ArrayList<Boolean> list)
	{
		boolean hasNull = false;
		for (int i = 1; i < list.size(); i++) {
			Boolean currentValue = list.get(i);
			if( !currentValue )
			{
				hasNull = true;
			}
		}
		return hasNull;
	}
	
	private boolean hasValues(ArrayList<String> row)
	{
		boolean hasValue = false;
		for (int i = 1; i < row.size(); i++) {
			String currentValue = row.get(i);
			if( currentValue != null)
			{
				hasValue = true;
			}
		}
		return hasValue;
	}

	public String downloadTrafficFlowReport(String tagId) throws Exception {

		TagDetails tagDetails = new TagDetails();
		tagDetails.setId(tagId);
		
		tagDetails = regionDao.getStudyRegion(tagDetails);
		String tagName = tagDetails.getName();
		String studyRegionName = tagDetails.getRegion().getName();
		
		TrafficFlowReport weekday;
		TrafficFlowReport saturday;
		TrafficFlowReport sundayHoliday;
		
		String output = "";
		
		try {
			weekday = dao.getTrafficFlowReport(tagDetails, TrafficCountRecord.DAYTYPE_WEEKDAY);
			saturday = dao.getTrafficFlowReport(tagDetails, TrafficCountRecord.DAYTYPE_SATURDAY);
			sundayHoliday = dao.getTrafficFlowReport(tagDetails, TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
			
			// get the date the report was created
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
			Date created = weekday.getCreated();
			logger.debug("Date created:" + created);
			String ts = format.format(created);
			logger.debug("Formatted:" + ts);
			String csvFileName = "traffic-flow-report-" + ts;
			
			StringBuffer sb = new StringBuffer();
			sb.append("REPORTDATE,REGION,TAG,DAYTYPE,HOUR,W2,W3,PC,TX,LDV,LDC,HDC,MDB,HDB\n");
			ArrayList<ArrayList> weekdayValues = weekday.getDayTypeValues();
			for (Iterator iterator = weekdayValues.iterator(); iterator
					.hasNext();) {
				ArrayList row = (ArrayList) iterator.next();
				
				// add date report was created
				sb.append(ts + ",");
				
				// add region name (need to lookup)
				sb.append(studyRegionName + ",");
				
				// add tag name (need to lookup)
				sb.append(tagName + ",");
				
				// add day type
				sb.append(TrafficCountRecord.DAYTYPE_WEEKDAY + ",");
				
				for (Iterator iter = row.iterator(); iter.hasNext();) {
					String item = (String) iter.next();
					sb.append(item);
					sb.append(",");
				}
				sb.append("\n");
				
				
			}
			
			// saturday
			ArrayList<ArrayList> saturdayValues = saturday.getDayTypeValues();
			for (Iterator iterator = saturdayValues.iterator(); iterator
					.hasNext();) {
				ArrayList row = (ArrayList) iterator.next();
				
				// add date report was created
				sb.append(ts + ",");
				
				// add region name (need to lookup)
				sb.append(studyRegionName + ",");
				
				// add tag name (need to lookup)
				sb.append(tagName + ",");
				
				// add day type
				sb.append(TrafficCountRecord.DAYTYPE_SATURDAY + ",");
				
				for (Iterator iter = row.iterator(); iter.hasNext();) {
					String item = (String) iter.next();
					sb.append(item);
					sb.append(",");
				}
				sb.append("\n");
			}
			
			// sunday
			ArrayList<ArrayList> sundayHolidayValues = sundayHoliday.getDayTypeValues();
			for (Iterator iterator = sundayHolidayValues.iterator(); iterator
					.hasNext();) {
				ArrayList row = (ArrayList) iterator.next();
				
				// add date report was created
				sb.append(ts + ",");
				
				// add region name (need to lookup)
				sb.append(studyRegionName + ",");
				
				// add tag name (need to lookup)
				sb.append(tagName + ",");
				
				// add day type
				sb.append(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY + ",");
				
				for (Iterator iter = row.iterator(); iter.hasNext();) {
					String item = (String) iter.next();
					sb.append(item);
					sb.append(",");
				}
				sb.append("\n");
			}
			
			output = sb.toString();
			
		} catch (Exception e) {
			// TODO throw an error the download page can handle
			logger.error(e.getMessage());
		}
		
		return output;
		
	}

	public String downloadTrafficFlowReportForRegion() {

		String output = "";
		
		try {
			
			output = dao.getTrafficFlowReportForRegion();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}

}
