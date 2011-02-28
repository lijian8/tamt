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
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
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

		String output = "";
		
		try {
			
			output = dao.downloadSpeedDistributionReport(tagId);
			
		} catch (Exception e) {
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

	public void createSpeedDistributionAggregateByDayTypeReport() throws Exception {
		dao.createSpeedDistributionAggregateByDayTypeReport();
	}

	public void createSpeedDistributionAggregateByTagReport() throws Exception {
		dao.createSpeedDistributionAggregateByTagReport();
	}
	
	public SpeedDistributionTrafficFlowReport getSpeedDistributionTrafficFlowReport(
			TagDetails tagDetails) throws Exception {
		return dao.getSpeedDistributionTrafficFlowReport(tagDetails);
	}
	
	public SpeedDistributionAggregateByDayTypeReport getSpeedDistributionAggregateByDayTypeReport(
			TagDetails tagDetails) throws Exception {
		return dao.getSpeedDistributionAggregateByDayTypeReport(tagDetails);
	}	
	
	public SpeedDistributionAggregateByTagReport getSpeedDistributionAggregateByTagReport() throws Exception {
		return dao.getSpeedDistributionAggregateByTagReport();
	}

	public String downloadSpeedDistributionReportForRegion() throws Exception {

		String output = "";
		
		try {
			
			output = dao.downloadSpeedDistributionReportForRegion();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
		
	}

	public String downloadSpeedDistributionTrafficFlowReport(String tagId) throws Exception {

		String output = "";
		
		try {
			
			output = dao.downloadSpeedDistributionTrafficFlowReport(tagId);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}

	public String downloadSpeedDistributionTrafficFlowReportForRegion() throws Exception {

		String output = "";
		
		try {
			
			output = dao.downloadSpeedDistributionTrafficFlowReportForRegion();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}

	public String downloadSpeedBinAggregateByDayTypeReport(String tagId) {
		String output = "";
		
		try {
			
			output = dao.downloadSpeedBinAggregateByDayTypeReport(tagId);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}

	public String downloadSpeedBinAggregateByDayTypeReportForRegion() {
		String output = "";
		
		try {
			
			output = dao.downloadSpeedBinAggregateByDayTypeReportForRegion();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}

	public String downloadSpeedBinAggregateByTagReport() throws Exception {
		String output = "";
		
		try {
			
			output = dao.downloadSpeedBinAggregateByTagReport();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return output;
	}	
}
