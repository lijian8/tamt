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
import org.worldbank.transport.tamt.server.dao.SpeedDistributionReportDAO;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class SpeedDistributionReportBO {

	static Logger logger = Logger.getLogger(SpeedDistributionReportBO.class);
	
	private SpeedDistributionReportDAO dao;

	private RegionDAO regionDao;
	private TagDAO tagDao;
	
	private static SpeedDistributionReportBO singleton = null;
	public static SpeedDistributionReportBO get()
	{
		if(singleton == null)
		{
			singleton = new SpeedDistributionReportBO();
		}
		return singleton;		
	}
	
	public SpeedDistributionReportBO()
	{
		dao = SpeedDistributionReportDAO.get();
		regionDao = RegionDAO.get();
		tagDao = TagDAO.get();
	}
	
	
	public SpeedDistributionReport getSpeedDistributionReport(TagDetails tagDetails) throws Exception
	{
		logger.debug("getSpeedDistribution BO start");
		return dao.getSpeedDistributionReport(tagDetails);
	}
	
	
	public String downloadSpeedDistributionReport(String tagId) throws Exception {

		TagDetails tagDetails = new TagDetails();
		tagDetails.setId(tagId);
		
		tagDetails = regionDao.getStudyRegion(tagDetails);
		String tagName = tagDetails.getName();
		String studyRegionName = tagDetails.getRegion().getName();
		
		SpeedDistributionReport weekday;
		SpeedDistributionReport saturday;
		SpeedDistributionReport sundayHoliday;
		
		String output = "";
		
		try {
			
			/*
			weekday = dao.getSpeedDistributionReport(tagDetails, TrafficCountRecord.DAYTYPE_WEEKDAY);
			saturday = dao.getSpeedDistributionReport(tagDetails, TrafficCountRecord.DAYTYPE_SATURDAY);
			sundayHoliday = dao.getSpeedDistributionReport(tagDetails, TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
			
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
			*/
			
		} catch (Exception e) {
			// TODO throw an error the download page can handle
			logger.error(e.getMessage());
		}
		
		return output;
		
	}

	public void createSpeedDistributionReport() throws Exception {
		dao.createSpeedDistributionReport();
	}

	public void createSpeedDistributionTrafficFlowReport() throws Exception {
		dao.createSpeedDistributionTrafficFlowReport();
	}

	public SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(
			TagDetails tagDetails) throws Exception {
		logger.debug("getSpeedDistributionTrafficFlowReport BO start");
		return dao.getSpeedDistributionTrafficFlowReport(tagDetails);
	}
}
