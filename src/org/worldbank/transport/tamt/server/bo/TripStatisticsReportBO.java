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
import org.worldbank.transport.tamt.server.dao.TripStatisticsReportDAO;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByDayTypeReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionAggregateByTagReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.TripStatisticsReport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class TripStatisticsReportBO {

	static Logger logger = Logger.getLogger(TripStatisticsReportBO.class);
	
	private TripStatisticsReportDAO dao;
	
	private static TripStatisticsReportBO singleton = null;
	public static TripStatisticsReportBO get()
	{
		if(singleton == null)
		{
			singleton = new TripStatisticsReportBO();
		}
		return singleton;		
	}
	
	public TripStatisticsReportBO()
	{
		dao = TripStatisticsReportDAO.get();	
	}

	public void createTripStatisticsReport() throws Exception {
		dao.createTripStatisticsReport();
	}

	public TripStatisticsReport getSoakBinReport() throws Exception {
		
		TripStatisticsReport report = dao.getSoakBinReport();
		//TODO: add missing rows (so that we have all bin numbers from 0..9)
		return report;
	}
	
	public TripStatisticsReport getTripStatisticsReport() throws Exception {
		
		TripStatisticsReport report = dao.getTripStatisticsReport();
		//TODO: add missing rows (so that we have all bin numbers from 0..9)
		return report;
	}

	public String downloadTripStatisticsTripBinReport() throws Exception {
		return dao.downloadTripStatisticsTripBinReport();
	}

	public String downloadTripStatisticsSoakBinReport() throws Exception {
		return dao.downloadTripStatisticsSoakBinReport();
	}
	
	
}
